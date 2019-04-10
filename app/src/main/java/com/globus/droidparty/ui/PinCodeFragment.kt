package com.globus.droidparty.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.globus.droidparty.R
import com.globus.droidparty.usersession.LoginCompleted
import com.globus.droidparty.usersession.UserSession
import kotlinx.android.synthetic.main.fragment_pincode.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class PinCodeFragment : Fragment(), KodeinAware {

    companion object {

        const val TAG = "PinCodeFragment"

        private const val EXTRA_AFTER_EXPIRED = "is_after_expired"

        fun newInstance(isAfterExpired: Boolean): PinCodeFragment = PinCodeFragment()
                .apply { arguments = bundleOf(EXTRA_AFTER_EXPIRED to isAfterExpired) }

    }

    override val kodein by kodein { requireContext() }

    private val userSession by instance<UserSession>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_pincode, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        coordinator.onBackPressListener = {
            val isAfterExpired = arguments?.getBoolean(EXTRA_AFTER_EXPIRED) ?: false
            if (isAfterExpired) {
                requireActivity().supportFinishAfterTransition()
            }
            true // Handled
        }

        confirmButton.setOnClickListener { userSession.notify(LoginCompleted) }
    }

}