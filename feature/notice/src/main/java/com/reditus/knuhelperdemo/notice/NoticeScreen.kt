package com.reditus.knuhelperdemo.notice

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import com.reditus.knuhelperdemo.data.common.ServerError
import java.time.LocalDate

@Composable
fun NoticeScreen(
    noticeViewModel: NoticeViewModel = hiltViewModel(),
) {
    val notices = noticeViewModel.noticePagingState.collectAsStateWithLifecycle()
    DefaultLayout {
        when (val state = notices.value) {
            is UiState.Error -> {
                val errorMsg = state.error.message
                ErrorContent(
                    errorMsg,
                    onRetry = {
                        noticeViewModel.handleIntent(NoticeIntent.Refresh)
                    }
                )
            }

            is UiState.Loading -> {
                LoadingSpinner(delay = 0L)
            }

            is UiState.Success -> {
                NoticeScreen(
                    notices = state.data,
                    onIntent = {
                        noticeViewModel.handleIntent(it)
                    }
                )
            }
        }
    }
}

@Composable
private fun NoticeScreen(
    onIntent: (NoticeIntent)-> Unit = {},
    notices: PagingData<NoticeUiState, ServerError>,
) {
    val state = notices.state
    val error = state as? PagingState.Error
    NoticeListContent(
        noticeItems = notices.data,
        onLoadMore = { onIntent(NoticeIntent.LoadMore) },
        onRefresh = { onIntent(NoticeIntent.Refresh) },
        hasNext = notices.hasNext,
        error = error?.error?.message,
        onRetry = { onIntent(NoticeIntent.LoadMore) },
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun NoticeListContent(
    noticeItems: List<NoticeUiState>,
    onLoadMore: () -> Unit,
    onRefresh: () -> Unit,
    hasNext: Boolean = false,
    error: String?,
    onRetry: () -> Unit,
) {
    val lazyListState = rememberLazyListState()
    // 스크롤 위치 감지하여 추가 데이터 로드
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                if (visibleItems.size > 1 &&
                    visibleItems.last().index >= lazyListState.layoutInfo.totalItemsCount - 3
                ) {
                    onLoadMore()
                }
            }
    }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = false,
        onRefresh = onRefresh
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(
                count = noticeItems.size,
                key={ index-> noticeItems[index].id }
            ) { index ->
                NoticeCard(
                    noticeUiState = noticeItems[index],
                    onClick = { /* TODO */ },
                    favoriteOnClick = { /* TODO */ },
                )
            }

            // 추가 데이터 로딩 표시
            if (hasNext) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
            if(error != null){
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ){
                        ErrorContent(
                            error
                        ) {
                            onRetry()
                        }
                    }
                }
            }
        }

        PullRefreshIndicator(
            refreshing = false,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
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
            siteColor = Color(0xffff33ff),
            favorite = false,
        )
    )
    val pagingData = PagingData<NoticeUiState, ServerError>(
        data = data,
        state = PagingState.LoadingMore,
        page = 0,
        hasNext = false
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
