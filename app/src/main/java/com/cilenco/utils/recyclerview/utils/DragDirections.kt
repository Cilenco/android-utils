package com.cilenco.utils.recyclerview.utils

import androidx.recyclerview.widget.ItemTouchHelper.*

object DragDirections {
    val ALL = { _: Int -> UP or DOWN or LEFT or RIGHT }
    val LEFT_RIGHT = { _: Int -> LEFT or RIGHT }
    val UP_DOWN = {_: Int -> UP or DOWN }

    val NONE = { _: Int -> 0 }
}