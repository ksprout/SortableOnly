package me.yu124choco.sortableonly

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import kotlinx.coroutines.*
import me.yu124choco.sortableonly.database.AppDatabase
import me.yu124choco.sortableonly.util.makeShortToast
import me.yu124choco.sortableonly.models.Item

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        readTest()
        //insertTest()
    }

    private fun insertTest() = GlobalScope.launch(Dispatchers.Main) {
        GlobalScope.async {
            val db = AppDatabase.getDatabase(this@MainActivity)
            return@async db.itemDao().insertAll(listOf(Item(null, "アイテム1", "本文です")))
        }.await()
        makeShortToast(this@MainActivity, "完了しました")
    }

    private fun readTest() = GlobalScope.launch(Dispatchers.Main) {
        val items = GlobalScope.async {
            val db = AppDatabase.getDatabase(this@MainActivity)
            return@async db.itemDao().getAll()
        }.await()
        makeShortToast(this@MainActivity, "完了しました")
    }
}
