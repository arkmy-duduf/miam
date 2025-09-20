package com.example.miam.data
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity data class ShoppingItem(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val productName: String,
  val unit: String,
  val qty: Double,
  val checked: Boolean = false
)
