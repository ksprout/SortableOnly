package me.yu124choco.sortableonly.models

import androidx.room.*

@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true) var id: Int?,
    @ColumnInfo(name = "name") var name: String?,
    @ColumnInfo(name = "description") var description: String?,
    @ColumnInfo(name = "order") var order: Int?
)

@Dao
interface ItemDao {
    @Query("SELECT * FROM items WHERE id = :i")
    fun get(i: Int): Item

    @Query("SELECT * FROM items")
    fun getAll(): List<Item>

    @Insert
    fun insertAll(items: List<Item>)

    @Update
    fun updateAll(items: List<Item>)

    @Delete
    fun deleteAll(items: List<Item>)
}