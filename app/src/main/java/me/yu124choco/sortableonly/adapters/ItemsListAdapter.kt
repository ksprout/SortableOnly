package me.yu124choco.sortableonly.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import me.yu124choco.sortableonly.R
import me.yu124choco.sortableonly.models.Item

class ItemsListAdapter(private val context: Context, private val items: MutableList<Item>, private val onItemClickListener: ((pos: Int) -> Unit)) : BaseAdapter() {

    private class ViewHolder {
        var itemContainer: ConstraintLayout? = null
        var textViewName: TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val holder: ViewHolder
        var cv = convertView

        if (cv == null) {
            cv = LayoutInflater.from(context).inflate(R.layout.adapter_items_list, null)
            holder = ViewHolder()
            holder.itemContainer = cv.findViewById(R.id.item_container)
            holder.textViewName = cv.findViewById(R.id.text_view_name)
            cv.tag = holder
        } else {
            holder = cv.tag as ViewHolder
        }
        holder.itemContainer?.setOnClickListener {
            onItemClickListener.invoke(position)
        }
        holder.textViewName?.text = items[position].name
        return cv!!
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}