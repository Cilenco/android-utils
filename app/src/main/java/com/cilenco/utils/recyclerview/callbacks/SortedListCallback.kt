package com.cilenco.utils.recyclerview.callbacks

import androidx.recyclerview.widget.SortedList
import com.cilenco.utils.recyclerview.adapter.LiveAdapter

class SortedListCallback<V:Any>(private val adapter: LiveAdapter<V,*>): SortedList.Callback<V>() {
    lateinit var selector: (V) -> Comparable<*>

    private val comp: Comparator<V> = Comparator {
            x: V, y: V -> adapter.sortOrder.value * compareValuesBy(x, y, selector)
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        adapter.notifyItemMoved(fromPosition, toPosition)
    }

    override fun onChanged(position: Int, count: Int) {
        adapter.notifyItemRangeChanged(position, count)
    }

    override fun onInserted(position: Int, count: Int) {
        adapter.notifyItemRangeInserted(position, count)
    }

    override fun onRemoved(position: Int, count: Int) {
        adapter.notifyItemRangeRemoved(position, count)
    }

    override fun compare(t1: V, t2: V): Int {
        return comp.compare(t1, t2)
    }

    override fun areContentsTheSame(oldItem: V, newItem: V): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(item1: V, item2: V): Boolean {
        return item1 === item2
    }
}