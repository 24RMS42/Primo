package com.primo.goods.decoration

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

class VerticalSpaceItemDecoration(val mVerticalSpaceHeight: Int, var withLastItem: Boolean = false) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                state: RecyclerView.State?) {

        if (parent.getChildAdapterPosition(view) != parent.adapter.itemCount - 1 || withLastItem)
            outRect.top = mVerticalSpaceHeight
    }
}

