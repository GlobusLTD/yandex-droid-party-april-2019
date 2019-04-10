package com.globus.droidparty.presentation

data class MainViewState(
        val pendingDeepLink: DeepLink? = null,
        val navigation: MainNavigation = NoneFlow
)