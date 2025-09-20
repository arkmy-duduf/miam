package com.example.miam.data
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
@Entity(
  foreignKeys=[ForeignKey(entity=Product::class,parentColumns=["id"],childColumns=["productId"],onDelete=ForeignKey.CASCADE)],
  indices=[Index("productId")]
)
data class InventoryItem(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val productId: Long,
  val location: String,
  val quantity: Double,
  val expiresAt: Long? = null
)
