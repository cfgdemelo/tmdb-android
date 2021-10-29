package com.arctouch.codechallenge.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.api.TmdbApi
import com.arctouch.codechallenge.base.BaseActivity
import com.arctouch.codechallenge.data.Cache
import com.arctouch.codechallenge.util.MovieImageUrlBuilder
import com.arctouch.codechallenge.util.fullscreenDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.home_activity.*

class HomeActivity : BaseActivity(), ItemClick {

    private val movieImageUrlBuilder = MovieImageUrlBuilder()

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)

        api.upcomingMovies(TmdbApi.API_KEY, TmdbApi.DEFAULT_LANGUAGE, 1, TmdbApi.DEFAULT_REGION)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
//                val moviesWithGenres = it.results.map { movie ->
//                    movie.copy(genres = Cache.genres.filter { genre -> movie.genreIds?.contains(genre.id) == true })
//                }
                    recyclerView.adapter = HomeAdapter(it.results, this)
                    progressBar.visibility = View.GONE
                }
    }

    @SuppressLint("CheckResult")
    override fun click(id: Int) {
        progressBar.visibility = View.VISIBLE
        api.movie(id = id, apiKey = TmdbApi.API_KEY, language = TmdbApi.DEFAULT_LANGUAGE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    progressBar.visibility = View.GONE
                    fullscreenDialog(R.layout.dialog_image).apply {
                        findViewById<ImageView>(R.id.ivPosterImage)?.let { imageView ->
                            Glide.with(imageView)
                                    .load(it.backdropPath?.let { movieImageUrlBuilder.buildBackdropUrl(it) })
                                    .apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                                    .into(imageView)
                        }

                        findViewById<ImageView>(R.id.ivMovieImage)?.let { imageView ->
                            Glide.with(imageView)
                                    .load(it.posterPath?.let { movieImageUrlBuilder.buildPosterUrl(it) })
                                    .apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                                    .into(imageView)
                        }

                        findViewById<TextView>(R.id.tvTitle)?.let { textView ->
                            textView.text = it.title
                        }

                        findViewById<TextView>(R.id.tvDescription)?.let { textView ->
                            textView.text = it.overview
                        }

                        findViewById<ImageButton>(R.id.ibClose)?.let {
                            it.setOnClickListener {
                                this.dismiss()
                            }
                        }
                    }
                }
    }
}
