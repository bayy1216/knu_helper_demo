package com.reditus.core.system


import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.NetworkRequest.Builder
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 네트워크 연결 상태를 감지하는 인터페이스.
 *
 * [NetworkMonitor]를 구현한 클래스는
 * [isOnline] 프로퍼티를 통해 현재 온라인 상태를 확인할 수 있음.
 */
interface NetworkMonitor {
    val isOnline: Flow<Boolean>
}


/**
 * 네트워크 연결 상태를 감지하는 구현체.
 * [ConnectivityManager.NetworkCallback] 기반으로 동작함.
 */
@Singleton
class ConnectivityManagerNetworkMonitor @Inject constructor(
    @ApplicationContext private val context: Context,
) : NetworkMonitor {
    override val isOnline: Flow<Boolean> = callbackFlow {
        val connectivityManager = context.getSystemService<ConnectivityManager>()
        if (connectivityManager == null) {
            channel.trySend(false)
            channel.close()
            return@callbackFlow
        }

        /**
         * 이 콜백은 현재 활성 네트워크뿐 아니라,
         * [NetworkRequest] 조건에 부합하는 *모든* 네트워크의 상태 변화에 대해 호출됨.
         *
         * 즉, 단일 네트워크의 연결 여부만 보는 것이 아니라
         * 인터넷 사용이 가능한 네트워크가 하나라도 존재하는지 여부를 추적하는 방식임.
         *
         * 여러 개의 네트워크(예: Wi-Fi, LTE 등)가 동시에 연결될 수 있기 때문에,
         * 연결된 네트워크들을 Set으로 관리하고,
         * 그 중 하나라도 남아있다면 온라인 상태로 판단함.
         */
        val callback = object : ConnectivityManager.NetworkCallback() {

            private val networks = mutableSetOf<Network>()

            override fun onAvailable(network: Network) {
                networks += network
                channel.trySend(true)
            }

            override fun onLost(network: Network) {
                networks -= network
                channel.trySend(networks.isNotEmpty())
            }
        }

        /**
         * 인터넷 연결 가능한 네트워크 요청 설정.
         *
         * [NetworkCapabilities.NET_CAPABILITY_INTERNET]은 인터넷에 연결된 네트워크을 나타냄.
         */
        val request = Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, callback)

        // 초기 상태 전송
        channel.trySend(connectivityManager.isCurrentlyConnected())

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.conflate()

    /// 현재 네트워크가 인터넷 연결 가능한지 확인
    private fun ConnectivityManager.isCurrentlyConnected() = activeNetwork
        ?.let(::getNetworkCapabilities)
        ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
}