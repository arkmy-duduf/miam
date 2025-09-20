
package com.example.miam.data.dao
import androidx.room.*
import com.example.miam.data.ShoppingItem
@Dao interface ShoppingDao {
  @Query("SELECT * FROM ShoppingItem ORDER BY checked ASC, productName ASC") suspend fun all(): List<ShoppingItem>
  @Insert suspend fun insert(item: ShoppingItem): Long
  @Query("UPDATE ShoppingItem SET checked = :checked WHERE id = :id") suspend fun setChecked(id: Long, checked: Boolean)
  @Query("DELETE FROM ShoppingItem WHERE id = :id") suspend fun delete(id: Long)
}
