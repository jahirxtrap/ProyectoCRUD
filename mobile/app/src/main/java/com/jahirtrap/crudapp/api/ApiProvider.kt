package com.jahirtrap.crudapp.api

import android.content.Context

object ApiProvider {
    private var instance: RetrofitInstance? = null

    fun getApi(context: Context): ApiService {
        if (instance == null) {
            instance = RetrofitInstance(context.applicationContext)
        }
        return instance!!.api
    }

    fun reset() {
        instance = null
    }
}
