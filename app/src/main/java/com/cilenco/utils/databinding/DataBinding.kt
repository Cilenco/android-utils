package com.cilenco.utils.databinding

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class DataBinding<T : ViewDataBinding>(fragment: Fragment, private val binder: (View) -> T) : ReadOnlyProperty<Fragment, T> {
    private var fragmentBinding: T? = null

    private val viewObserver = object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            fragmentBinding = null
        }
    }

    init {
        fragment.viewLifecycleOwnerLiveData.observe(fragment, Observer {
            it?.lifecycle?.addObserver(viewObserver)
        })
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

fun <T : ViewDataBinding> Fragment.dataBinding(binder: (View) -> T) = DataBinding(this, binder)
