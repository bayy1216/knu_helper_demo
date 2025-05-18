package com.reditus.knuhelperdemo.data.favorite

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
data class FavoriteNoticeModel(
    @PrimaryKey val id: Long,
    val title: String,
    val site: String,
    val date: LocalDate,
    val url: String,
    val views: Int,
    val type: String,
    val createdAt: LocalDateTime
) {
}
