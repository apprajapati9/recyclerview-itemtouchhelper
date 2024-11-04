package ca.apprajapati.recyclerview

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class HolderItemHelper : ItemTouchHelper.Callback() {

    private var mRecyclerView : RecyclerView ?= null
    //private val rvListener : RecyclerViewListener ?= null

    private val paint  = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLUE
        style = Paint.Style.FILL
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val swipeFlags = ItemTouchHelper.START // towards start, means towards left
        return makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, swipeFlags)
    }

    override fun isLongPressDragEnabled(): Boolean = false
    override fun isItemViewSwipeEnabled(): Boolean = true

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        Log.d("Ajay", "Swiped")
    }

    fun attachToRecyclerView(recyclerView: RecyclerView) {
        mRecyclerView = recyclerView

        val helper = ItemTouchHelper(this)
        helper.attachToRecyclerView(mRecyclerView)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val position = viewHolder.adapterPosition

        val viewHolderItemTop =  viewHolder.itemView.top.toFloat() //top gives top pixel of view holder

        val viewHolderItemWidth = viewHolder.itemView.width.toFloat()
        val viewHolderItemHeight = viewHolder.itemView.height.toFloat()

        val viewHolderItemBottom = viewHolder.itemView.bottom.toFloat()

        val underlyingButton = UnderlyingButton(buttonSize = RectF(viewHolderItemWidth,viewHolderItemTop,viewHolderItemWidth + 50f, viewHolderItemTop + 50f), buttonBackgroundColor = viewHolder.itemView.context.getColor(android.R.color.holo_blue_dark))

        if(isCurrentlyActive){
            c.save()
            c.drawRect(viewHolderItemWidth-dX, viewHolderItemTop, viewHolderItemWidth + dX, viewHolderItemBottom, paint)
            //top+height OR viewHolder.itemView.bottom will work as well.

            c.restore()

            //1440-80 = 1360 -- starting width.
            //1440+80 = 1520 -- ending width
        }

        Log.d("Ajay", "Dx - $dX, Dy - $dY, position - $position, start w- ${viewHolderItemWidth-dX}, endW -> ${viewHolderItemWidth + dX}") // swiping towards right so Dx becomes minus.
    }

}


class UnderlyingButton(
    val buttonName: String?= "",
    val buttonSize : RectF,
    val buttonBackgroundColor: Int,
    val buttonImage: Int? = -1, )
{


    fun draw(canvas: Canvas){
//        canvas.drawRect(buttonSize.left, buttonSize.top, buttonSize.right, buttonSize.bottom, Paint().apply { style = Paint.Style.FILL
//        color = Color.BLUE})
        canvas.drawColor(buttonBackgroundColor)
    }

}