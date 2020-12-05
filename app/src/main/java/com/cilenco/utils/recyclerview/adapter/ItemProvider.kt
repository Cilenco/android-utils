package com.cilenco.utils.recyclerview.adapter

import android.widget.Filter

import androidx.databinding.ObservableList

class ItemProvider<T:Any>(val adapter: LiveAdapter<T,*>, private val items: List<T>) : Filter() {
    var predicate: (T) -> Boolean = { true }
        set(value) { field = value; filter(null) }

    private val callback = ListChangeCallback()

    fun startObserving() {
        if(items is ObservableList) items.addOnListChangedCallback(callback)
        if(items.isNotEmpty()) publishResults(null, performFiltering(null))
    }

    fun endObserving() {
        if(items is ObservableList) items.removeOnListChangedCallback(callback)
    }

    override fun performFiltering(query: CharSequence?): FilterResults {
        val results = FilterResults()
        val filteredItems = LinkedHashSet<T>()

        filteredItems.addAll(items.filter { predicate(it) })

        results.values = filteredItems
        results.count = filteredItems.size

        return results
    }

    @Suppress("UNCHECKED_CAST")
    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
        val filteredItems = results.values as Set<T>
        adapter.setVisibleItems(filteredItems)
    }

    internal inner class ListChangeCallback: ObservableList.OnListChangedCallback<ObservableList<T>>() {
        override fun onChanged(sender: ObservableList<T>) {
            publishResults(null, performFiltering(null))
            adapter.notifyDataSetChanged()
        }

        override fun onItemRangeChanged(sender: ObservableList<T>, from: Int, count: Int) {
            publishResults(null, performFiltering(null))
            adapter.notifyItemRangeChanged(from, count)
        }

        override fun onItemRangeInserted(sender: ObservableList<T>, from: Int, count: Int) {
            publishResults(null, performFiltering(null))
            adapter.notifyItemRangeInserted(from, count)
        }

        override fun onItemRangeMoved(sender: ObservableList<T>, from: Int, to: Int, count: Int) {
            publishResults(null, performFiltering(null))
            adapter.let { for (i in 0 until count) it.notifyItemMoved(from+i, to+i) }
        }

        override fun onItemRangeRemoved(sender: ObservableList<T>, from: Int, count: Int) {
            publishResults(null, performFiltering(null))
            adapter.notifyItemRangeRemoved(from, count)
        }
    }
}