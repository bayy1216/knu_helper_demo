package com.reditus.knuhelperdemosettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reditus.core.system.UiState
import com.reditus.knuhelperdemo.data.notice.NoticeRepository
import com.reditus.knuhelperdemo.data.notice.SiteInfo
import com.reditus.knuhelperdemo.data.notice.SubscribeModel
import com.reditus.knuhelperdemo.data.notice.UserSubscribeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    private val _sites = MutableStateFlow<UiState<List<SiteInfo>>>(UiState.Loading)
    val sites = _sites.asStateFlow() /// 사이트 정보

    private val _userSubscribeSites = MutableStateFlow<UiState<List<SubscribeModel>>>(UiState.Loading)
    val userSubscribeSites = _userSubscribeSites.asStateFlow() /// 구독 사이트 정보

    private val _errorToast = Channel<String>(Channel.BUFFERED)
    val errorToast = _errorToast.receiveAsFlow() /// 에러 토스트


    init {
        viewModelScope.launch {
            try {
                val siteInfo = noticeRepository.getSiteInfo()
                _sites.value = UiState.Success(siteInfo.siteInfoList)
            } catch (e: Exception) {
                _sites.value = UiState.Error(e)
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
            try{
                userSubscribeRepository.addSubscribe(intent.toReq())
                _userSubscribeSites.update {
                    if(it is UiState.Success) {
                        val newList = it.data + intent.toModel()
                        UiState.Success(newList)
                    } else {
                        it
                    }
                }
            }catch (e: Exception) {
                _userSubscribeSites.value = UiState.Error(e)
            }
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
            try{
                userSubscribeRepository.updateSubscribe(intent.toReq())
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
            }catch (e: Exception) {
                _userSubscribeSites.value = UiState.Error(e)
            }
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
            try{
                userSubscribeRepository.deleteSubscribe(intent.toReq())
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
            }catch (e: Exception) {
                _userSubscribeSites.value = UiState.Error(e)
            }
        }
    }
}