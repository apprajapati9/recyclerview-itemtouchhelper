package ca.apprajapati.recyclerview

import android.animation.ValueAnimator
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.MotionEvent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import ca.apprajapati.recyclerview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ItemsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val editButton =
            ActionButton(this, ContextCompat.getDrawable(this, R.drawable.edit)!!) { position ->
                Log.d("Ajay", "Edit button is clicked $position")
            }


        val deleteButton =
            ActionButton(
                this,
                ContextCompat.getDrawable(this, R.drawable.ic_delete)!!
            ) { position ->
                Log.d("Ajay", "Delete button is clicked $position")
                adapter.removeItem(position)
                binding.itemsRecyclerview.adapter?.notifyItemRemoved(position)
            }

        val archiveButton =
            ActionButton(
                this,
                ContextCompat.getDrawable(this, R.drawable.ic_archive)!!
            ) { position ->
                Log.d("Ajay", "archive button is clicked $position")
            }

        val itemTouchHelperCallback =
            HolderItemHelper(this, listOf(deleteButton, editButton, archiveButton))


        adapter = ItemsAdapter((1..7).toMutableList()) { position ->
            Log.d("Ajay", "Clicked item $position")
//                itemHelper.swipeRecyclerviewItem(position, distance = 200, time = 500)
            val holder = binding.itemsRecyclerview.findViewHolderForAdapterPosition(position)!!
        }
        binding.itemsRecyclerview.adapter = adapter
        binding.itemsRecyclerview.layoutManager = LinearLayoutManager(this)

        itemTouchHelperCallback.attachToRecyclerView(binding.itemsRecyclerview)
    }

    private fun swipeRecyclerviewItem(
        index: Int,
        distance: Int,
        direction: Int? = ItemTouchHelper.END,
        time: Long
    ) {
        val childView =
            binding.itemsRecyclerview.findViewHolderForAdapterPosition(index)?.itemView!!

        val x = childView.top.toFloat()
        val y = childView.top.toFloat()
        Log.d("Ajay", "x -> $x  y -> $y")
        val downTime = SystemClock.uptimeMillis()

        binding.itemsRecyclerview.dispatchTouchEvent(
            MotionEvent.obtain(
                downTime,
                downTime,
                MotionEvent.ACTION_DOWN,
                x,
                y,
                0
            )
        )
        ValueAnimator.ofInt(0, distance).apply {
            duration = time
            addUpdateListener {
                val dX = it.animatedValue as Int
                val mX = when (direction) {
                    ItemTouchHelper.END -> x + dX
                    ItemTouchHelper.START -> x - dX
                    else -> 0F
                }
                binding.itemsRecyclerview.dispatchTouchEvent(
                    MotionEvent.obtain(
                        downTime,
                        SystemClock.uptimeMillis(),
                        MotionEvent.ACTION_MOVE,
                        mX,
                        y,
                        0
                    )
                )
            }
        }.start()
    }

}