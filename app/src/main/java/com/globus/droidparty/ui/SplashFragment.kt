package com.globus.droidparty.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.globus.droidparty.R

class SplashFragment : Fragment() {

    companion object {

        const val TAG = "SplashFragment"

        fun newInstance(): SplashFragment = SplashFragment()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_splash, container, false)

}