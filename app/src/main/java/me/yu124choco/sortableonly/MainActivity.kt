package me.yu124choco.sortableonly

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.*
import me.yu124choco.sortableonly.database.AppDatabase
import me.yu124choco.sortableonly.util.makeShortToast
import me.yu124choco.sortableonly.models.Item

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val a = readTest()
        //val b = insertTest()
    }

    private fun insertTest() = GlobalScope.launch(Dispatchers.Main) {
        runBlocking(Dispatchers.Default) {
            val db = AppDatabase.getDatabase(this@MainActivity)
            db.itemDao().insertAll(listOf(Item(null, "アイテム1", "本文です")))
        }
        makeShortToast(this@MainActivity, "完了しました")
    }

    private fun readTest() = GlobalScope.launch(Dispatchers.Main) {
        val items = runBlocking(Dispatchers.Default) {
            val db = AppDatabase.getDatabase(this@MainActivity)
            db.itemDao().getAll()
        }
        makeShortToast(this@MainActivity, "完了しました")
    }
}
