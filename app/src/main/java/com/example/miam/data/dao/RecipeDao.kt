
package com.example.miam.data.dao
import androidx.room.*
import com.example.miam.data.Recipe
import com.example.miam.data.RecipeIngredient
@Dao interface RecipeDao {
  @Query("SELECT * FROM Recipe") suspend fun all(): List<Recipe>
  @Query("SELECT * FROM Recipe WHERE (','||mealTags||',') LIKE '%,'||:meal||',%' AND (','||seasonTags||',') LIKE '%,'||:season||',%'")
  suspend fun filter(meal: String, season: String): List<Recipe>
  @Query("SELECT * FROM RecipeIngredient WHERE recipeId = :rid") suspend fun ingredients(rid: Long): List<RecipeIngredient>
  @Insert suspend fun insertRecipe(r: Recipe): Long
  @Insert suspend fun insertIngredients(list: List<RecipeIngredient>)
}
