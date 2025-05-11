package com.reditus.knuhelperdemo.notice

sealed class NoticeIntent {
    data object LoadMore: NoticeIntent()
    data object Refresh: NoticeIntent()
}