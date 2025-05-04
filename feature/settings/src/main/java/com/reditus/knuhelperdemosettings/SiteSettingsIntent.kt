package com.reditus.knuhelperdemosettings

import com.reditus.knuhelperdemo.data.notice.AddSubscribeReq
import com.reditus.knuhelperdemo.data.notice.DeleteSubscribeReq
import com.reditus.knuhelperdemo.data.notice.SubscribeModel

sealed class SiteSettingsIntent {
    data class AddSubscribe(
        val site: String,
        val color: String,
        val isAlarm: Boolean,
    ) : SiteSettingsIntent() {
        fun toReq() = AddSubscribeReq(
            site = site,
            color = color,
            alarm = isAlarm,
        )

        fun toModel() = SubscribeModel(
            site = site,
            color = color,
            isAlarm = isAlarm,
        )

    }

    data class UpdateSubscribe(
        val site: String,
        val color: String,
        val isAlarm: Boolean,
    ) : SiteSettingsIntent() {
        fun toReq() = AddSubscribeReq(
            site = site,
            color = color,
            alarm = isAlarm,
        )

        fun toModel() = SubscribeModel(
            site = site,
            color = color,
            isAlarm = isAlarm,
        )

    }

    data class DeleteSubscribe(
        val site: String,
    ) : SiteSettingsIntent() {
        fun toReq() = DeleteSubscribeReq(
            site = site,
        )
    }
}