package com.globus.droidparty

import android.app.Application
import com.globus.droidparty.usersession.UserSession
import com.globus.droidparty.usersession.UserSessionDatasource
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

class SampleApplication : Application(), KodeinAware {

    override val kodein by Kodein.lazy {
        bind<UserSessionDatasource>() with singleton { UserSessionDatasource() }
        bind<UserSession>() with singleton { UserSession(instance()) }
    }

    private val userSession: UserSession by instance()

    override fun onCreate() {
        super.onCreate()
        userSession.state.subscribe()
    }

}