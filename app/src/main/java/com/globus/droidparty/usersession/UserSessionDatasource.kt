package com.globus.droidparty.usersession

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * This is just a sample.
 * In real apps please declare UserSessionDatasource as interface and
 * provide an implementation that stores values in database or SharedPreferences.
 */
class UserSessionDatasource {

    fun getStartupParams(): Single<StartupParams> = Single
            .just(StartupParams(hasAuthorizedUser = false, isPresaleShown = false))
            .subscribeOn(Schedulers.io())

    fun getAutolockInterval(): Single<Duration> = Single
            .just(Duration(10L, TimeUnit.SECONDS))
            .subscribeOn(Schedulers.io())

    fun setPresaleCompleted(): Completable = Completable.complete()

    fun setHasAuthorizedUser(): Completable = Completable.complete()

    fun resetAuthorizedUser(): Completable = Completable.complete()

}