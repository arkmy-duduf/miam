
package com.example.miam.viewmodel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.miam.data.AppDatabase
import com.example.miam.data.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

data class Suggestion(val recipe: Recipe, val score: Double, val missing: List<String>)
data class SuggestionUI(val people: Int = 2, val meal: String = "midi", val suggestions: List<Suggestion> = emptyList())

class SuggestionViewModel(app: Application): AndroidViewModel(app) {
  private val db = AppDatabase.get(app)
  private val _ui = MutableStateFlow(SuggestionUI())
  val ui = _ui.asStateFlow()

  init { refresh() }

  fun setPeople(n: Int) { _ui.value = _ui.value.copy(people = n.coerceIn(1,6)); refresh() }
  fun setMeal(meal: String) { _ui.value = _ui.value.copy(meal = meal); refresh() }

  private fun scaleQty(baseQty: Double, baseServes: Int, people: Int) = baseQty * people / baseServes.toDouble()
  private fun currentSeasonTag(): String = when(Calendar.getInstance().get(Calendar.MONTH) + 1) {
    12,1,2 -> "hiver"; 3,4,5 -> "printemps"; 6,7,8 -> "été"; else -> "automne"
  }

  fun refresh() = viewModelScope.launch {
    val ui = _ui.value
    val recipes = db.recipeDao().filter(ui.meal, currentSeasonTag())
    val suggestions = recipes.map { r ->
      val ings = db.recipeDao().ingredients(r.id)
      var have = 0; val missing = mutableListOf<String>()
      for (ing in ings) {
        val need = scaleQty(ing.qty, r.serves, ui.people)
        val stock = db.inventoryDao().byProduct(ing.productId).sumOf { it.quantity }
        if (stock + 1e-6 >= need) have++ else missing += (db.productDao().byId(ing.productId)?.name ?: "Ingrédient")
      }
      val cover = if (ings.isNotEmpty()) have.toDouble()/ings.size else 0.0
      Suggestion(r, cover - 0.05 * missing.size, missing)
    }.sortedByDescending { it.score }
    _ui.value = ui.copy(suggestions = suggestions)
  }
}
