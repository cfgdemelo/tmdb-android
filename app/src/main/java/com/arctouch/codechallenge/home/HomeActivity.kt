package com.arctouch.codechallenge.home

import android.app.SearchManager
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.home.details.DetailsFragment
import com.arctouch.codechallenge.model.Movie
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.home_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeActivity : AppCompatActivity(), ItemClick {

    private val viewModel: HomeViewModel by viewModel()
    private var isLoading: Boolean = false
    private lateinit var adapter: HomeAdapter
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)
        setupViewModelObservables()
        setupScreenBehavior()
    }

    override fun click(movie: Movie) {
        DetailsFragment.newInstance(movie).show(supportFragmentManager, TAG)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        searchView = menu?.findItem(R.id.search)?.actionView as SearchView
        initSearchView()
        return super.onCreateOptionsMenu(menu)
    }

    private fun setupScreenBehavior() {
        setSupportActionBar(toolbar)

        adapter = HomeAdapter(this)
        recyclerView.adapter = adapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val childCount = recyclerView.layoutManager?.childCount ?: 0
                    val itemCount = recyclerView.layoutManager?.itemCount ?: 0
                    val firstVisible = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                    if (!isLoading && childCount + firstVisible >= itemCount) {
                        viewModel.getUpcomingMovies(false)
                    }
                }
            }
        })
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
                    adapter.updateMoviesList(it)
                }.let { viewModel.disposable.add(it) }

        viewModel.upcomingMoviesFromSearch
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    adapter.updateMoviesList(it, true)
                }.let { viewModel.disposable.add(it) }
    }

    private fun initSearchView() {
        (getSystemService(SEARCH_SERVICE) as SearchManager).apply {
            searchView.setSearchableInfo(getSearchableInfo(componentName))
            searchView.maxWidth = Integer.MAX_VALUE
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    viewModel.searchMovie(it)
                }
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                query?.let {
                    viewModel.searchMovie(it)
                }
                return false
            }

        })
    }

    companion object {
        private const val TAG = "detailsFragment"
    }
}
