package com.cilenco.utils.recyclerview.adapter

import android.graphics.drawable.Drawable
import android.view.View

import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.SortedList

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.cilenco.utils.recyclerview.callbacks.SimpleSwipeCallback
import com.cilenco.utils.recyclerview.callbacks.SortedListCallback
import com.cilenco.utils.recyclerview.callbacks.SwipeAdapter
import com.cilenco.utils.recyclerview.utils.SortOrder
import java.util.*

abstract class LiveAdapter<V: Any, VH: LiveAdapter<V, VH>.ItemHolder>(private val items: List<V>): Adapter<VH>(), SwipeAdapter<V> {
    fun interface OnItemClickedListener<V> { fun onItemClicked(itemView: View, item: V, position: Int) }
    fun interface OnItemLongPressedListener<V> { fun onItemLongPressed(itemView: View, item: V, position: Int): Boolean }

    protected val itemProvider = ItemProvider(this, items)
    protected lateinit var visibleItems: SortedList<V>

    protected val touchHelper by lazy { ItemTouchHelper(swipeHelper) }
    protected val swipeHelper by lazy { SimpleSwipeCallback(this) }
    protected val listCallback by lazy { SortedListCallback(this) }

    protected var recyclerView: RecyclerView? = null

    protected var onClickCallback: OnItemClickedListener<V>? = null
    protected var onLongPressCallback: OnItemLongPressedListener<V>? = null

    override var swipeDirections: (position: Int) -> Int
        set(value) { swipeHelper.swipeDirections = value }
        get() { return swipeHelper.swipeDirections }

    override var dragDirections: (position: Int) -> Int
        set(value) { swipeHelper.dragDirections = value }
        get() { return swipeHelper.dragDirections }

    override var onSwipeCallback: (position:Int, item: V, direction: Int) -> Unit
        set(value) { swipeHelper.onSwipeCallback = value }
        get() { return swipeHelper.onSwipeCallback }

    override var onDragCallback: (oldPosition: Int, newPosition: Int, dropped: Boolean) -> Boolean
        set(value) { swipeHelper.onDragCallback = value }
        get() { return swipeHelper.onDragCallback }

    override var swipeColorLeft: Int
        set(value) { swipeHelper.swipeColorLeft = value }
        get() { return swipeHelper.swipeColorLeft }

    override var swipeColorRight: Int
        set(value) { swipeHelper.swipeColorRight = value }
        get() { return swipeHelper.swipeColorRight }

    override var swipeDrawableLeft: Drawable?
        set(value) { swipeHelper.swipeDrawableLeft = value }
        get() { return swipeHelper.swipeDrawableLeft }

    override var swipeDrawableRight: Drawable?
        set(value) { swipeHelper.swipeDrawableRight = value }
        get() { return swipeHelper.swipeDrawableRight }

    override var swipeMargin: Int
        set(value) { swipeHelper.swipeMargin = value }
        get() { return swipeHelper.swipeMargin }

    var sortOrder = SortOrder.ASC
        set(value) { field = value; reorderList() }

    init {
        listCallback.selector = { sortOrder.value * items.indexOf(it) }
        itemProvider.filter(null)
    }

    fun setOnItemClickedListener(listener: OnItemClickedListener<V>) {
        onClickCallback = listener
    }

    fun setOnItemLongPressed(listener: OnItemLongPressedListener<V>) {
        onLongPressCallback = listener
    }

    fun filter(predicate: (V) -> Boolean) {
        itemProvider.predicate = predicate
    }

    fun clearFilter() {
        itemProvider.predicate = { true }
    }

    fun <C : Comparable<C>> sortedBy(selector: (V) -> C) {
        listCallback.selector = selector
        reorderList()
    }

    fun clearOrder() {
        sortedBy { items.indexOf(it) }
    }

    private fun reorderList() {
        if(!::visibleItems.isInitialized) return

        visibleItems.beginBatchedUpdates()

        val copy = (visibleItems.size() - 1 downTo 0).map { visibleItems.removeItemAt(it) }
        copy.forEach { visibleItems.add(it) }

        visibleItems.endBatchedUpdates()
    }

    /*override fun getItemId(position: Int): Long {
        return visibleItems[position].hashCode().toLong()
    }*/

    internal fun setVisibleItems(items: Collection<V>) {
        if(!::visibleItems.isInitialized) {
            if(items.isEmpty()) return // Not able to instantiate visibleItems yet
            else visibleItems = SortedList(items.first().javaClass, listCallback)
        }

        visibleItems.beginBatchedUpdates()

        for (i in visibleItems.size() - 1 downTo 0) {
            val item = visibleItems[i]
            if (items.contains(item)) continue
            visibleItems.remove(item)
        }

        visibleItems.addAll(items)
        visibleItems.endBatchedUpdates()
    }

    fun getItem(position: Int): V {
        return visibleItems[position]
    }

    override fun getItemCount(): Int {
        return if(::visibleItems.isInitialized) visibleItems.size() else 0
    }

    override fun onAttachedToRecyclerView(rv: RecyclerView) {
        recyclerView = rv // Indicate attached to RecyclerView

        itemProvider.startObserving()
        touchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onDetachedFromRecyclerView(rv: RecyclerView) {
        recyclerView = null // Indicate detached from RecyclerView
        itemProvider.endObserving()
    }

    open inner class ItemHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener { onItemClicked() }
            itemView.setOnLongClickListener { onItemLongPressed() }
        }

        private fun onItemClicked() {
            val position = adapterPosition

            val item = if(position != NO_POSITION) getItem(position) else return
            onClickCallback?.onItemClicked(itemView, item, adapterPosition)
        }

        private fun onItemLongPressed(): Boolean {
            val position = adapterPosition

            val item = if(position != NO_POSITION) getItem(position) else return false
            return onLongPressCallback?.onItemLongPressed(itemView, item, adapterPosition) ?: false
        }
    }
}