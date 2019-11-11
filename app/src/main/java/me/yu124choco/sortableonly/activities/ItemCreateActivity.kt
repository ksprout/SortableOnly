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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_create)

        button_submit.setOnClickListener {
            if (!buttonClicked) {
                buttonClicked = true
                val name = edit_text_name.text.toString()
                val description = edit_text_description.text.toString()
                Handler().postDelayed({
                    saveItem(name, description)
                }, 300)
            }
        }
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
        val intent = Intent()
        intent.putExtra("item_saved", true)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

}