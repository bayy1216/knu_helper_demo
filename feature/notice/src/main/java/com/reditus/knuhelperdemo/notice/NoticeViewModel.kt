package com.reditus.knuhelperdemo.notice

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.reditus.core.system.PagingData
import com.reditus.core.system.PagingState
import com.reditus.core.system.UiState
import com.reditus.core.system.toUiState
import com.reditus.knuhelperdemo.data.common.PagingRes
import com.reditus.knuhelperdemo.data.common.ServerError
import com.reditus.knuhelperdemo.data.notice.NoticeModel
import com.reditus.knuhelperdemo.data.notice.NoticePagingReq
import com.reditus.knuhelperdemo.data.notice.NoticeRepository
import com.reditus.knuhelperdemo.data.notice.SubscribeModel
import com.reditus.knuhelperdemo.data.notice.SubscribesRes
import com.reditus.knuhelperdemo.data.notice.UserSubscribeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class NoticeUiState(
    val id: Long,
    val title: String,
    val siteColor: Color,
    val site1st: String,
    val site2nd: String,
    val date: String,
    val url: String,
    val views: Int,
    val favorite: Boolean,
){
    companion object{
        fun from(model: NoticeModel): NoticeUiState {
            return NoticeUiState(
                id = model.id,
                title = model.title,
                site1st = model.site,
                siteColor = Color(0xFF6200EE),
                site2nd = model.type,
                date = model.date.toString(),
                url = model.url,
                views = model.views,
                favorite = false,
            )
        }
    }
}

@HiltViewModel
class NoticeViewModel @Inject constructor(
    private val noticeRepository: NoticeRepository,
    private val userSubscribeRepository: UserSubscribeRepository,
) :ViewModel(){
    private val _noticePagingState = MutableStateFlow<UiState<PagingData<NoticeUiState, ServerError>,ServerError>>(UiState.Loading)
    val noticePagingState = _noticePagingState.asStateFlow()

    private val _userSubscribeSites = MutableStateFlow<UiState<List<SubscribeModel>,ServerError>>(UiState.Loading)

    init {
        viewModelScope.launch {
            val result: Either<ServerError, SubscribesRes> = userSubscribeRepository.getSubscribes()
            _userSubscribeSites.update {
                result.map{ it.data }.toUiState()
            }
        }

        loadData(page = 0, size = 20, forceRefresh = true)
    }

    private var currentSearchQuery: String? = null
    private var currentSiteFilter: String? = null


    fun handleIntent(intent: NoticeIntent){
        when(intent){
            NoticeIntent.LoadMore -> loadMore()
            NoticeIntent.Refresh -> refresh()
        }
    }


    private fun loadMore() {
        val currentData: PagingData<NoticeUiState, ServerError> = (_noticePagingState.value as? UiState.Success)?.data ?: return
        if (currentData.state == PagingState.LoadingMore) return

        loadData(
            page = currentData.page + 1,
            size = 20,
            forceRefresh = false
        )
    }

    private fun refresh() {
        loadData(page = 0, size = 20, forceRefresh = true)
    }

    private fun loadData(
        page: Int,
        size: Int,
        forceRefresh: Boolean
    ) {
        viewModelScope.launch {
            // 상태 업데이트
            updatePagingState(
                newState = if (forceRefresh) PagingState.ForceRefreshing else PagingState.LoadingMore,
                newPage = page,
                keepData = !forceRefresh
            )

            // API 호출
            val req = NoticePagingReq(
                page = page,
                size = size,
                site = currentSiteFilter,
                title = currentSearchQuery
            )
            val response: Either<ServerError, PagingRes<NoticeModel>> = noticeRepository.getNoticePaging(req)

            response.fold(
                ifLeft = { err->
                    if(page == 0){// 첫 페이지 로드 실패 시 기존 데이터 유지하지 않음
                        _noticePagingState.update{
                            UiState.Error(err)
                        }
                    }else{
                        updatePagingState(
                            newState = PagingState.Error(err),
                            newPage = page,
                            keepData = true
                        )
                    }
                },
                ifRight = { res ->
                    // 결과 처리
                    val newData = res.data.map { NoticeUiState.from(it) }

                    updatePagingState(
                        newData = if (page == 0) newData else {
                            val currentData = (_noticePagingState.value as? UiState.Success)?.data?.data ?: emptyList()
                            currentData + newData
                        },
                        newPage = page,
                        hasNext = res.hasNext,
                        newState = PagingState.Success
                    )
                }
            )
        }
    }

    private fun updatePagingState(
        newData: List<NoticeUiState>? = null,
        newPage: Int? = null,
        hasNext: Boolean? = null,
        newState: PagingState<ServerError>? = null,
        keepData: Boolean = true
    ) {
        _noticePagingState.update { currentState ->
            val currentData = (currentState as? UiState.Success)?.data

            val mergedData = when {
                newData != null -> newData
                keepData && currentData != null -> currentData.data
                else -> emptyList()
            }

            val mergedPage = newPage ?: currentData?.page ?: 0
            val mergedHasNext = hasNext ?: currentData?.hasNext ?: false
            val mergedState = newState ?: currentData?.state ?: PagingState.Success

            UiState.Success(
                PagingData(
                    data = mergedData,
                    page = mergedPage,
                    hasNext = mergedHasNext,
                    state = mergedState
                )
            )
        }
    }
}