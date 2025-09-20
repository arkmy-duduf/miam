package com.example.miam.data.dao
import androidx.room.*
import com.example.miam.data.Product
@Dao interface ProductDao {
  @Query("SELECT * FROM Product WHERE name LIKE '%'||:q||'%' OR barcode = :q LIMIT 50")
  suspend fun search(q: String): List<Product>
  @Query("SELECT * FROM Product WHERE id = :id") suspend fun byId(id: Long): Product?
  @Insert suspend fun insert(p: Product): Long
}
