package com.arctouch.codechallenge.home

import androidx.lifecycle.ViewModel
import com.arctouch.codechallenge.data.Cache
import com.arctouch.codechallenge.model.Movie
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class HomeViewModel(private val homeRepository: HomeRepository) : ViewModel() {

    val disposable: CompositeDisposable = CompositeDisposable()
    val loading: PublishSubject<Boolean> = PublishSubject.create()
    val upcomingMovies: PublishSubject<List<Movie>> = PublishSubject.create()
    val upcomingMoviesFromSearch: PublishSubject<List<Movie>> = PublishSubject.create()
    var movie: PublishSubject<Movie> = PublishSubject.create()
    var page: Long = 1

    init {
        loading.onNext(true)
        homeRepository.genres()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { loading.onNext(false) }
                .subscribe {
                    Cache.cacheGenres(it.genres)
                    getUpcomingMovies(true)
                    loading.onNext(false)
                }.let { disposable.add(it) }
    }

    fun getUpcomingMovies(clearList: Boolean) {
        loading.onNext(true)
        homeRepository.upcomingMovies(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { loading.onNext(false) }
                .subscribe({ movies ->
                    val moviesWithGenres = movies.results.map { movie ->
                        movie.copy(genres = Cache.genres.filter { genre -> movie.genreIds?.contains(genre.id) == true })
                    }
                    if (clearList) {
                        upcomingMoviesFromSearch.onNext(moviesWithGenres)
                    } else {
                        upcomingMovies.onNext(moviesWithGenres)
                    }
                    page += 1
                    loading.onNext(false)
                }, { error ->
                    error.printStackTrace()
                    loading.onNext(false)
                }).let { disposable.add(it) }
    }

    fun getMovie(id: Long) {
        loading.onNext(true)
        homeRepository.movie(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { loading.onNext(false) }
                .subscribe({ movieGot ->
                    movie.onNext(movieGot)
                    loading.onNext(false)
                }, { error ->
                    error.printStackTrace()
                    loading.onNext(false)
                }).let { disposable.add(it) }
    }

    fun searchMovie(query: String) {
        loading.onNext(true)
        if (query.isBlank()) {
            getUpcomingMovies(true)
        } else {
            homeRepository.searchMovie(query)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError { loading.onNext(false) }
                    .subscribe({ movies ->
                        upcomingMoviesFromSearch.onNext(movies.results)
                    }, { error ->
                        error.printStackTrace()
                        loading.onNext(false)
                    }).let { disposable.add(it) }
        }
    }

}