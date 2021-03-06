package me.yu124choco.sortableonly

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.animation.TranslateAnimation
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import me.yu124choco.sortableonly.activities.ItemCreateActivity
import me.yu124choco.sortableonly.database.AppDatabase
import me.yu124choco.sortableonly.fragments.ItemShowDialogFragment
import me.yu124choco.sortableonly.fragments.ItemsListFragment
import me.yu124choco.sortableonly.util.makeShortToast
import me.yu124choco.sortableonly.models.Item

class MainActivity : AppCompatActivity() {

    private val RESULT_ITEM_CREATE = 1000

    private var itemsListFragment: ItemsListFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupClickListeners()
        updateItemsList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                RESULT_ITEM_CREATE -> {
                    when (data.getIntExtra("saved_status", 0)) {
                        1 -> {
                            updateItemsList()
                            makeShortToast(this, "アイテムが追加されました")
                        }
                        2 -> {
                            updateItemsList()
                            makeShortToast(this, "アイテムが更新されました")
                        }
                    }
                }
            }
        }
    }

    private fun updateItemsList() {
        val ft = supportFragmentManager.beginTransaction()
        val bundle = Bundle()
        bundle.putString("order_rule", ItemsListFragment.ORDER_BY_CUSTOM)

        itemsListFragment?.updateList(this) ?: ItemsListFragment().run {
            onItemsListElemClickListener = { item ->
                ItemShowDialogFragment().run {
                    this.item = item
                    editButtonClickListener = { i ->
                        dismiss()
                        val intent = Intent(application, ItemCreateActivity::class.java)
                        intent.putExtra("target_item_id", i.id)
                        startActivityForResult(intent, RESULT_ITEM_CREATE)
                    }
                    deleteButtonClickListener = { i ->
                        dismiss()
                        deleteItem(this@MainActivity, i)
                    }
                    show(supportFragmentManager, "item_show_dialog")
                }
            }
            arguments = bundle
            ft.replace(R.id.layout_inner, this).commit()
            itemsListFragment = this
        }
    }

    private fun setupClickListeners() {
        button_add_item.setOnClickListener {
            Handler().postDelayed({
                val intent = Intent(application, ItemCreateActivity::class.java)
                intent.putExtra("target_item_id", -1)
                startActivityForResult(intent, RESULT_ITEM_CREATE)
            }, 500)
        }

        // ホバーアニメーションを設定
        button_add_item.setOnTouchListener { v, event ->
            val delta = 4 * resources.displayMetrics.density
            val duration = 200L
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val anim = TranslateAnimation(0f, 0f, 0f, delta)
                    anim.duration = duration
                    anim.fillAfter = true
                    button_add_item.startAnimation(anim)
                }
                MotionEvent.ACTION_UP -> {
                    val anim = TranslateAnimation(0f, 0f, delta, 0f)
                    anim.duration = duration
                    anim.fillAfter = true
                    button_add_item.startAnimation(anim)
                }
            }
            return@setOnTouchListener false
        }

        button_delete.setOnClickListener {
            itemsListFragment?.deleteTargetItems(this)
        }
    }

    private fun insertTest() = GlobalScope.launch(Dispatchers.Main) {
        GlobalScope.async {
            val db = AppDatabase.getDatabase(this@MainActivity)
            return@async db.itemDao().insertAll(List(5) { i -> Item(null, "アイテム$i", "本文", 0) })
        }.await()
    }

    private fun readTest() = GlobalScope.launch(Dispatchers.Main) {
        val items = GlobalScope.async {
            val db = AppDatabase.getDatabase(this@MainActivity)
            return@async db.itemDao().getAll()
        }.await()
    }
}
