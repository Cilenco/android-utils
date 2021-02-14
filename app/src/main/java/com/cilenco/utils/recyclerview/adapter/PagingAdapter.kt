package com.cilenco.utils.recyclerview.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil

class PagingAdapter<V: Any, VH: LiveAdapter<V, VH>.ItemHolder> internal constructor(
    private val baseAdapter: LiveAdapter<V, VH>, callback: DiffUtil.ItemCallback<V>)
    : PagingDataAdapter<V, VH>(callback) {

    companion object {
        fun <V: Any, VH: LiveAdapter<V, VH>.ItemHolder> fromLiveAdapter(adapter: LiveAdapter<V, VH>, callback: DiffUtil.ItemCallback<V>): PagingAdapter<V, VH> {
            return PagingAdapter(adapter, callback)
        }
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        baseAdapter.onBindViewHolder(holder, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return baseAdapter.onCreateViewHolder(parent, viewType)
    }
}

fun <V: Any, VH: LiveAdapter<V, VH>.ItemHolder> LiveAdapter<V, VH>.asPagingAdapter(callback: DiffUtil.ItemCallback<V>): PagingAdapter<V, VH> {
    return PagingAdapter(this, callback)
}