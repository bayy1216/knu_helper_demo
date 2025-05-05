package com.reditus.knuhelperdemo.data.notice

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSubscribeRepository @Inject constructor(
    private val client: HttpClient,
){
    /// 구독 사이트 조회
    suspend fun getSubscribes(): SubscribesRes {
        return client.get("/user/favorite-site").body()
    }

    /// 구독 사이트 추가
    suspend fun addSubscribe(
        addSubscribeReq: AddSubscribeReq,
    ) {
        return client.post("/user/favorite-site") {
            setBody(addSubscribeReq)
        }.body()
    }

    /// 구독 사이트 수정
    suspend fun updateSubscribe(
        updateSubscribeReq: AddSubscribeReq,
    ) {
        return client.put("/user/favorite-site") {
            setBody(updateSubscribeReq)
        }.body()
    }

    /// 구독 사이트 삭제
    suspend fun deleteSubscribe(
        deleteSubscribeReq: DeleteSubscribeReq,
    ) {
        return client.delete("/user/favorite-site") {
            setBody(deleteSubscribeReq)
        }.body()
    }

}