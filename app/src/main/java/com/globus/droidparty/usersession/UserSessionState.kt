package com.globus.droidparty.usersession

sealed class UserSessionState

object Closed : UserSessionState()

object Splash : UserSessionState()

object Presale : UserSessionState()

data class LoginRequired(val isAfterLogout: Boolean) : UserSessionState()

data class PinCodeRequired(val isAfterExpired: Boolean) : UserSessionState()

object Active : UserSessionState()