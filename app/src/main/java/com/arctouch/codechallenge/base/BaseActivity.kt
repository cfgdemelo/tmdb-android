package com.arctouch.codechallenge.base

import androidx.appcompat.app.AppCompatActivity
import com.arctouch.codechallenge.api.TmdbApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

abstract class BaseActivity : AppCompatActivity() {

    protected val api: TmdbApi = Retrofit.Builder()
            .baseUrl(TmdbApi.URL)
            .client(OkHttpClient.Builder()
                    .connectTimeout(5L, TimeUnit.SECONDS)
                    .callTimeout(5L, TimeUnit.SECONDS)
                    .writeTimeout(5L, TimeUnit.SECONDS)
                    .readTimeout(5L, TimeUnit.SECONDS)
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }).build())
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(TmdbApi::class.java)
}
