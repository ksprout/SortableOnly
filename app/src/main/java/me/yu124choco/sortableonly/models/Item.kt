package me.yu124choco.sortableonly.models

import androidx.annotation.NonNull
import androidx.room.*

@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true) var id: Long?,
    var name: String?,
    var description: String?,
    @ColumnInfo(name = "order_number", defaultValue = "0") @NonNull var orderNumber: Long
)

@Dao
interface ItemDao {
    @Query("SELECT * FROM items WHERE id = :i")
    fun get(i: Long): Item

    @Query("SELECT * FROM items ORDER BY order_number DESC")
    fun getAll(): List<Item>

    @Query("SELECT MAX(order_number) FROM items")
    fun getMaxOrderNumber(): Long

    @Insert
    fun insertAllMain(items: List<Item>)

    @Transaction
    fun insertAll(items: List<Item>) {
        var max = getMaxOrderNumber() + 1
        for (item in items) {
            item.orderNumber = max
            max++
        }
        insertAllMain(items)
    }

    @Update
    fun updateAll(items: List<Item>)

    @Delete
    fun deleteAll(items: List<Item>)
}