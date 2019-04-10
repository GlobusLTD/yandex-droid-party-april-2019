package com.globus.droidparty.presentation

import android.net.Uri

data class DeepLink(
        val uri: Uri,
        val timestamp: Long
)