package com.example.menuadvisor.di

import com.example.menuadvisor.api.AuthService
import com.example.menuadvisor.repository.AuthRepository
//import com.example.menuadvisor.util.Constants.Companion.BASE_URL
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
        return Retrofit.Builder().baseUrl("https://menuadv.azurewebsites.net/")
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


//    @Singleton
//    @Provides
//    fun providePlaceService(retrofit: Retrofit): PlaceService {
//        return retrofit.create(PlaceService::class.java)
//    }
//
//
//    @Singleton
//    @Provides
//    fun providePlaceRepository(placeService: PlaceService): PlaceRepository {
//        return PlaceRepository(placeService)
//    }
//
//    @Singleton
//    @Provides
//    fun provideProductService(retrofit: Retrofit): ProductService {
//        return retrofit.create(ProductService::class.java)
//    }
//
//    @Singleton
//    @Provides
//    fun provideProductRepository(productService: ProductService): ProductRepository {
//        return ProductRepository(productService)
//    }
//
//    @Singleton
//    @Provides
//    fun provideReviewService(retrofit: Retrofit): ReviewService {
//        return retrofit.create(ReviewService::class.java)
//    }
//
//    @Singleton
//    @Provides
//    fun provideReviewRepository(reviewService: ReviewService): ReviewRepository {
//        return ReviewRepository(reviewService)
//    }
}