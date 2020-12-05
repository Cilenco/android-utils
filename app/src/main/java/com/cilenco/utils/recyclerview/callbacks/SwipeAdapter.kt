package com.cilenco.utils.recyclerview.callbacks

import android.graphics.drawable.Drawable
import android.view.View

interface SwipeAdapter<V> {
    fun interface OnItemSwipedListener<V> { fun onItemSwiped(itemView: View, item: V, position: Int, direction: Int) }
    fun interface OnItemDragListener { fun onItemDragged(oldPosition: Int, newPosition: Int, dropped: Boolean): Boolean }

    var swipeDirections: (position: Int) -> Int
    var dragDirections: (position: Int) -> Int

    var onSwipeCallback: OnItemSwipedListener<V>?
    var onDragCallback: OnItemDragListener?

    var swipeColorLeft: Int
    var swipeColorRight: Int

    var swipeDrawableLeft: Drawable?
    var swipeDrawableRight: Drawable?

    var swipeMargin: Int
}