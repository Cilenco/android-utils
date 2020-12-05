package com.cilenco.utils.recyclerview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableList
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

typealias Inflater<VB> = (inflater: LayoutInflater, parent: ViewGroup, attach: Boolean) -> VB
typealias Binder<VB, V> = (binding: VB, item: V) -> Unit

class ViewBindingAdapter<V: Any, VB: ViewBinding>
    (items: List<V>, private val creator: Inflater<VB>, private val binder: Binder<VB, V>)
    : LiveAdapter<V, ViewBindingAdapter<V, VB>.ViewBindingHolder>(items) {


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

fun <V: Any, VB: ViewBinding> RecyclerView.bindViews(items: List<V>, inflater: Inflater<VB>, binder: Binder<VB, V>) = ViewBindingAdapter(items, inflater, binder)