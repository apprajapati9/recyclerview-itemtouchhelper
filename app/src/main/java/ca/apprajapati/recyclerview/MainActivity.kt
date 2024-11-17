package ca.apprajapati.recyclerview

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import ca.apprajapati.recyclerview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding : ActivityMainBinding ?= null
    private val binding get() = _binding!!

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

        val editButton = ActionButton(this, ContextCompat.getDrawable(this, R.drawable.edit)!!) {
            Log.d("Ajay", "Edit button is clicked")
        }


        val deleteButton = ActionButton(this, ContextCompat.getDrawable(this, R.drawable.ic_delete)!!) {
            Log.d("Ajay", "Delete button is clicked")
        }

        val archiveButton = ActionButton(this, ContextCompat.getDrawable(this, R.drawable.ic_archive)!!) {
            Log.d("Ajay", "Delete button is clicked")
        }

        val itemHelper = HolderItemHelper(this, listOf(deleteButton, editButton, archiveButton))

        val adapter = ItemsAdapter((1..7).toList()) {
            position ->
                Log.d("Ajay", "Clicked item $position")
//                itemHelper.swipeRecyclerviewItem(position, distance = 200, time = 500)
                val holder = binding.itemsRecyclerview.findViewHolderForAdapterPosition(position)
        }

        binding.itemsRecyclerview.adapter = adapter
        binding.itemsRecyclerview.layoutManager = LinearLayoutManager(this)

        itemHelper.attachToRecyclerView(binding.itemsRecyclerview)
    }

}