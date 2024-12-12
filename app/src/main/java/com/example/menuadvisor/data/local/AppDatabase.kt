package com.example.menuadvisor.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.menuadvisor.data.local.dao.FavoriteDao
import com.example.menuadvisor.data.local.dao.ProductFavoriteDao
import com.example.menuadvisor.data.local.entity.FavoriteEntity
import com.example.menuadvisor.data.local.entity.ProductFavoriteEntity

@Database(
    entities = [
        FavoriteEntity::class,
        ProductFavoriteEntity::class
    ],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun productFavoriteDao(): ProductFavoriteDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `product_favorites` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `userId` TEXT NOT NULL,
                        `productId` INTEGER NOT NULL,
                        `timestamp` INTEGER NOT NULL
                    )
                """)
            }
        }
    }
}