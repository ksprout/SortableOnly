package me.yu124choco.sortableonly.fragments

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_items_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import me.yu124choco.sortableonly.MainActivity
import me.yu124choco.sortableonly.R
import me.yu124choco.sortableonly.adapters.ItemsListAdapter
import me.yu124choco.sortableonly.database.AppDatabase
import me.yu124choco.sortableonly.models.Item
import me.yu124choco.sortableonly.util.makeShortToast

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
        if (activity != null) displayList(activity!!)
    }

    /**
     * アイテム一覧のリストを表示する
     */
    fun displayList(activity: Activity) = GlobalScope.launch(Dispatchers.Main) {
        val items = GlobalScope.async {
            val db = AppDatabase.getDatabase(activity)
            return@async db.itemDao().getAll()
        }.await().toMutableList()

        itemsListAdapter = ItemsListAdapter(activity, items) {item ->
            if (onItemsListElemClickListener != null) onItemsListElemClickListener?.invoke(item)
        }
        list_view_items?.adapter = itemsListAdapter
    }

    fun deleteTargetItems(activity: Activity) = GlobalScope.launch(Dispatchers.Main) {
        if (itemsListAdapter != null) {
            val targets = itemsListAdapter!!.checkedItems
            GlobalScope.async {
                val db = AppDatabase.getDatabase(activity)
                return@async db.itemDao().deleteAll(targets.toList())
            }.await()
            displayList(activity)
        }
    }

    fun deleteItem(activity: Activity, item: Item) = GlobalScope.launch(Dispatchers.Main) {
        GlobalScope.async {
            val db = AppDatabase.getDatabase(activity)
            return@async db.itemDao().deleteAll(listOf(item))
        }.await()
        displayList(activity)
    }
}