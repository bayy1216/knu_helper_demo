package com.reditus.knuhelperdemo.data.common.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.reditus.knuhelperdemo.data.favorite.FavoriteNoticeDao
import com.reditus.knuhelperdemo.data.favorite.FavoriteNoticeModel

@Database(
    entities = [FavoriteNoticeModel::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class KnuDatabase : RoomDatabase() {
    abstract fun favoriteNoticeDao(): FavoriteNoticeDao
}