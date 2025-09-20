package com.example.miam.data
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity data class Product(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val barcode: String? = null,
  val category: String? = null,
  val unit: String,
  val perishable: Boolean = true
)
