package me.yu124choco.sortableonly.models

import androidx.room.*

@Entity
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "description") val description: String?
)

@Dao
interface ItemDao {
    @Query("SELECT * FROM item")
    fun getAll(): List<Item>

    @Insert
    fun insertAll(items: List<Item>)

    @Delete
    fun deleteAll(items: List<Item>)
}