package com.example.menuadvisor.api

import com.example.menuadvisor.model.ApiResponse
import com.example.menuadvisor.model.ProductData
import com.example.menuadvisor.model.ProductRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductService {

    @GET("api/v1/Product")
    suspend fun getProducts(
        @Query("PageNumber") pageNumber: Int? = 1,
        @Query("PageSize") pageSize: Int? = 10,
        @Query("Name") name: String? = null,
        @Query("RateSort") rateSort: Boolean? = null,
    ): Response<ApiResponse<List<ProductData>>>

    @POST("api/v1/Product")
    suspend fun addProduct(
        @Body productData: ProductRequest
    ): Response<ApiResponse<Int>>


    @GET("api/v1/Product/GetProductByPlaceId/{placeId}")
    suspend fun getProductsByPlaceId(
        @Path("placeId") placeId: Int
    ): Response<ApiResponse<List<ProductData>>>

    @GET("api/v1/Product/{id}")
    suspend fun getProductById(
        @Path("id") id: Int
    ): Response<ApiResponse<ProductData>>
}