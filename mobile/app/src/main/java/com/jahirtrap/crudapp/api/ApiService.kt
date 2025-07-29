package com.jahirtrap.crudapp.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("/api/login")
    fun login(@Body body: LoginRequest): Call<LoginResponse>

    @POST("/api/register")
    fun register(@Body body: RegisterRequest): Call<ApiResponse>

    @GET("/api/profile")
    fun getProfile(): Call<UserProfile>

    @GET("/api/users")
    fun getUsers(): Call<UsersResponse>

    @POST("/api/users")
    fun createUser(@Body body: CreateUserRequest): Call<ApiResponse>

    @PUT("/api/users/{id}")
    fun updateUser(@Path("id") id: Int, @Body body: UpdateUserRequest): Call<ApiResponse>

    @DELETE("/api/users/{id}")
    fun deleteUser(@Path("id") id: Int): Call<ApiResponse>

    @POST("/api/logout")
    fun logout(): Call<ApiResponse>
}
