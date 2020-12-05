package com.cilenco.utils.viewbinding

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import kotlin.properties.ReadOnlyProperty
import androidx.viewbinding.ViewBinding
import kotlin.reflect.KProperty

class ViewBinding<T : ViewBinding>(fragment: Fragment, private val binder: (View) -> T) :
    ReadOnlyProperty<Fragment, T> {
    private var fragmentBinding: T? = null

    private val viewObserver = object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            fragmentBinding = null
        }
    }

    init {
        fragment.viewLifecycleOwnerLiveData.observe(fragment) {
            it?.lifecycle?.addObserver(viewObserver)
        }
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        val binding = fragmentBinding
        if (binding != null) return binding

        val lifecycle = thisRef.viewLifecycleOwner.lifecycle
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            throw IllegalStateException("Fragment views are destroyed.")
        }

        return binder(thisRef.requireView()).also { fragmentBinding = it }
    }
}

fun <T : ViewBinding> Fragment.viewBinding(binder: (View) -> T) = ViewBinding(this, binder)