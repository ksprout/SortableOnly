package me.yu124choco.sortableonly.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_item_create.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import me.yu124choco.sortableonly.R
import me.yu124choco.sortableonly.database.AppDatabase
import me.yu124choco.sortableonly.models.Item

class ItemCreateActivity : AppCompatActivity() {

    // 保存ボタンの重複クリックを防ぐ
    private var buttonClicked = false
    private var isEdit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_create)

        val targetItemId = intent.getIntExtra("target_item_id", -1)
        if (targetItemId != -1) {
            isEdit = true
            showItem(targetItemId)
        }

        button_submit.setOnClickListener {
            if (!buttonClicked) {
                buttonClicked = true
                val name = edit_text_name.text.toString()
                val description = edit_text_description.text.toString()
                Handler().postDelayed({
                    if (isEdit) {
                        updateItem(targetItemId, name, description)
                    } else {
                        saveItem(name, description)
                    }
                }, 300)
            }
        }
    }

    private fun showItem(itemId: Int) = GlobalScope.launch(Dispatchers.Main) {
        val item = GlobalScope.async {
            val db = AppDatabase.getDatabase(this@ItemCreateActivity)
            return@async db.itemDao().get(itemId)
        }.await()
        edit_text_name.setText(item.name)
        edit_text_description.setText(item.description)
    }

    /**
     * EditTextの情報をもとにItemを作成して保存し、メイン画面に戻る
     * @args name Itemの名前
     * @args description Itemの説明
     */
    private fun saveItem(name: String, description: String) = GlobalScope.launch(Dispatchers.Main) {
        GlobalScope.async {
            val db = AppDatabase.getDatabase(this@ItemCreateActivity)
            return@async db.itemDao().insertAll(listOf(Item(null, name, description)))
        }.await()
        backToHome()
    }

    private fun updateItem(itemId: Int, name: String, description: String) = GlobalScope.launch(Dispatchers.Main) {
        GlobalScope.async {
            val db = AppDatabase.getDatabase(this@ItemCreateActivity)
            val item = db.itemDao().get(itemId)
            item.name = name
            item.description = description
            return@async db.itemDao().updateAll(listOf(item))
        }.await()
        backToHome()
    }

    private fun backToHome() {
        val intent = Intent()
        intent.putExtra("saved_status", if (isEdit) 2 else 1)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}