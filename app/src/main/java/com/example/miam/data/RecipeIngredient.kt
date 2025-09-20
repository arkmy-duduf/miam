
package com.example.miam.data
import androidx.room.Entity
@Entity(primaryKeys=["recipeId","productId"])
data class RecipeIngredient(
  val recipeId: Long,
  val productId: Long,
  val qty: Double,
  val unit: String
)
