package com.example.quilon_interface

import Produto
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("product")
    fun enviarDados(@Body produto: Produto): Call<ResponseBody>


    @GET("/search-products")
    fun buscarProdutosPorTermo(@Query("term") termo: String): Call<List<Int>>

    @GET("product-ids")
    fun listarProdutosIdsPorCategoria(@Query("category") category: String?): Call<List<Int>>

    @GET("product/{id}")
    fun obterProdutoPorId(@Path("id") productId: Int): Call<Produto>


    @GET("product/{productId}")
    fun receberProduto(@Path("productId") productId: Int): Call<Produto>

    @PUT("product/{productId}")
    fun atualizarProduto(@Path("productId") productId: Int, @Body produto: Produto): Call<ResponseBody>

    @DELETE("product/{productId}")
    fun deletarProduto(@Path("productId") productId: Int): Call<ResponseBody>
}

