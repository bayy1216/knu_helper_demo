package com.reditus.knuhelperdemo

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

sealed interface AppEvent {
    // 각 계층별 이벤트 분리 가능
    sealed interface UserEvent : AppEvent {
        data object LoginClicked : UserEvent
        data class ProfileUpdated(val name: String) : UserEvent
    }

    sealed interface PaymentEvent : AppEvent {
        data object PurchaseInitiated : PaymentEvent
        data class RefundRequested(val orderId: String) : PaymentEvent
    }
}

// presentation/event/EventDispatcher.kt
@Singleton
open class EventDispatcher @Inject constructor(
) {
    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val _events = MutableSharedFlow<AppEvent>(
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events = _events.asSharedFlow()

    open fun emit(event: AppEvent) {
        coroutineScope.launch { _events.emit(event) }
    }
}
// Fake 구현
object FakeEventDispatcher : EventDispatcher() {
    override fun emit(event: AppEvent) {
        println("FakeEventDispatcher: ${event}")
    }
}


@EntryPoint
@InstallIn(SingletonComponent::class)
interface EventEntryPoint {
    fun eventDispatcher(): EventDispatcher
}

// Hilt Wrapper 클래스로 추상화
object HiltInjector {
    fun getEventDispatcher(context: Context): EventDispatcher {
        return try {
            EntryPointAccessors.fromApplication(
                context.applicationContext,
                EventEntryPoint::class.java
            ).eventDispatcher()
        } catch (e: IllegalStateException) {
            FakeEventDispatcher // Preview용 대체 객체
        }
    }
}

@Composable
fun TestPage(){
    val context = LocalContext.current
    val eventDispatcher = remember {
        HiltInjector.getEventDispatcher(context)
    }
    Scaffold { innerPadding ->
        Column(
            modifier = androidx.compose.ui.Modifier
                .padding(innerPadding)
        ) {
            Text(
                text = "Test Page",
                modifier = androidx.compose.ui.Modifier.padding(16.dp)
            )

            Button(
                onClick = {
                    eventDispatcher.emit(
                        AppEvent.UserEvent.LoginClicked
                    )
                },
                modifier = androidx.compose.ui.Modifier.padding(16.dp)
            ) {
                Text(text = "Click Me!")
            }
        }
    }
}


@Preview
@Composable
fun TestPagePreview() {
    TestPage()
}