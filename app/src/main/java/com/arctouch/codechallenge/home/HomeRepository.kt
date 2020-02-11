package com.arctouch.codechallenge.home

import com.arctouch.codechallenge.api.TmdbApi
import com.arctouch.codechallenge.model.GenreResponse
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.model.UpcomingMoviesResponse
import io.reactivex.Observable

class HomeRepository(private val api: TmdbApi) {
    fun genres(): Observable<GenreResponse> = api.genres()
    fun upcomingMovies(page: Long): Observable<UpcomingMoviesResponse> = api.upcomingMovies(page)
    fun movie(id: Long): Observable<Movie> = api.movie(id)
    fun searchMovie(query: String): Observable<UpcomingMoviesResponse> = api.searchMovie(query)
}