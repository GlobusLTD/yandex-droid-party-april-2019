package com.globus.droidparty.usersession

import com.globus.droidparty.functions.apply
import com.globus.droidparty.functions.first
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.observables.GroupedObservable
import io.reactivex.rxkotlin.Singles
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class UserSession(
        private val datasource: UserSessionDatasource
) {

    companion object {

        private val SplashMinDuration = Duration(2L, TimeUnit.SECONDS)

    }

    private val userSessionActions = PublishRelay.create<UserSessionAction>()

    val state: Observable<UserSessionState> by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        return@lazy userSessionActions
                .groupBy { userSessionAction -> userSessionAction } // Functions.identity()
                .flatMap { groupedObservable -> doOnUserSessionAction(groupedObservable) }
                .scan<UserSessionState>(Closed, apply())
                .distinctUntilChanged()
                .replay(1)
                .refCount()
    }

    fun notify(userSessionAction: UserSessionAction) = userSessionActions.accept(userSessionAction)

    private fun doOnUserSessionAction(
            actionObservable: GroupedObservable<*, UserSessionAction>
    ): Observable<UserSessionPartialState> = actionObservable.switchMap { action ->
        return@switchMap when (action) {
            is BackgroundLifecycle -> doOnBackgroundLifecycle(action)
            is ForegroundLifecycle -> doOnForegroundLifecycle(action)
            is PresaleCompleted -> doOnPresaleCompleted()
            is LoginCompleted -> doOnLoginCompleted()
            is Unauthorized -> doOnUnauthorized()
            is Logout -> doOnLogout()
        }
    }

    private fun doOnBackgroundLifecycle(
            lifecycle: BackgroundLifecycle
    ): Observable<UserSessionPartialState> = when (lifecycle.startedOrClosed) {
        StartedOrClosed.Started -> {
            val startupParams = datasource.getStartupParams()
            val splashMinDuration = Single.timer(SplashMinDuration.time, SplashMinDuration.unit)
            Singles.zip(startupParams, splashMinDuration, first())
                    .map(UserSessionPartialStates::startupParamsLoaded)
                    .toObservable()
                    .startWith(UserSessionPartialStates.started())
        }

        StartedOrClosed.Closed -> Observable
                .just(UserSessionPartialStates.closed())
                .subscribeOn(Schedulers.computation())
    }

    private fun doOnForegroundLifecycle(
            lifecycle: ForegroundLifecycle
    ): Observable<UserSessionPartialState> = when (lifecycle.resumedOrPaused) {
        ResumedOrPaused.Resumed -> Observable.empty()

        ResumedOrPaused.Paused -> datasource.getAutolockInterval()
                .flatMap { autolockInterval -> Single.timer(autolockInterval.time, autolockInterval.unit) }
                .map { UserSessionPartialStates.sessionExpired() }
                .toObservable()
    }

    private fun doOnPresaleCompleted(): Observable<UserSessionPartialState> = Observable
            .just(UserSessionPartialStates.presaleCompleted())
            .concatWith(datasource.setPresaleCompleted())
            .subscribeOn(Schedulers.computation())

    private fun doOnLoginCompleted(): Observable<UserSessionPartialState> = Observable
            .just(UserSessionPartialStates.loginCompleted())
            .concatWith(datasource.setHasAuthorizedUser())
            .subscribeOn(Schedulers.computation())

    private fun doOnUnauthorized(): Observable<UserSessionPartialState> = Observable
            .just(UserSessionPartialStates.sessionExpired())
            .subscribeOn(Schedulers.computation())

    private fun doOnLogout(): Observable<UserSessionPartialState> = Observable
            .just(UserSessionPartialStates.logoutCompleted())
            .concatWith(datasource.resetAuthorizedUser())
            .subscribeOn(Schedulers.computation())

}