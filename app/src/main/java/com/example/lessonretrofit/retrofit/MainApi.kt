package com.example.lessonretrofit.retrofit

import retrofit2.http.*

interface MainApi {
    @GET("auth/products/{id}")
    suspend fun getProductById(@Path("id") id:Int): Product

    @POST("auth/login")
    suspend fun auth(@Body authRequest: AuthRequest): User

    @GET("auth/products")
    suspend fun getAllProducts():Products

    @GET("auth/products/search")
    suspend fun getProductsByName(@Query ("q") name:String):Products

    @Headers("Content_Type: application/json")
    @GET("auth/products/search")
    suspend fun getProductsByNameAuth(@Header("Authorization") token: String,
                                      @Query ("q") name:String):Products

}