package com.reditus.knuhelperdemo.notice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reditus.core.system.PagingData
import com.reditus.core.system.PagingState
import com.reditus.core.system.UiState
import com.reditus.knuhelperdemo.data.notice.NoticeModel
import com.reditus.knuhelperdemo.data.notice.NoticePagingReq
import com.reditus.knuhelperdemo.data.notice.NoticeRepository
import com.reditus.knuhelperdemo.data.notice.SubscribeModel
import com.reditus.knuhelperdemo.data.notice.UserSubscribeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NoticeUiState(
    val id: Long,
    val title: String,
    val site1st: String,
    val site2nd: String,
    val date: String,
    val url: String,
    val views: Int,
){
    companion object{
        fun from(model: NoticeModel): NoticeUiState {
            return NoticeUiState(
                id = model.id,
                title = model.title,
                site1st = model.site,
                site2nd = model.type,
                date = model.date.toString(),
                url = model.url,
                views = model.views,
            )
        }
    }
}

@HiltViewModel
class NoticeViewModel @Inject constructor(
    private val noticeRepository: NoticeRepository,
    private val userSubscribeRepository: UserSubscribeRepository,
) :ViewModel(){
    private val _noticePagingState = MutableStateFlow<UiState<PagingData<NoticeUiState>>>(UiState.Loading)
    val noticePagingState = _noticePagingState.asStateFlow()

    private val _userSubscribeSites = MutableStateFlow<List<SubscribeModel>>(emptyList())

    init {
        viewModelScope.launch {
            val userSubScribes = userSubscribeRepository.getSubscribes()
            _userSubscribeSites.update {
                userSubScribes.data
            }

        }
    }

    fun getNotices(
        size: Int = 20,
        site: String? = null,
        title: String? = null,
        forceRefresh: Boolean = false,
    ){
        val currentState = _noticePagingState.value
        val currentData = (currentState as? UiState.Success)?.data

        // 중복 로딩 방지
        if (currentData?.isFetchingMore() == true) return

        val newReqPage = if (currentData == null) {
            0
        } else {
            currentData.page + 1
        }


        viewModelScope.launch {
            try{
                // 로딩 상태로 업데이트
                _noticePagingState.update {
                    if (currentData != null) {
                        if(forceRefresh){
                            UiState.Success(currentData.copy(state = PagingState.FORCE_REFRESH))
                        }else{
                            UiState.Success(currentData.copy(state = PagingState.LOADING_MORE))
                        }
                    } else {
                        it
                    }
                }

                // API 호출
                val req = NoticePagingReq(page = newReqPage , size = size, site = site, title = title)
                val response = noticeRepository.getNoticePaging(req)

                val newData = response.data.map { NoticeUiState.from(it) }
                val newState = if (response.hasNext) PagingState.HAS_NEXT else PagingState.NOT_HAS_NEXT

                // 결과 반영
                _noticePagingState.update {
                    val existing = (it as? UiState.Success)?.data
                    UiState.Success(
                        PagingData(
                            data = (existing?.data.orEmpty() + newData),
                            state = newState,
                            page = newReqPage,
                        )
                    )
                }
            }catch (e: Exception){
                _noticePagingState.update {
                    UiState.Error(e)
                }
            }

        }
    }
}