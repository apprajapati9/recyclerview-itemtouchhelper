package ca.apprajapati.recyclerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ca.apprajapati.recyclerview.util.UiUtil

class HolderItemHelper(
    private val context: Context,
    private val actionButtons: List<ActionButton>
) :
    ItemTouchHelper.Callback() {


    private var mRecyclerView: RecyclerView? = null
    private var gestureDetector: GestureDetector? = null

    private val buttons = emptyMap<Int, List<ActionButton>>()
    private var buttonWidth = 0

    private val buttonDistance = UiUtil.dpToPx(context, 4f)

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        //color = Color.WHITE
        color = Color.GREEN
        style = Paint.Style.FILL
    }

    private val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLUE
        style = Paint.Style.FILL
    }


    init {
        gestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {

                return true
            }
        })
        buttonWidth = UiUtil.dpToPx(context, 72f)
    }


    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val swipeFlags =
            ItemTouchHelper.START or ItemTouchHelper.END // towards start, means towards left
        return makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, swipeFlags)
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.4f
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


    override fun onChildDrawOver(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder?,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    /*
        Dx - displacement X on swiped flag side. If START, then left side which becomes - (negative)
             if Right side, then Dx becomes + (positive)
     */
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        val position = viewHolder.adapterPosition  //adapter position
        var translationX = dX

        val itemView = viewHolder.itemView


        if (position < 0) {
            // we don't do anything.
            return
        }

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

            if (dX > 0) { // when viewHolder moves towards right, x becomes positive so swipe right.

                translationX = dX * actionButtons.size * buttonWidth / itemView.width
                Log.d(
                    "Ajay", "itemView width -> ${itemView.width}, " +
                            "TranslationX -> $translationX, " +
                            "buttonWidth -> $buttonWidth, " +
                            "dX -> $dX"
                )

                drawButtons(canvas = c, itemView = itemView, position, translationX)
            }

        }

        super.onChildDraw(
            c,
            recyclerView,
            viewHolder,
            translationX,
            dY,
            actionState,
            isCurrentlyActive
        )
    }

    private fun drawButtons(
        canvas: Canvas,
        itemView: View,
        pos: Int,
        dX: Float
    ) {

        //draw background

        canvas.drawRect(
            itemView.left.toFloat(),
            itemView.top.toFloat(),
            dX,
            itemView.bottom.toFloat(),
            backgroundPaint
        )

//        backgroundPaint.color = Color.WHITE
//
//        canvas.drawRect(itemView.left.toFloat() + dX , itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat(), backgroundPaint)
//
//        backgroundPaint.color = Color.GREEN


        //draw action buttons
        var buttonLeft = itemView.left
        for (i in actionButtons.indices) {
            val right = buttonLeft + buttonWidth

            canvas.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
            actionButtons[i].draw(
                canvas = canvas,
                itemSize = RectF(
                    buttonLeft.toFloat(),
                    itemView.top.toFloat(),
                    right.toFloat(),
                    itemView.bottom.toFloat()
                ),
                buttonNumber = i,
                count = actionButtons.size,
                position = pos
            )

            buttonLeft = right
        }
    }

}

class ActionButton(
    private val context: Context,
    private var imageId: Drawable,
    private val onButtonClicked: (position: Int) -> Unit
) {

    private var position = -1
    private var clickRegion = RectF()
    private var iconSize = 0
    private var backgroundSize = 0
    private var backgroundRadius = 0
    private var buttonDistance = 0

    init {

        iconSize = UiUtil.dpToPx(context, 24f)
        buttonDistance = UiUtil.dpToPx(context, 4f)
        backgroundSize = UiUtil.dpToPx(context, 48f) //TODO: dynamic?
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE //TODO: clear androidx.
    }

    private fun onClick(buttonPosition: PointF): Boolean {
        if (clickRegion.contains(buttonPosition.x, buttonPosition.y)) {
            onButtonClicked.invoke(position)
            return true
        }
        return false
    }

    fun draw(canvas: Canvas, itemSize: RectF, count: Int, buttonNumber: Int, position: Int) {

        canvas.save()
        val offsetX = if (count == 2) {
            if (buttonNumber == 0) {
                buttonDistance
            } else {
                -buttonDistance
            }
        } else 0

        // draw round background

        val centerX = itemSize.centerX() + offsetX
        val centerY = itemSize.centerY()

        val sizeHalf = backgroundSize / 2 //background around the image icons.

        //Tweak rx,ry to add more rounded radius
        paint.alpha = 100
        canvas.drawRoundRect(
            centerX - sizeHalf,
            centerY - sizeHalf,
            centerX + sizeHalf,
            centerY + sizeHalf,
            2f,
            2f,
            paint
        )
        paint.alpha = 255


        //draw icon
        imageId.setBounds(
            (itemSize.centerX() - iconSize / 2f + offsetX).toInt(),
            (itemSize.top + (itemSize.bottom - itemSize.top - iconSize) / 2).toInt(),
            ((itemSize.centerX() - iconSize / 2f + offsetX) + iconSize).toInt(),
            ((itemSize.top + (itemSize.bottom - itemSize.top - iconSize) / 2) + iconSize).toInt()
        )

        imageId.draw(canvas)
        canvas.restore()

        clickRegion = itemSize
        this.position = position
    }

}

//TODO: refactor to see if you can use lambda function because it has only 1 method
interface OnSwipeListener {
    fun onSwipeStartedOrEnabled(started: Boolean)
}

//interface UnderlyingButtonClickListener{
//    fun onClick
//}


//    fun swipeRecyclerviewItem(index: Int, distance: Int, direction: Int ?= ItemTouchHelper.START, time: Long){
//        val childView = mRecyclerView?.findViewHolderForAdapterPosition(index)?.itemView!!
//
//        val x = childView.top.toFloat()
//        val y =  childView.top.toFloat()
//        Log.d("Ajay", "x -> $x  y -> $y")
//        val downTime = SystemClock.uptimeMillis()
//
//        mRecyclerView?.dispatchTouchEvent(
//            MotionEvent.obtain(
//                downTime,
//                downTime,
//                MotionEvent.ACTION_DOWN,
//                x,
//                y,
//                0
//            )
//        )
//        ValueAnimator.ofInt(0, distance).apply {
//            duration = time
//            addUpdateListener {
//                val dX = it.animatedValue as Int
//                val mX = when (direction) {
//                    ItemTouchHelper.END -> x + dX
//                    ItemTouchHelper.START -> x - dX
//                    else -> 0F
//                }
//                mRecyclerView?.dispatchTouchEvent(
//                    MotionEvent.obtain(
//                        downTime,
//                        SystemClock.uptimeMillis(),
//                        MotionEvent.ACTION_MOVE,
//                        mX,
//                        y,
//                        0
//                    )
//                )
//            }
//        }.start()
//    }