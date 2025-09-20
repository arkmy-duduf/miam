
package com.example.miam.data
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity data class Recipe(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String,
  val minutes: Int,
  val serves: Int,
  val seasonTags: String, // "hiver;ete;..."
  val mealTags: String    // "midi;soir"
)
