package com.reditus.knuhelperdemo.data.notice

import arrow.core.Either
import com.reditus.knuhelperdemo.data.common.ServerError
import com.reditus.knuhelperdemo.data.common.unwrapToServerError
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
    suspend fun getSubscribes(): Either<ServerError, SubscribesRes> = Either.catch {
        client.get("/user/favorite-site")
            .body<SubscribesRes>()
    }.mapLeft {
        it.unwrapToServerError()
    }

    /// 구독 사이트 추가
    suspend fun addSubscribe(
        addSubscribeReq: AddSubscribeReq,
    ):Either<ServerError, Unit> = Either.catch{
        client.post("/user/favorite-site") {
            setBody(addSubscribeReq)
        }.body<Unit>()
    }.mapLeft {
        it.unwrapToServerError()
    }

    /// 구독 사이트 수정
    suspend fun updateSubscribe(
        updateSubscribeReq: AddSubscribeReq,
    ):Either<ServerError, Unit> = Either.catch{
        client.put("/user/favorite-site") {
            setBody(updateSubscribeReq)
        }.body<Unit>()
    }.mapLeft {
        it.unwrapToServerError()
    }

    /// 구독 사이트 삭제
    suspend fun deleteSubscribe(
        deleteSubscribeReq: DeleteSubscribeReq,
    ):Either<ServerError, Unit> = Either.catch{
        client.delete("/user/favorite-site") {
            setBody(deleteSubscribeReq)
        }.body<Unit>()
    }.mapLeft {
        it.unwrapToServerError()
    }

}