package com.reditus.knuhelperdemo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reditus.core.system.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

//    @Test
    fun test() {
        val testFlow = TestFlow()
        runBlocking {
            testFlow.add()
            launch {
                testFlow.state.collect {
                    println("State: $it")
                }
            }

            launch {
                testFlow.state.collectLatest {
                    delay(1000)
                    println("Last State: $it")
                }
            }
            launch {
                testFlow.state.buffer().collect {
                    println("Buffer State: $it")
                    delay(165)
                }
            }

            launch {
                testFlow.getState().conflate().collect {
                    println("Conflate State: $it")
                    delay(200)
                }
            }
            launch {
                testFlow.getState().collect {
                    println("Flow State: $it")
                    delay(300)
                }
            }


            delay(10000)
        }
    }

//    @Test
    fun repoTest() {
        val repo = ItemRepository(
            client = ItemApiClient(),
            repositoryScope = CoroutineScope(Dispatchers.IO)
        )
        runBlocking {
            println("Start")
            val job1 = launch {
                repo.getItemCountFlow().collect {
                    println("Item Count: $it")
                }
            }
            launch {
                for (i in 0..10) {
                    delay(200)
                    repo.setCount(i)
                    println("Set Count: $i")
                }
            }
            val job2 = launch {
                repo.getItemCountFlow().collect {
                    println("Item Count 2: $it")
                }
            }

            // 5초 후에 job1을 취소합니다.
            delay(5000)
            job1.cancel()
            println("Job1 Cancelled")
            // 5초 후에 job2를 취소합니다.
            delay(1000)
            job2.cancel()
            println("Job2 Cancelled")
            delay(5100)
            launch {
                println("Job3 Start")
                repo.getItemCountFlow().collect {
                    println("Item Count 3: $it")
                }
            }
        }
    }
}

class ItemApiClient {
    private var _count = 100
    suspend fun getItemCount(): Int {
        delay(100)
        println("Get Item Count called From API")
        return _count
    }

    suspend fun setItemCount(count: Int) {
        delay(100)
        this._count = count
    }
}

class ItemRepository(
    private val client: ItemApiClient,
    repositoryScope: CoroutineScope,
) {
    private val _itemCountFlow = MutableSharedFlow<Int>(
        replay = 0, // 과거 값을 다시 보내지 않음
        extraBufferCapacity = 0, // 버퍼 없음
        onBufferOverflow = BufferOverflow.SUSPEND // 버퍼가 꽉 차면 발행측 일시 중단 (기본값)
    )
    private val externalCountFlow: SharedFlow<Int> by lazy {
        merge(
            flow { emit(client.getItemCount()) },
            _itemCountFlow
        ).shareIn(
            scope = repositoryScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            replay = 1
        )
    }

    fun getItemCountFlow(): Flow<Int> {
        return externalCountFlow // 외부에 공개할 Flow
    }

    suspend fun setCount(count: Int) {
        client.setItemCount(count)
        _itemCountFlow.emit(count) // itemCountFlow에 새로운 값 발행
    }
}

data class CountUiState(
    val count: Int,
){
    companion object {
        fun from(count: Int): CountUiState {
            return CountUiState(count)
        }
    }
}
//
//class ItemViewModel(
//    private val itemRepository: ItemRepository,
//) : ViewModel() {
//    val itemUiState: StateFlow<UiState<CountUiState>> = itemRepository.getItemCountFlow()
//        .map<Int, UiState<CountUiState>> { UiState.Success(CountUiState.from(it)) }
//        .catch { e -> emit(UiState.Error(e)) }
//        .stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(5000),
//            initialValue = UiState.Loading
//        )
//
//
//    private val _sideEffect = Channel<String>(Channel.BUFFERED)
//    val sideEffect = _sideEffect.receiveAsFlow()
//
//
//    fun setCount(count: Int) {
//        viewModelScope.launch {
//            try {
//                itemRepository.setCount(count)
//            } catch (e: Exception) {
//                _sideEffect.send("Error: ${e.message}")
//            }
//        }
//    }
//}


class TestFlow {
    val state = MutableStateFlow(0)


    fun getState(): Flow<Int> {
        return state
    }

    suspend fun setState(value: Int) {
        state.emit(value)
    }

    fun add() {
        CoroutineScope(Dispatchers.IO).launch {
            for (i in 0..10) {
                delay(100)
                state.emit(i)
                println("----Emit: $i-----")
            }

        }
    }
}