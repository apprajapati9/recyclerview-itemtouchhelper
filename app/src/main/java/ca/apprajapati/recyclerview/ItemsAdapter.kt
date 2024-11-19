package ca.apprajapati.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ca.apprajapati.recyclerview.databinding.RecyclerviewItemLayoutBinding

class ItemsAdapter(
    private val items: MutableList<Int> = mutableListOf<Int>(),
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<ItemsAdapter.ItemHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder.from(parent)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = items[position]

        holder.binding.itemName.text = item.toString()

        holder.binding.itemName.setOnClickListener {
            onClick.invoke(position)
        }
    }

    fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    class ItemHolder(val binding: RecyclerviewItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ItemHolder {
                val binding = RecyclerviewItemLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ItemHolder(binding)
            }
        }
    }

}