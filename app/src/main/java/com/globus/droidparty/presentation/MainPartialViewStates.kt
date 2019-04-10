package com.globus.droidparty.presentation

import com.globus.droidparty.usersession.*

typealias MainPartialViewState = (MainViewState) -> MainViewState

object MainPartialViewStates {

    fun userSessionState(userSessionState: UserSessionState): MainPartialViewState = { previousViewState ->
        when (userSessionState) {
            is Closed -> previousViewState.copy(navigation = NoneFlow)
            is Presale -> previousViewState.copy(navigation = PresaleFlow)
            is Splash -> previousViewState.copy(navigation = SplashFlow)
            is LoginRequired -> previousViewState.copy(
                    pendingDeepLink = previousViewState.pendingDeepLink
                            .takeIf { !userSessionState.isAfterLogout },
                    navigation = LoginFlow
            )
            is PinCodeRequired -> previousViewState.copy(
                    pendingDeepLink = previousViewState.pendingDeepLink
                            .takeIf { !userSessionState.isAfterExpired },
                    navigation = PinCodeFlow(isAfterExpired = userSessionState.isAfterExpired)
            )
            is Active -> previousViewState.copy(
                    pendingDeepLink = null,
                    navigation = MainFlow(deepLink = previousViewState.pendingDeepLink)
            )
        }
    }

    fun deepLink(deepLink: DeepLink): MainPartialViewState = { previousViewState ->
        val navigation = previousViewState.navigation
        when (navigation) {
            is MainFlow -> previousViewState.copy(
                    pendingDeepLink = null,
                    navigation = MainFlow(deepLink = deepLink)
            )
            else -> previousViewState.copy(pendingDeepLink = deepLink)
        }
    }

}