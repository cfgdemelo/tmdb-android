package com.arctouch.codechallenge.di

import com.arctouch.codechallenge.api.TmdbApi
import com.arctouch.codechallenge.home.HomeRepository
import com.arctouch.codechallenge.home.HomeViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.lang.Exception
import java.util.concurrent.TimeUnit

val appModule = module {
    single { createWebService<TmdbApi>(TmdbApi.URL) }
    factory { HomeRepository(get()) }
    viewModel { HomeViewModel(get()) }
}

inline fun <reified T> createWebService(url: String): T {
    return Retrofit.Builder()
            .baseUrl(url)
            .client(providesOkHttpClient())
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(T::class.java)
}

fun providesOkHttpClient(): OkHttpClient {
    try {
        return OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS).apply {
                    HttpLoggingInterceptor().let {
                        it.level = HttpLoggingInterceptor.Level.BODY
                        this.addInterceptor(it)
                    }
                }.build()
    } catch (exception: Exception) {
        exception.printStackTrace()
        throw Exception(exception)
    }
}