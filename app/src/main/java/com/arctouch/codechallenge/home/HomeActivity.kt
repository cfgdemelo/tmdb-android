package com.arctouch.codechallenge.home

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arctouch.codechallenge.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.home_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeActivity : AppCompatActivity() {

    private val viewModel: HomeViewModel by viewModel()
    private var isLoading: Boolean = false
    private lateinit var adapter: HomeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)
        setupViewModelObservables()
        setupScreenBehavior()
    }

    private fun setupViewModelObservables() {
        viewModel.loading.subscribe {
            if (it) {
                isLoading = true
                progressBar.visibility = View.VISIBLE
            } else {
                isLoading = false
                progressBar.visibility = View.GONE
            }
        }.let { viewModel.disposable.add(it) }

        viewModel.upcomingMovies
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    progressBar.visibility = View.GONE
                    adapter.updateMoviesList(it)
                }.let { viewModel.disposable.add(it) }
    }

    private fun setupScreenBehavior() {
        adapter = HomeAdapter()
        recyclerView.adapter = adapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val childCount = recyclerView.layoutManager?.childCount ?: 0
                    val itemCount = recyclerView.layoutManager?.itemCount ?: 0
                    val firstVisible = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                    if (!isLoading && childCount + firstVisible >= itemCount) {
                        viewModel.getUpcomingMovies()
                    }
                }
            }
        })
    }
}
