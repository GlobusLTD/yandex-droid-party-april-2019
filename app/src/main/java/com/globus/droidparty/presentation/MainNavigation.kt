package com.globus.droidparty.presentation

sealed class MainNavigation

object NoneFlow : MainNavigation()

object SplashFlow : MainNavigation()

object PresaleFlow : MainNavigation()

object LoginFlow : MainNavigation()

data class PinCodeFlow(val isAfterExpired: Boolean) : MainNavigation()

data class MainFlow(val deepLink: DeepLink?) : MainNavigation()