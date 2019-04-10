package com.globus.droidparty.usersession

sealed class UserSessionAction {

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        javaClass != other?.javaClass -> false
        else -> true
    }

    override fun hashCode(): Int = this::class.hashCode()

}

class BackgroundLifecycle(
        val startedOrClosed: StartedOrClosed
) : UserSessionAction()

enum class StartedOrClosed {
    Started,
    Closed
}

class ForegroundLifecycle(
        val resumedOrPaused: ResumedOrPaused
) : UserSessionAction()

enum class ResumedOrPaused {
    Resumed,
    Paused
}

object PresaleCompleted : UserSessionAction()

object LoginCompleted : UserSessionAction()

object Unauthorized : UserSessionAction()

object Logout : UserSessionAction()