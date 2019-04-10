package com.globus.droidparty.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.globus.droidparty.R
import com.globus.droidparty.presentation.DeepLink
import com.globus.droidparty.usersession.Logout
import com.globus.droidparty.usersession.Unauthorized
import com.globus.droidparty.usersession.UserSession
import kotlinx.android.synthetic.main.fragment_main.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class MainFragment : Fragment(), KodeinAware {

    companion object {

        const val TAG = "MainFragment"

        fun newInstance(): MainFragment = MainFragment()

    }

    override val kodein by kodein { requireContext() }

    private val userSession by instance<UserSession>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_main, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        emulate401Button.setOnClickListener { userSession.notify(Unauthorized) }
        logoutButton.setOnClickListener { userSession.notify(Logout) }
    }

    fun onDeepLinkChanged(deepLink: DeepLink) = Toast
            .makeText(requireContext(), "Handle me! $deepLink", Toast.LENGTH_LONG)
            .show()

}