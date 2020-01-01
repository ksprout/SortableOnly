package me.yu124choco.sortableonly.fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_items_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import me.yu124choco.sortableonly.R
import me.yu124choco.sortableonly.adapters.ItemsListAdapter
import me.yu124choco.sortableonly.database.AppDatabase
import me.yu124choco.sortableonly.models.Item
import kotlin.math.max
import kotlin.math.min

class ItemsListFragment : Fragment() {

    companion object {
        const val ORDER_BY_CUSTOM = "ORDER_BY_CUSTOM"
        const val ORDER_BY_NAME = "ORDER_BY_NAME"
        const val ORDER_BY_PRICE = "ORDER_BY_PRICE"
    }

    private var orderRule = ORDER_BY_CUSTOM
    private var itemsListAdapter: ItemsListAdapter? = null
    var onItemsListElemClickListener: ((item: Item) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val or = arguments?.getString("order_rule")
        if (or != null) orderRule = or
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_items_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity != null) setupList(activity!!)
    }

    private fun setupList(activity: Activity) = GlobalScope.launch(Dispatchers.Main) {
        val items = getAllItemsAsync(activity).await().toMutableList()

        itemsListAdapter = ItemsListAdapter(activity, items) {item ->
            if (onItemsListElemClickListener != null) onItemsListElemClickListener?.invoke(item)
        }
        list_view_items?.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        list_view_items?.adapter = itemsListAdapter
        list_view_items?.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {

            private var fromPosition = -1
            private var toPosition = -1
            private var touchDowned = false

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                if (!touchDowned) {
                    touchDowned = true
                    fromPosition = viewHolder.adapterPosition
                }
                toPosition = target.adapterPosition
                list_view_items?.adapter?.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                if (viewHolder == null) {
                    if (toPosition != fromPosition) {
                        moveItem(activity, fromPosition, toPosition)
                        touchDowned = false
                        fromPosition = -1
                        toPosition = -1
                    }
                }
            }
        })
        if (list_view_items != null) itemTouchHelper.attachToRecyclerView(list_view_items)
    }

    /**
     * アイテム一覧のリストを表示する
     */
    fun updateList(activity: Activity) = GlobalScope.launch(Dispatchers.Main) {
        val items = getAllItemsAsync(activity).await().toMutableList()

        itemsListAdapter?.let {
            it.updateItemsList(items)
            it.notifyDataSetChanged()
        }
    }

    private fun getAllItemsAsync(activity: Activity) = GlobalScope.async {
        val db = AppDatabase.getDatabase(activity)
        return@async db.itemDao().getAll()
    }

    fun deleteTargetItems(activity: Activity) = GlobalScope.launch(Dispatchers.Main) {
        if (itemsListAdapter != null) {
            val targets = itemsListAdapter!!.items.filter { itemsListAdapter!!.checkedItemIds.contains(it.id) }
            GlobalScope.async {
                val db = AppDatabase.getDatabase(activity)
                return@async db.itemDao().deleteAll(targets.toList())
            }.await()
            updateList(activity)
        }
    }

    fun deleteItem(activity: Activity, item: Item) = GlobalScope.launch(Dispatchers.Main) {
        GlobalScope.async {
            val db = AppDatabase.getDatabase(activity)
            return@async db.itemDao().deleteAll(listOf(item))
        }.await()
        updateList(activity)
    }

    fun moveItem(activity: Activity, fromPos: Int, toPos: Int) = GlobalScope.launch(Dispatchers.Main) {
        if (itemsListAdapter != null) {
            val items = itemsListAdapter!!.items
            val toOrder = items[toPos].orderNumber
            GlobalScope.async {
                items[fromPos].orderNumber = toOrder
                if (fromPos < toPos) {
                    for (i in (fromPos + 1)..toPos) {
                        items[i].orderNumber += 1
                    }
                } else if (fromPos > toPos) {
                    for (i in toPos..(fromPos - 1)) {
                        items[i].orderNumber -= 1
                    }
                }
                val targets = items.filterIndexed { i, _ ->
                    i >= min(fromPos, toPos) && i <= max(fromPos, toPos)
                }

                val db = AppDatabase.getDatabase(activity)
                return@async db.itemDao().updateAll(targets)

            }.await()
            updateList(activity)
        }
    }
}