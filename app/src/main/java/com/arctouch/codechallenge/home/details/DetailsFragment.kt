package com.arctouch.codechallenge.home.details

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.api.TmdbApi
import com.arctouch.codechallenge.home.HomeViewModel
import com.arctouch.codechallenge.model.Movie
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_details_bottom_sheet.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class DetailsFragment : BottomSheetDialogFragment() {

    private lateinit var movie: Movie
    private lateinit var disposable: Disposable
    private val viewModel: HomeViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            movie = it.getSerializable(MOVIE) as Movie
            viewModel.getMovie(movie.id.toLong())
        }
    }

    override fun onStop() {
        super.onStop()
        disposable.dispose()
    }

    override fun getTheme(): Int {
        return R.style.Theme_MaterialComponents_Light_BottomSheetDialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_details_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(this)
                .load(movie.backdropPath?.let { TmdbApi.buildBackdropUrl(it) })
                .centerCrop()
                .apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                .into(ivBackdrop)
        tvTitle.text = movie.title
        tvOverview.text = movie.overview
    }

    override fun onResume() {
        super.onResume()
        setupBottomSheet()
        setupViewModelObservables()
    }

    private fun setupBottomSheet() {
        BottomSheetBehavior.from(view?.parent as View).apply {
            peekHeight = resources.getDimensionPixelSize(R.dimen.bottom_sheet_peek_height)
            isHideable = true
        }

        ccBottomSheet.layoutParams.apply {
            DisplayMetrics().let {
                activity?.windowManager?.defaultDisplay?.getMetrics(it)
                this.height = it.heightPixels
                ccBottomSheet.layoutParams = this
            }
        }
    }

    private fun setupViewModelObservables() {
        viewModel.movie.subscribe {
            showLeftInfo(it)
        }.let { disposable = it }
    }

    private fun showLeftInfo(movie: Movie) {
        movie.genres?.map { genre ->
            context?.let {
                Chip(it).apply {
                    text = genre.name
                    cgGenres.addView(this as View)
                }
            }
        }
        Glide.with(this)
                .load(movie.posterPath?.let { TmdbApi.buildPosterUrl(it) })
                .centerCrop()
                .apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                .into(ivPoster)

        movie.voteAverage?.let { average ->
            arbStars.rating = average.toFloat() / 2
        }

        movie.releaseDate?.let { date ->
            tvReleaseDate.text = date.format(Locale.getDefault())
        }

        movie.originalLanguage?.let { lang ->
            tvLanguage.text = lang
        }
    }

    companion object {
        private const val MOVIE = "movie"
        fun newInstance(movie: Movie) = DetailsFragment().apply {
            arguments = Bundle().apply {
                putSerializable(MOVIE, movie)
            }
        }
    }
}