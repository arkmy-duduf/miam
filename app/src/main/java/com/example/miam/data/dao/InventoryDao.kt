
package com.example.miam.data.dao
import androidx.room.*
import com.example.miam.data.InventoryItem
@Dao interface InventoryDao {
  @Query("SELECT * FROM InventoryItem WHERE productId = :pid") suspend fun byProduct(pid: Long): List<InventoryItem>
  @Query("SELECT * FROM InventoryItem") suspend fun all(): List<InventoryItem>
  @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(item: InventoryItem): Long
  @Query("UPDATE InventoryItem SET quantity = quantity - :delta WHERE id = :id") suspend fun consume(id: Long, delta: Double)
  @Query("DELETE FROM InventoryItem WHERE id = :id") suspend fun delete(id: Long)
}
