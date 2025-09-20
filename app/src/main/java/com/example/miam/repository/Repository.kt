
package com.example.miam.repository
import android.content.Context
import com.example.miam.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.BufferedReader

class Repository(private val db: AppDatabase) {
  suspend fun seedIfEmpty(context: Context) = withContext(Dispatchers.IO) {
    if (db.productDao().search("").isNotEmpty()) return@withContext
    val products = readJsonArray(context, "seed_products.json")
    val idMap = mutableMapOf<String, Long>()
    for (i in 0 until products.length()) {
      val o = products.getJSONObject(i)
      val id = db.productDao().insert(Product(
        name = o.getString("name"),
        unit = o.getString("unit"),
        barcode = null,
        category = o.optString("category", null),
        perishable = o.optBoolean("perishable", true)
      ))
      idMap[o.getString("name")] = id
    }
    val recipes = readJsonArray(context, "seed_recipes.json")
    for (i in 0 until recipes.length()) {
      val o = recipes.getJSONObject(i)
      val rid = db.recipeDao().insertRecipe(Recipe(
        title = o.getString("title"),
        minutes = o.getInt("minutes"),
        serves = o.getInt("serves"),
        seasonTags = o.getString("seasonTags"),
        mealTags = o.getString("mealTags")
      ))
      val ings = o.getJSONArray("ingredients")
      val list = mutableListOf<RecipeIngredient>()
      for (j in 0 until ings.length()) {
        val ing = ings.getJSONObject(j)
        val pid = idMap[ing.getString("productName")] ?: continue
        list += RecipeIngredient(recipeId = rid, productId = pid, qty = ing.getDouble("qty"), unit = ing.getString("unit"))
      }
      db.recipeDao().insertIngredients(list)
    }
  }
  private fun readJsonArray(context: Context, asset: String): JSONArray {
    val br: BufferedReader = context.assets.open(asset).bufferedReader()
    val text = br.use { it.readText() }
    return JSONArray(text)
  }
}
