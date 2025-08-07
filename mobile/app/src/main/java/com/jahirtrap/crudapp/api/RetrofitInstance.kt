package com.jahirtrap.crudapp.api

import android.content.Context
import androidx.preference.PreferenceManager
import com.jahirtrap.crudapp.R
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance(context: Context) {
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val baseUrl = preferences.getString(
        context.getString(R.string.api_url_key),
        context.getString(R.string.api_url_default)
    ) ?: "http://localhost:5000"
    private val cookieJar = SessionCookieJar()
    private val logging =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    private val client = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }
        .addInterceptor(logging)
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
