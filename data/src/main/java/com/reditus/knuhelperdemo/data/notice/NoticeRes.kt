package com.reditus.knuhelperdemo.data.notice

import com.reditus.knuhelperdemo.data.common.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class NoticeModel(
    val id: Long,
    val title: String,
    val site: String,
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate,
    val url: String,
    val views: Int,
    val type: String,
)

@Serializable
data class NoticeInfoRes(
    val siteInfoList: List<SiteInfo>,
)
@Serializable
data class SiteInfo(
    val site: String,
    val siteCategoryKorean: String,
)