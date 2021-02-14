package com.cilenco.utils.recyclerview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableList
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import kotlin.reflect.KClass

typealias Inflater<VB> = (inflater: LayoutInflater, parent: ViewGroup, attach: Boolean) -> VB
typealias Binder<VB, V> = (binding: VB, item: V) -> Unit

open class ViewBindingAdapter<V: Any, VB: ViewBinding>
    (cls: Class<V>, items: List<V>, private val creator: Inflater<VB>, private val binder: Binder<VB, V>)
    : LiveAdapter<V, ViewBindingAdapter<V, VB>.ViewBindingHolder>(cls, items) {

    constructor(cls: KClass<V>, items: List<V>, creator: Inflater<VB>, binder: Binder<VB, V>): this(cls.java, items, creator, binder)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewBindingHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        return ViewBindingHolder(creator(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewBindingHolder, position: Int) {
        binder(holder.binding, getItem(position))
    }

    inner class ViewBindingHolder(val binding: VB):
        LiveAdapter<V, ViewBindingAdapter<V, VB>.ViewBindingHolder>.ItemHolder(binding.root)
}

inline fun <reified V: Any, VB: ViewBinding> RecyclerView.bindViews
        (items: List<V>, noinline inflater: Inflater<VB>, noinline binder: Binder<VB, V>): ViewBindingAdapter<V, VB> {
    return ViewBindingAdapter(V::class.java, items, inflater, binder)
}