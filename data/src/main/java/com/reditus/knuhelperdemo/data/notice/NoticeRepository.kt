package com.reditus.knuhelperdemo.data.notice

import com.reditus.knuhelperdemo.data.common.PagingRes
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoticeRepository @Inject constructor(
    private val client: HttpClient,
){
    /// 공지사항 목록 페이징
    suspend fun getNoticePaging(
        noticePagingReq: NoticePagingReq,
    ): PagingRes<NoticeModel> {
        return client.get("/notice") {
            parameter("page", noticePagingReq.page)
            parameter("size", noticePagingReq.size)
            if(noticePagingReq.site != null) {
                parameter("site", noticePagingReq.site)
            }
            if(noticePagingReq.title != null) {
                parameter("title", noticePagingReq.title)
            }
        }.body()
    }

    /// 공지 사이트 목록 조회
    suspend fun getSiteInfo(): NoticeInfoRes {
        return client.get("/notice/site-info").body()
    }
}