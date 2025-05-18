package com.reditus.knuhelperdemo.data.favorite

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteNoticeDao {
    @Query("SELECT * FROM FavoriteNoticeModel order by createdAt desc")
    fun getFavoriteNoticeFlow(): Flow<List<FavoriteNoticeModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favoriteNotice: FavoriteNoticeModel)

    @Query("DELETE FROM FavoriteNoticeModel WHERE id = :id")
    suspend fun deleteById(id:Long)
}