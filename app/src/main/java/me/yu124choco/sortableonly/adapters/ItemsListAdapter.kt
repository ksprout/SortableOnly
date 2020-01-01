package me.yu124choco.sortableonly.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import me.yu124choco.sortableonly.R
import me.yu124choco.sortableonly.models.Item

class ItemsListAdapter(private val context: Context, var items: MutableList<Item>, private val onItemClickListener: ((item: Item) -> Unit)) : RecyclerView.Adapter<ItemsListAdapter.ViewHolder>() {

    val checkedItemIds = mutableSetOf<Long>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var itemContainer: ConstraintLayout = view.findViewById(R.id.item_container)
        var checkBox: CheckBox = view.findViewById(R.id.item_check_box)
        var textViewName: TextView = view.findViewById(R.id.text_view_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.adapter_items_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.let { h -> items[position].id?.let { itemId ->
            h.itemContainer.setOnClickListener {
                onItemClickListener.invoke(items[position])
            }
            h.checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    checkedItemIds.add(itemId)
                } else {
                    checkedItemIds.remove(itemId)
                }
            }
            h.textViewName.text = items[position].name
            h.checkBox.isChecked = checkedItemIds.contains(itemId)
        } }
    }

    override fun getItemCount(): Int = items.size

    fun updateItemsList(items: MutableList<Item>) {
        this.items = items
    }
}