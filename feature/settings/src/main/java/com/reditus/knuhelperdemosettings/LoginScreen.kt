package com.reditus.knuhelperdemosettings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reditus.core.design.common.DefaultLayout
import com.reditus.knuhelperdemo.data.user.AuthRepository
import com.reditus.knuhelperdemo.data.user.JwtRepository
import com.reditus.knuhelperdemo.data.user.JwtToken
import com.reditus.knuhelperdemo.data.user.SignupReq
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable
fun SignUpScreen(
    settingViewmodel: SettingViewModel = hiltViewModel(),
    onClickNextScreen: () -> Unit = {},
) {
    DefaultLayout {
        SignUpScreen(
            onClickNextScreen = onClickNextScreen,
            onLogin = { uuid ->
                settingViewmodel.login(uuid)
            },
            onSignup = { uuid, fcm ->
                settingViewmodel.signup(uuid, fcm)
            }
        )
    }

}

@Composable
private fun SignUpScreen(
    onClickNextScreen: () -> Unit = {},
    onLogin: (uuid:String) -> Unit = {},
    onSignup: (uuid:String, fcm:String) -> Unit = {_,_ ->},
) {
    var uuid by remember { mutableStateOf("TEST-UUID") }
    var fcm by remember { mutableStateOf("TEST-FCM") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        TextButton(
            onClick = { onClickNextScreen() },
        ) {
            Text(
                text = "Next Screen",
            )
        }
        TextField(
            value = uuid,
            onValueChange = { uuid = it },
            label = { Text("UUID") },
        )

        TextField(
            value = fcm,
            onValueChange = { fcm = it },
            label = { Text("FCM") },
        )
        TextButton(
            onClick = { onSignup(uuid,fcm) },
        ) {
            Text(
                text = "Signup",
            )
        }
        TextButton(
            onClick = { onLogin(uuid) },
        ) {
            Text(
                text = "Login",
            )
        }
    }
}



@HiltViewModel
class SettingViewModel @Inject constructor(
    private val jwtRepository: JwtRepository,
    private val authRepository: AuthRepository,
): ViewModel(){
    fun signup(
        uuid: String,
        fcm: String,
    ){
        viewModelScope.launch {
            val jwtRes = authRepository.signup(
                SignupReq(
                    uuid = uuid,
                    fcmToken = fcm,
                )
            )
            jwtRepository.save(JwtToken(
                accessToken = jwtRes.accessToken,
                refreshToken = jwtRes.refreshToken,
            ))
        }
    }

    fun login(
        uuid: String,
    ){
        viewModelScope.launch {
            val jwtRes = authRepository.login(uuid)
            jwtRepository.save(JwtToken(
                accessToken = jwtRes.accessToken,
                refreshToken = jwtRes.refreshToken,
            ))
        }
    }
}
