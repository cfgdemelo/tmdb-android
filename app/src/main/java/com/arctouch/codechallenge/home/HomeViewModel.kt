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
    var page: Long = 1

    init {
        loading.onNext(true)
        homeRepository.genres()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { loading.onNext(false) }
                .subscribe {
                    Cache.cacheGenres(it.genres)
                    getUpcomingMovies()
                    loading.onNext(false)
                }.let { disposable.add(it) }
    }

    fun getUpcomingMovies() {
        loading.onNext(true)
        homeRepository.upcomingMovies(page = page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { loading.onNext(false) }
                .subscribe {
                    val moviesWithGenres = it.results.map { movie ->
                        movie.copy(genres = Cache.genres.filter { genre -> movie.genreIds?.contains(genre.id) == true })
                    }
                    upcomingMovies.onNext(moviesWithGenres)
                    page += 1
                    loading.onNext(false)
                }.let { disposable.add(it) }
    }

}