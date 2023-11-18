package com.example.quilon_interface//package com.example.quilon_interface

import Produto
import ProdutoTypeAdapter
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient {
    private val BASE_URL = "https://quilon-api.onrender.com/"

    fun createApiService(): ApiService {
        val gson = GsonBuilder()
            .registerTypeAdapter(Produto::class.java, ProdutoTypeAdapter())
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit.create(ApiService::class.java)
    }
}

