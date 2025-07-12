package com.reditus.knuhelperdemosettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.reditus.core.system.UiState
import com.reditus.core.system.toUiState
import com.reditus.knuhelperdemo.data.common.ServerError
import com.reditus.knuhelperdemo.data.notice.NoticeInfoRes
import com.reditus.knuhelperdemo.data.notice.NoticeRepository
import com.reditus.knuhelperdemo.data.notice.SiteInfo
import com.reditus.knuhelperdemo.data.notice.SubscribeModel
import com.reditus.knuhelperdemo.data.notice.UserSubscribeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SiteSettingsViewModel @Inject constructor(
    private val noticeRepository: NoticeRepository,
    private val userSubscribeRepository: UserSubscribeRepository,
) :ViewModel(){
    private val _sites = MutableStateFlow<UiState<List<SiteInfo>,ServerError>>(UiState.Loading)
    val sites = _sites.asStateFlow() /// 사이트 정보

    private val _userSubscribeSites = MutableStateFlow<UiState<List<SubscribeModel>,ServerError>>(UiState.Loading)
    val userSubscribeSites = _userSubscribeSites.asStateFlow() /// 구독 사이트 정보

    private val _errorToast = Channel<String>(Channel.BUFFERED)
    val errorToast = _errorToast.receiveAsFlow() /// 에러 토스트


    init {
        viewModelScope.launch {
            val res: Either<ServerError, NoticeInfoRes> = noticeRepository.getSiteInfo()
            _sites.update {
                res.map { it.siteInfoList }.toUiState()
            }
        }
    }

    fun handleIntent(
        intent: SiteSettingsIntent,
    ) {
        when (intent) {
            is SiteSettingsIntent.AddSubscribe -> {
                addSubscribe(intent)
            }
            is SiteSettingsIntent.UpdateSubscribe -> {
                updateSubscribe(intent)
            }
            is SiteSettingsIntent.DeleteSubscribe -> {
                deleteSubscribe(intent)
            }
        }
    }

    private fun addSubscribe(
        intent: SiteSettingsIntent.AddSubscribe,
    ) {
        viewModelScope.launch {
            if(_userSubscribeSites.value is UiState.Success){ /// 구독 중 아닌 것만 추가 가능
                val currentList = (_userSubscribeSites.value as UiState.Success).data
                if(currentList.any { it.site == intent.site }) {
                    _errorToast.send("이미 구독 중인 사이트 입니다.")
                    return@launch
                }
            }
            val res: Either<ServerError, Unit> = userSubscribeRepository.addSubscribe(intent.toReq())
            res.fold(
                ifLeft = { err->
                    _userSubscribeSites.update {
                        UiState.Error(err)
                    }
                },
                ifRight = {_->
                    _userSubscribeSites.update {
                        if(it is UiState.Success) {
                            val newList = it.data + intent.toModel()
                            UiState.Success(newList)
                        } else {
                            it
                        }
                    }
                }
            )
        }
    }

    private fun updateSubscribe(
        intent: SiteSettingsIntent.UpdateSubscribe,
    ) {
        viewModelScope.launch {
            if(_userSubscribeSites.value is UiState.Success){/// 구독 중인 것만 수정 가능
                val currentList = (_userSubscribeSites.value as UiState.Success).data
                if(!currentList.any { it.site == intent.site }) {
                    _errorToast.send("존재하지 않는 사이트 입니다.")
                    return@launch
                }
            }
            val res: Either<ServerError, Unit> = userSubscribeRepository.updateSubscribe(intent.toReq())
            res.fold(
                ifLeft = { err->
                    _userSubscribeSites.update {
                        UiState.Error(err)
                    }
                },
                ifRight = { _->
                    _userSubscribeSites.update {
                        if(it is UiState.Success) {
                            val newList = it.data.map { subscribe ->
                                if(subscribe.site == intent.site) { intent.toModel() } else { subscribe }
                            }
                            UiState.Success(newList)
                        } else {
                            it
                        }
                    }
                }
            )
        }
    }

    private fun deleteSubscribe(
        intent: SiteSettingsIntent.DeleteSubscribe,
    ) {
        viewModelScope.launch {
            if(_userSubscribeSites.value is UiState.Success){ /// 구독 중인 것만 삭제 가능
                val currentList = (_userSubscribeSites.value as UiState.Success).data
                if(!currentList.any { it.site == intent.site }) {
                    _errorToast.send("존재하지 않는 사이트 입니다.")
                    return@launch
                }
            }
            val res: Either<ServerError, Unit> =  userSubscribeRepository.deleteSubscribe(intent.toReq())
            res.fold(
                ifLeft = {err->
                    _userSubscribeSites.value = UiState.Error(err)
                },
                ifRight = {_->
                    _userSubscribeSites.update {
                        if(it is UiState.Success) {
                            val newList = it.data.filter { subscribe ->
                                subscribe.site != intent.site
                            }
                            UiState.Success(newList)
                        } else {
                            it
                        }
                    }
                }
            )
        }
    }

    fun testApi() {
        viewModelScope.launch {
            userSubscribeRepository.getSubscribes()
        }
    }
}