package com.example.menuadvisor.di

import com.example.menuadvisor.api.AuthService
import com.example.menuadvisor.api.FavoritesService
import com.example.menuadvisor.api.PlaceService
import com.example.menuadvisor.api.ProductService
import com.example.menuadvisor.api.ReviewService
import com.example.menuadvisor.data.UserPreferences
import com.example.menuadvisor.repository.AuthRepository
import com.example.menuadvisor.repository.FavoritesRepository
import com.example.menuadvisor.repository.PlaceRepository
import com.example.menuadvisor.repository.ProductRepository
import com.example.menuadvisor.repository.ReviewRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkingModule {

    @Singleton
    @Provides
    fun provideHttpLoggerInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }

    @Singleton
    @Provides
    fun provideHttpClint(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS).addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideConverterFactory(): GsonConverterFactory{
        return GsonConverterFactory.create()
    }

    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit{
        return Retrofit.Builder().baseUrl("https://menuadvmobile-hubvgncqeng7gye8.italynorth-01.azurewebsites.net/")
            .addConverterFactory(gsonConverterFactory)
            .client(okHttpClient)
            .build()
    }



    @Singleton
    @Provides
    fun provideAuthService(retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Singleton
    @Provides
    fun provideAuthRepository(authService: AuthService): AuthRepository {
        return AuthRepository(authService)
    }

    @Singleton
    @Provides
    fun provideFavoritesService(retrofit: Retrofit): FavoritesService {
        return retrofit.create(FavoritesService::class.java)
    }

    @Singleton
    @Provides
    fun provideFavoritesRepository(favoritesService: FavoritesService): FavoritesRepository {
        return FavoritesRepository(favoritesService)
    }


    @Singleton
    @Provides
    fun providePlaceService(retrofit: Retrofit): PlaceService {
        return retrofit.create(PlaceService::class.java)
    }


    @Singleton
    @Provides
    fun providePlaceRepository(placeService: PlaceService): PlaceRepository {
        return PlaceRepository(placeService)
    }

    @Singleton
    @Provides
    fun provideProductService(retrofit: Retrofit): ProductService {
        return retrofit.create(ProductService::class.java)
    }

    @Singleton
    @Provides
    fun provideProductRepository(productService: ProductService): ProductRepository {
        return ProductRepository(productService)
    }

    @Singleton
    @Provides
    fun provideReviewService(retrofit: Retrofit): ReviewService {
        return retrofit.create(ReviewService::class.java)
    }

    @Singleton
    @Provides
    fun provideReviewRepository(
        reviewService: ReviewService,
        userPreferences: UserPreferences
    ): ReviewRepository {
        return ReviewRepository(reviewService, userPreferences)
    }
}