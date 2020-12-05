package com.cilenco.utils.recyclerview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableList
import androidx.databinding.ViewDataBinding

open class DataBindingAdapter<V: Any>(items: List<V>, private val layout: Int, private val variable: Int): LiveAdapter<V, DataBindingAdapter<V>.BindingHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val binding: ViewDataBinding = DataBindingUtil.inflate(inflater, layout, parent, false)

        return BindingHolder(binding)
    }

    override fun onBindViewHolder(holder: BindingHolder, position: Int) {
        holder.binding.setVariable(variable, getItem(position))
        holder.binding.executePendingBindings()
    }

    inner class BindingHolder(val binding: ViewDataBinding):
        LiveAdapter<V, BindingHolder>.ItemHolder(binding.root)
}