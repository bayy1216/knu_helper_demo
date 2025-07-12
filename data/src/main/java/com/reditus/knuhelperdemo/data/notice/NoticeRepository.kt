package com.reditus.knuhelperdemo.data.notice

import arrow.core.Either
import com.reditus.knuhelperdemo.data.common.PagingRes
import com.reditus.knuhelperdemo.data.common.ServerError
import com.reditus.knuhelperdemo.data.common.unwrapToServerError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoticeRepository @Inject constructor(
    private val client: HttpClient,
) {
    /// 공지사항 목록 페이징
    suspend fun getNoticePaging(
        noticePagingReq: NoticePagingReq,
    ): Either<ServerError, PagingRes<NoticeModel>> = Either.catch {
        client.get("/notice") {
            parameter("page", noticePagingReq.page)
            parameter("size", noticePagingReq.size)
            if (noticePagingReq.site != null) {
                parameter("site", noticePagingReq.site)
            }
            if (noticePagingReq.title != null) {
                parameter("title", noticePagingReq.title)
            }
        }.body<PagingRes<NoticeModel>>()
    }.mapLeft {
        it.unwrapToServerError()
    }

    /// 공지 사이트 목록 조회
    suspend fun getSiteInfo(): Either<ServerError, NoticeInfoRes> = Either.catch {
        client.get("/notice/site-info")
            .body<NoticeInfoRes>()
    }.mapLeft {
        it.unwrapToServerError()
    }
}