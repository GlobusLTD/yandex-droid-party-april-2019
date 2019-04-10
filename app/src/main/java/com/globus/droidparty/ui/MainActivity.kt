package com.globus.droidparty.ui

import android.content.Intent
import android.os.Bundle
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.globus.droidparty.R
import com.globus.droidparty.presentation.*
import com.globus.droidparty.usersession.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class MainActivity : MvpAppCompatActivity(), KodeinAware, MainView {

    override val kodein by kodein()

    private val userSession by instance<UserSession>()

    @InjectPresenter
    lateinit var presenter: MainPresenter

    @ProvidePresenter
    fun provideMainPresenter(): MainPresenter = MainPresenter(userSession)

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            onNewIntent(intent)
            userSession.notify(BackgroundLifecycle(StartedOrClosed.Started))
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        when (intent.action) {
            Intent.ACTION_VIEW -> {
                val uri = intent.data ?: throw IllegalArgumentException("Intent.ACTION_VIEW should provide data argument")
                val deepLink = DeepLink(uri = uri, timestamp = System.currentTimeMillis())
                presenter.deepLinkTo(deepLink)
            }
        }
    }

    override fun onNavigationChanged(navigation: MainNavigation) {
        when (navigation) {
            is NoneFlow -> supportFragmentManager.removeAll()

            is SplashFlow -> supportFragmentManager.popOrReplaceRoot(
                    containerId = R.id.content,
                    fragmentFactory = { SplashFragment.newInstance() },
                    tag = SplashFragment.TAG
            )

            is PresaleFlow -> supportFragmentManager.popOrReplaceRoot(
                    containerId = R.id.content,
                    fragmentFactory = { PresaleFragment.newInstance() },
                    tag = PresaleFragment.TAG
            )

            is LoginFlow -> supportFragmentManager.popOrReplaceRoot(
                    containerId = R.id.content,
                    fragmentFactory = { LoginFragment.newInstance() },
                    tag = LoginFragment.TAG
            )

            is PinCodeFlow -> if (navigation.isAfterExpired) {
                // Place pin code screen above main flow screens
                supportFragmentManager.transaction {
                    addToBackStack(
                            containerId = R.id.content,
                            fragment = PinCodeFragment.newInstance(isAfterExpired = true),
                            tag = PinCodeFragment.TAG
                    )
                }

            } else {
                supportFragmentManager.popOrReplaceRoot(
                        containerId = R.id.content,
                        fragmentFactory = { PinCodeFragment.newInstance(isAfterExpired = false) },
                        tag = PinCodeFragment.TAG
                )
            }

            is MainFlow -> {
                val mainFragment = supportFragmentManager.popOrReplaceRoot(
                        containerId = R.id.content,
                        fragmentFactory = { MainFragment.newInstance() },
                        tag = MainFragment.TAG
                )
                navigation.deepLink?.let(mainFragment::onDeepLinkChanged)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        userSession.notify(ForegroundLifecycle(ResumedOrPaused.Resumed))
    }

    override fun onStop() {
        super.onStop()
        if (!isFinishing) {
            userSession.notify(ForegroundLifecycle(ResumedOrPaused.Paused))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            userSession.notify(BackgroundLifecycle(StartedOrClosed.Closed))
        }
    }

}