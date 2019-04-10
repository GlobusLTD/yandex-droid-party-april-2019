package com.globus.droidparty.presentation

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.globus.droidparty.functions.apply
import com.globus.droidparty.usersession.UserSession
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

@InjectViewState
class MainPresenter(
        private val userSession: UserSession
) : MvpPresenter<MainView>() {

    private val deepLinks = BehaviorRelay.create<DeepLink>()
    private val disposable = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        val userSessionState = userSession.state
                .observeOn(Schedulers.computation())
                .map(MainPartialViewStates::userSessionState)

        val deepLink = deepLinks // BehaviorSubject<DeepLink>
                .observeOn(Schedulers.computation())
                .map(MainPartialViewStates::deepLink)

        disposable += Observable.merge(userSessionState, deepLink)
                .scan(MainViewState(), apply())
                .map(MainViewState::navigation)
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = viewState::onNavigationChanged,
                        onError = { throwable -> throw throwable }
                )
    }

    fun deepLinkTo(deepLink: DeepLink) = deepLinks.accept(deepLink)

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

}