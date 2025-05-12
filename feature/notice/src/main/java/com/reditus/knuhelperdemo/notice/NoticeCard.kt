package com.reditus.knuhelperdemo.notice

import androidx.compose.runtime.Composable
import com.reditus.core.design.notice.NoticeCard

/**
 * [NoticeUiState]를 기반으로 디자인 시스템의 [NoticeCard]를 간편하게 호출하기 위한 래퍼 함수
 */
@Composable
internal fun NoticeCard(
    noticeUiState: NoticeUiState,
    onClick: () -> Unit,
    favoriteOnClick: ()-> Unit,
){
    NoticeCard(
        title = noticeUiState.title,
        site1st = noticeUiState.site1st,
        site2nd = noticeUiState.site2nd,
        date = noticeUiState.date,
        siteColor = noticeUiState.siteColor,
        views = noticeUiState.views,
        favorite = noticeUiState.favorite,
        onClick = onClick,
        favoriteOnClick = favoriteOnClick,
    )
}