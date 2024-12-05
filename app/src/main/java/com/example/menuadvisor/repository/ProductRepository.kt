package com.example.menuadvisor.repository

import com.example.menuadvisor.api.ProductService
import com.example.menuadvisor.model.ProductRequest
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val productService: ProductService
) {
    suspend fun getProducts(
        name: String,
        pageNumber: Int? = 1,
        pageSize: Int? = 10,
        rateSort: Boolean? = null
    ) = productService.getProducts(
        name = name,
        pageNumber = pageNumber,
        pageSize = pageSize,
        rateSort = rateSort
    )

    suspend fun getProductsByPlaceId(
        placeId: Int
    ) = productService.getProductsByPlaceId(
        placeId = placeId
    )

    suspend fun addProduct(
        name: String,
        description: String,
        image: String,
        rate: Int,
        price: Int,
        placeId: Int
    ) = productService.addProduct(
        ProductRequest(
            name = name,
            description = description,
            image = image,
            rate = rate,
            price = price,
            placeId = placeId
        )
    )

    suspend fun getProduct(
        id: Int
    ) = productService.getProductById(
        id = id
    )
}