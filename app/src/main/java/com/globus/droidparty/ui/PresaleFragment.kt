package com.globus.droidparty.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.globus.droidparty.R
import com.globus.droidparty.usersession.PresaleCompleted
import com.globus.droidparty.usersession.UserSession
import kotlinx.android.synthetic.main.fragment_presale.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class PresaleFragment : Fragment(), KodeinAware {

    companion object {

        const val TAG = "PresaleFragment"

        fun newInstance(): PresaleFragment = PresaleFragment()

    }

    override val kodein by kodein { requireContext() }

    private val userSession by instance<UserSession>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_presale, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        skipButton.setOnClickListener { userSession.notify(PresaleCompleted) }
    }

}