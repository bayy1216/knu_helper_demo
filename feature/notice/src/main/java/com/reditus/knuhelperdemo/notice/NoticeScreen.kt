package com.reditus.knuhelperdemo.notice

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reditus.core.design.KnuTheme
import com.reditus.core.design.common.DefaultLayout
import com.reditus.core.design.common.ErrorContent
import com.reditus.core.design.common.LoadingSpinner
import com.reditus.core.design.notice.NoticeCard
import com.reditus.core.system.PagingData
import com.reditus.core.system.PagingState
import com.reditus.core.system.UiState
import java.time.LocalDate

@Composable
fun NoticeScreen(
    noticeViewModel: NoticeViewModel = hiltViewModel(),
) {
    val notices = noticeViewModel.noticePagingState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        noticeViewModel.getNotices()
    }
    DefaultLayout {
        when(val state = notices.value){
            is UiState.Error -> {
                val errorMsg = state.exception.message
                ErrorContent(
                    errorMsg,
                    onRetry =  {
                        noticeViewModel.getNotices()
                    }
                )
            }
            UiState.Loading -> {
                LoadingSpinner()
            }
            is UiState.Success ->{
                NoticeScreen(
                    notices = state.data,
                    onRefresh = {
                        noticeViewModel.getNotices()
                    }
                )
            }
        }
    }
}

@Composable
private fun NoticeScreen(
    onRefresh: () -> Unit = {},
    notices: PagingData<NoticeUiState>,
){
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        TextButton(
            onClick = onRefresh,
        ) {
            Text("새로고침")
        }
        notices.data.forEach {
            NoticeCard(
                title = it.title,
                site1st = it.site1st,
                site2nd = it.site2nd,
                date = it.date,
                siteColor = Color(0xFF6200EE),
                views = it.views,
                favorite = false,
                onClick = { /* TODO */ },
                favoriteOnClick = { /* TODO */ },
            )
        }
    }
}


@Preview
@Composable
private fun NoticeScreenPreview() {
    val data = listOf(
        NoticeUiState(
            id = 1,
            title = "Title",
            site1st = "경북대",
            site2nd = "경북대-학사공지",
            date = LocalDate.of(2023, 10, 1).toString(),
            url = "https://example.com",
            views = 100,
        )
    )
    val pagingData = PagingData(
        data = data,
        state = PagingState.NOT_HAS_NEXT,
        page = 0
    )
    KnuTheme {
        NoticeScreen(
            notices = pagingData,
        )
    }
}

@Preview(widthDp = 540)
@Composable
private fun NoticeCardPreview() {
    KnuTheme {
        NoticeCard(
            modifier = Modifier.fillMaxWidth(),
            title = "제목",
            site1st = "경북대",
            site2nd = "경북대-학사공지",
            siteColor = Color.Red,
            date = "2023-10-10",
            views = 100,
            favorite = false,
            onClick = {},
            favoriteOnClick = {}
        )
    }
}
