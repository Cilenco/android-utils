package com.cilenco.utils.recyclerview.adapter

import android.view.View

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.SortedList

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.cilenco.utils.recyclerview.callbacks.SimpleSwipeCallback
import com.cilenco.utils.recyclerview.callbacks.SortedListCallback
import com.cilenco.utils.recyclerview.callbacks.SwipeAdapter
import com.cilenco.utils.recyclerview.callbacks.SwipeAdapter.OnItemDragListener
import com.cilenco.utils.recyclerview.callbacks.SwipeAdapter.OnItemSwipedListener
import com.cilenco.utils.recyclerview.utils.SortOrder
import kotlin.reflect.KClass

abstract class LiveAdapter<V: Any, VH: LiveAdapter<V, VH>.ItemHolder>(cls: Class<V>, private val items: List<V>): Adapter<VH>(), SwipeAdapter<V> {
    fun interface OnItemClickedListener<V> { fun onItemClicked(itemView: View, item: V, position: Int) }
    fun interface OnItemLongPressedListener<V> { fun onItemLongPressed(itemView: View, item: V, position: Int): Boolean }

    constructor(cls: KClass<V>, items: List<V>): this(cls.java, items)

    private val itemProvider = ItemProvider(this, items)

    private val listCallback = SortedListCallback(this)
    private val visibleItems = SortedList(cls, listCallback)

    private val touchHelper by lazy { ItemTouchHelper(swipeHelper) }
    private val swipeHelper by lazy { SimpleSwipeCallback(this) }

    private var onClickCallback: OnItemClickedListener<V>? = null
    private var onLongPressCallback: OnItemLongPressedListener<V>? = null

    override var onSwipeCallback = swipeHelper.onSwipeCallback
    override var onDragCallback = swipeHelper.onDragCallback

    override var swipeDirections = swipeHelper.swipeDirections
    override var dragDirections = swipeHelper.dragDirections

    override var swipeColorRight = swipeHelper.swipeColorRight
    override var swipeColorLeft = swipeHelper.swipeColorLeft

    override var swipeDrawableRight = swipeHelper.swipeDrawableRight
    override var swipeDrawableLeft = swipeHelper.swipeDrawableLeft

    override var swipeMargin = swipeHelper.swipeMargin

    var sortOrder = SortOrder.ASC; set(value) { field = value; reorderList() }

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

    fun setOnItemSwipedListener(listener: OnItemSwipedListener<V>) {
        onSwipeCallback = listener
    }

    fun setOnItemDraggedListener(listener: OnItemDragListener) {
        onDragCallback = listener
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
        visibleItems.beginBatchedUpdates()

        val copy = (visibleItems.size() - 1 downTo 0).map { visibleItems.removeItemAt(it) }
        copy.forEach { visibleItems.add(it) }

        visibleItems.endBatchedUpdates()
    }

    internal fun setVisibleItems(items: Collection<V>) {
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
        return visibleItems.size()
    }

    override fun onAttachedToRecyclerView(rv: RecyclerView) {
        touchHelper.attachToRecyclerView(rv)
        itemProvider.startObserving()
    }

    override fun onDetachedFromRecyclerView(rv: RecyclerView) {
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