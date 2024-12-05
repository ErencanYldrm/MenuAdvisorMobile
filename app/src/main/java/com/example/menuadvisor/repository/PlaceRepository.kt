package com.example.menuadvisor.repository

import com.example.menuadvisor.api.PlaceService
import javax.inject.Inject

class PlaceRepository @Inject constructor(
    private val placeService: PlaceService
) {
    suspend fun getPlaces(
        name: String? = null,
        pageNumber: Int? = 1,
        pageSize: Int? = 10,
        district : String? = null,
        rateSort : Boolean? = null
    ) = placeService.getPlaces(
        name = name,
        pageNumber = pageNumber,
        pageSize = pageSize,
        district = district,
        rateSort = rateSort
    )

    suspend fun getPlace(
        id: Int
    ) = placeService.getPlace(
        id = id
    )
}