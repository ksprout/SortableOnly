package me.yu124choco.sortableonly.database

import androidx.room.Database
import androidx.room.RoomDatabase
import me.yu124choco.sortableonly.models.Item
import me.yu124choco.sortableonly.models.ItemDao

@Database(entities = [Item::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
}