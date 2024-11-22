package ca.apprajapati.recyclerview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
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
    private val actionButtons: List<ActionButton>,
) :
    ItemTouchHelper.Callback() {


    private var mRecyclerView: RecyclerView? = null
    private var gestureDetector: GestureDetector? = null

    private var buttonWidth = 0

    private var swipedPosition = -1

    private var threshold = 0f

    private val buttonDistance = UiUtil.dpToPx(context, 4f)

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        //color = Color.WHITE
        //color = Color.GREEN
        style = Paint.Style.FILL
    }

    private val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.FILL
    }

    private val touchListener: View.OnTouchListener = View.OnTouchListener { view, event ->
        view.performClick()
        // Log.d("Ajay", "clicked on recyclerview $v, and event ${event.action}")
        gestureDetector?.onTouchEvent(event)
        false
    }

    private val viewHolderListener = View.OnTouchListener { view, _ ->
        view.performClick()
        Log.d("Ajay", "view holder item is touched..")
        false
    }

    private val colors = mutableListOf<Int>().apply {
        add(Color.parseColor("#ff5733"))
        add(Color.parseColor("#f4bd47"))
        add(Color.parseColor("#5769bd")) //blue
    }

    init {
        gestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                for (button in actionButtons) {
                    if (button.onClick(PointF(e.x, e.y))) {
                        Log.d("Ajay", "Gesture Detector -> true on button")
                        break
                    } else {
                        Log.d("Ajay", "Gesture Detector -> NOT on button")
                    }
                }
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

    override fun isLongPressDragEnabled(): Boolean = false
    override fun isItemViewSwipeEnabled(): Boolean = true

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        Log.d("Ajay", "Swiped")

        val position = viewHolder.adapterPosition

        //Undo previous swiped item
        if (swipedPosition != position && swipedPosition != -1) {
            mRecyclerView?.adapter?.notifyItemChanged(swipedPosition)
            swipedPosition = position
        } else {
            swipedPosition = position
        }

    }


    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
    }

    fun attachToRecyclerView(recyclerView: RecyclerView) {
        mRecyclerView = recyclerView

        mRecyclerView?.setOnTouchListener(touchListener)
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
        itemView.elevation = 10f


        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

            if (dX > 0) { // when viewHolder moves towards right, x becomes positive so swipe right.

                translationX = dX * actionButtons.size * buttonWidth / itemView.width
//                Log.d(
//                    "Ajay", "itemView width -> ${itemView.width}, " +
//                            "TranslationX -> $translationX, " +
//                            "buttonWidth -> $buttonWidth, " +
//                            "dX -> $dX"
//                )

                drawButtons(
                    canvas = c,
                    itemView = itemView,
                    itemPosition = position,
                    dX = translationX
                )
            } else {

                translationX = (dX * actionButtons.size * buttonWidth) / itemView.width
                val fullButtonSize = actionButtons.size * buttonWidth
                Log.d("Ajay", "translationX -> $translationX, all button size-> $fullButtonSize")
                val reverseX = -translationX
                val fraction = reverseX / actionButtons.size


                var left =
                    itemView.right.toFloat() // from width x that goes towards right when swiping left
                var right = itemView.width.toFloat() // right side width

                val str = "Ajay"

                var textPlaceStart = left
                var textPlaceX = fraction / 4

                //Log.d("Ajay", "translation x -> $reverseX, left -> $left, right -> $right ")
                for (i in 0..2) {
                    left -= fraction //decreasing by number of items thus right to left, right side being width of itemView and left being -fraction towards left side. i.e if width of screen is 1440, then 1440 - 20 is left. 1440 is right.

                    backgroundPaint.color = colors[i]
                    c.save()
                    c.clipRect(left, itemView.top.toFloat(), right, itemView.bottom.toFloat())

                    c.drawRect(
                        left,
                        itemView.top.toFloat(),
                        right,
                        itemView.bottom.toFloat(),
                        backgroundPaint
                    )


                    dividerPaint.textSize = UiUtil.dpToPx(context = context, 20f).toFloat()
                    val textWidth = dividerPaint.measureText(str)

                    val rect = Rect()
                    dividerPaint.getTextBounds(str, 0, str.length, rect)

//                    c.drawLine(left, itemView.top.toFloat(), right, itemView.bottom.toFloat(), dividerPaint)
//                    c.drawLine(right, itemView.top.toFloat(), left, itemView.bottom.toFloat(), dividerPaint)

                    dividerPaint.color = Color.WHITE
                    dividerPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
//

                    //left + fraction / actionButtons.size  == LEFT side reveal
                    // left + textPlaceX  ==  left size reveal
                    //right - fullButtonSize.toFloat() / 3 + textWidth / 2 == RIGHT side reveal
                    c.drawText(
                        str,
                        0,
                        str.length,
                        right - fullButtonSize.toFloat() / 3 + textWidth / 3, //fraction 20, every iteration - right left 1440- 1420, 1420-1400, 1400- 1388
                        (itemView.top.toFloat() + itemView.height / 2) + (rect.height() / 2),
                        dividerPaint
                    )

                    c.restore()

                    Log.d("Ajay", "fraction -> $fraction , right -> $right , left -> $left")
                    right = left

                }

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
        itemPosition: Int,
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

        //draw action buttons
        var buttonLeft = itemView.left
        val fraction = dX / actionButtons.size
        var sum = 0f

        for (i in actionButtons.indices) {
            val right = buttonLeft + buttonWidth

            sum += fraction
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
                itemPosition = itemPosition
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

    fun onClick(buttonPosition: PointF): Boolean {
        if (clickRegion.contains(buttonPosition.x, buttonPosition.y)) {
            onButtonClicked.invoke(position)
            return true
        }
        return false
    }

    fun draw(canvas: Canvas, itemSize: RectF, count: Int, buttonNumber: Int, itemPosition: Int) {

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
        paint.alpha = 200
        canvas.drawRoundRect(
            centerX - sizeHalf,
            centerY - sizeHalf,
            centerX + sizeHalf,
            centerY + sizeHalf,
            2f,
            2f,
            paint
        )

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
        position = itemPosition
    }

}

//TODO: refactor to see if you can use lambda function because it has only 1 method
interface OnSwipeListener {
    fun onSwipeStartedOrEnabled(started: Boolean)
}

//interface UnderlyingButtonClickListener{
//    fun onClick
//}


