package com.example.menuadvisor.di

import android.content.Context
import androidx.room.Room
import com.example.menuadvisor.data.local.AppDatabase
import com.example.menuadvisor.data.local.dao.FavoriteDao
import com.example.menuadvisor.data.local.dao.ProductFavoriteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        )
        .addMigrations(AppDatabase.MIGRATION_1_2)
        .build()
    }

    @Provides
    @Singleton
    fun provideFavoriteDao(database: AppDatabase): FavoriteDao {
        return database.favoriteDao()
    }

    @Provides
    @Singleton
    fun provideProductFavoriteDao(database: AppDatabase): ProductFavoriteDao {
        return database.productFavoriteDao()
    }
}