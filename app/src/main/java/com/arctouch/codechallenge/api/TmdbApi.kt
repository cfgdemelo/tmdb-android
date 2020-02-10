package com.arctouch.codechallenge.api

import com.arctouch.codechallenge.model.GenreResponse
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.model.UpcomingMoviesResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {

    @GET("genre/movie/list")
    fun genres(
            @Query("api_key") apiKey: String? = API_KEY,
            @Query("language") language: String? = DEFAULT_LANGUAGE
    ): Observable<GenreResponse>

    @GET("movie/upcoming")
    fun upcomingMovies(
            @Query("api_key") apiKey: String? = API_KEY,
            @Query("language") language: String? = DEFAULT_LANGUAGE,
            @Query("page") page: Long,
            @Query("region") region: String? = DEFAULT_REGION
    ): Observable<UpcomingMoviesResponse>

    @GET("movie/{id}")
    fun movie(
            @Path("id") id: Long,
            @Query("api_key") apiKey: String? = API_KEY,
            @Query("language") language: String? = DEFAULT_LANGUAGE
    ): Observable<Movie>

    companion object {
        private const val POSTER_URL = "https://image.tmdb.org/t/p/w154"
        private const val BACKDROP_URL = "https://image.tmdb.org/t/p/w780"
        const val URL = "https://api.themoviedb.org/3/"
        const val API_KEY = "1f54bd990f1cdfb230adb312546d765d"
        const val DEFAULT_LANGUAGE = "pt-BR"
        const val DEFAULT_REGION = "BR"

        fun buildPosterUrl(posterPath: String): String = "$POSTER_URL$posterPath?api_key=$API_KEY"

        fun buildBackdropUrl(backdropPath: String): String = "$BACKDROP_URL$backdropPath?api_key=$API_KEY"
    }
}
