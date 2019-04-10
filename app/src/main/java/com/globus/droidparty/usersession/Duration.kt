package com.globus.droidparty.usersession

import java.util.concurrent.TimeUnit

data class Duration(
        val time: Long,
        val unit: TimeUnit
)