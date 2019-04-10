package com.globus.droidparty.usersession

typealias UserSessionPartialState = (UserSessionState) -> UserSessionState

object UserSessionPartialStates {

    fun started(): UserSessionPartialState = { Splash }

    fun startupParamsLoaded(startupParams: StartupParams): UserSessionPartialState = {
        val (hasAuthorizedUser, isPresaleShown) = startupParams
        when {
            hasAuthorizedUser -> PinCodeRequired(isAfterExpired = false)
            !isPresaleShown -> Presale
            else -> LoginRequired(isAfterLogout = false)
        }
    }

    fun presaleCompleted(): UserSessionPartialState = { LoginRequired(isAfterLogout = false) }

    fun loginCompleted(): UserSessionPartialState = { Active }

    fun sessionExpired(): UserSessionPartialState = { previousState ->
        when (previousState) {
            is Active -> PinCodeRequired(isAfterExpired = true)
            else -> previousState
        }
    }

    fun logoutCompleted(): UserSessionPartialState = { LoginRequired(isAfterLogout = true) }

    fun closed(): UserSessionPartialState = { Closed }

}