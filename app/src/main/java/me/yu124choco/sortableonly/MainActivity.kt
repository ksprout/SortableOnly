package me.yu124choco.sortableonly

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import me.yu124choco.sortableonly.database.AppDatabase
import me.yu124choco.sortableonly.fragments.ItemsListFragment
import me.yu124choco.sortableonly.util.makeShortToast
import me.yu124choco.sortableonly.models.Item

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupClickListeners()
        updateItemsList()
    }

    private fun updateItemsList() {
        val ft = supportFragmentManager.beginTransaction()
        val fragment = ItemsListFragment()
        val bundle = Bundle()
        bundle.putString("order_rule", ItemsListFragment.ORDER_BY_CUSTOM)
        fragment.arguments = bundle
        ft.replace(R.id.layout_inner, fragment).commit()
    }

    private fun setupClickListeners() {
        image_view_button_add_item.setOnClickListener {
            makeShortToast(this, "アイテムを追加")
        }
    }

    private fun insertTest() = GlobalScope.launch(Dispatchers.Main) {
        GlobalScope.async {
            val db = AppDatabase.getDatabase(this@MainActivity)
            return@async db.itemDao().insertAll(List(5) { i -> Item(null, "アイテム$i", "本文") })
        }.await()
    }

    private fun readTest() = GlobalScope.launch(Dispatchers.Main) {
        val items = GlobalScope.async {
            val db = AppDatabase.getDatabase(this@MainActivity)
            return@async db.itemDao().getAll()
        }.await()
    }
}
