package com.globus.droidparty.usersession

data class StartupParams(
        val hasAuthorizedUser: Boolean = false,
        val isPresaleShown: Boolean = false
)