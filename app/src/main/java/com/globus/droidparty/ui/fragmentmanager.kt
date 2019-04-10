@file:Suppress("NOTHING_TO_INLINE")

package com.globus.droidparty.ui

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

typealias FragmentFactory<F> = () -> F

inline fun <F : Fragment> FragmentManager.popOrReplaceRoot(
        @IdRes containerId: Int,
        fragmentFactory: FragmentFactory<F>,
        tag: String
): F {
    var fragment = pop<F>(tag)
    if (fragment == null) {
        fragment = fragmentFactory()
        transaction { newBackStack(containerId, fragment, tag) }
    }
    return fragment
}

@Suppress("UNCHECKED_CAST")
inline fun <F : Fragment> FragmentManager.pop(tag: String, inclusive: Boolean = false): F? {
    val fragment = findFragmentByTag(tag) as F?
    if (fragment != null) {
        when (isInBackStack(tag)) {
            true -> {
                // 0 means that fragments will be popped to fragment specified by tag
                val flags = if (inclusive) FragmentManager.POP_BACK_STACK_INCLUSIVE else 0
                popBackStackImmediate(tag, flags)
            }
            false -> popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE) // Not a back stack entry (root)
        }
    }
    return fragment
}

inline fun FragmentManager.isInBackStack(tag: String): Boolean = (0 until backStackEntryCount)
        .map(::getBackStackEntryAt)
        .map(FragmentManager.BackStackEntry::getName)
        .any { backStackName -> backStackName == tag }

inline fun FragmentManager.removeAll() = commitNow {
    fragments.reversed().forEach { fragment -> remove(fragment) }
}

inline fun FragmentManager.transaction(
        body: FragmentTransactionBuilder.() -> Unit
) = FragmentTransactionBuilder(this).body()

class FragmentTransactionBuilder constructor(
        private val fragmentManager: FragmentManager
) {

    fun newBackStack(@IdRes containerId: Int, fragment: Fragment, tag: String) {
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        fragmentManager.commitNow {
            replace(containerId, fragment, tag)
        }
    }

    fun addToBackStack(@IdRes containerId: Int, fragment: Fragment, tag: String) = fragmentManager.commit {
        addToBackStack(tag)
        replace(containerId, fragment, tag)
    }

}

inline fun FragmentManager.commit(
        body: FragmentTransaction.() -> Unit
) {
    val transaction = beginTransaction()
    transaction.body()
    transaction.commit()
    executePendingTransactions()
}

inline fun FragmentManager.commitNow(
        body: FragmentTransaction.() -> Unit
) {
    val transaction = beginTransaction()
    transaction.body()
    transaction.commitNow()
}
