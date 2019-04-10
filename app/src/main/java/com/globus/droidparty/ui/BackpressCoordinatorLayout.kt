package com.globus.droidparty.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.KeyEvent
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.globus.droidparty.R

class BackpressCoordinatorLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @SuppressLint("PrivateResource") defStyleAttr: Int = R.attr.coordinatorLayoutStyle
) : CoordinatorLayout(context, attrs, defStyleAttr) {

    var onBackPressListener: () -> Boolean = { false }

    init {
        isFocusable = true
        isFocusableInTouchMode = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            isFocusedByDefault = true
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        requestFocus()
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        var result = super.dispatchKeyEvent(event)
        if (!result) {
            when (event.action) {
                KeyEvent.ACTION_DOWN -> result = dispatchKeyDown(event.keyCode, event)
                KeyEvent.ACTION_UP -> result = dispatchKeyUp(event.keyCode, event)
            }
        }
        return result
    }

    private fun dispatchKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val applicationInfo = context.applicationInfo
            return if (applicationInfo.targetSdkVersion >= Build.VERSION_CODES.ECLAIR) {
                event.startTracking()
                false
            } else {
                onBackPressListener()
            }
        }
        return false
    }

    private fun dispatchKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        val applicationInfo = context.applicationInfo
        if (applicationInfo.targetSdkVersion >= Build.VERSION_CODES.ECLAIR) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking && !event.isCanceled) {
                return onBackPressListener()
            }
        }
        return false
    }

}