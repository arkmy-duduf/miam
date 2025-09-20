
package com.example.miam.viewmodel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.miam.data.*
import com.example.miam.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InventoryRow(val id: Long, val productId: Long, val productName: String, val unit: String, val location: String, val quantity: Double)
data class InventoryState(val grouped: Map<String, List<InventoryRow>> = emptyMap())

@HiltViewModel
class InventoryViewModel @Inject constructor(
  app: Application,
  private val repo: Repository
): AndroidViewModel(app) {
  private val db = AppDatabase.get(app)
  private val _state = MutableStateFlow(InventoryState())
  val state = _state.asStateFlow()

  init { refresh() }

  fun refresh() = viewModelScope.launch {
    val rows = db.inventoryDao().all().mapNotNull { item ->
      val p = db.productDao().byId(item.productId) ?: return@mapNotNull null
      InventoryRow(item.id, p.id, p.name, p.unit, item.location, item.quantity)
    }
    _state.value = InventoryState(rows.groupBy { it.location })
  }

  fun addQuick(name: String, unit: String, qty: Double, location: String) = viewModelScope.launch {
    val existing = db.productDao().search(name).firstOrNull { it.name.equals(name, true) }
    val pid = existing?.id ?: db.productDao().insert(Product(name=name, unit=unit, perishable=true))
    db.inventoryDao().upsert(InventoryItem(productId=pid, location=location, quantity=qty))
    refresh()
  }

  fun addOne(productId: Long) = viewModelScope.launch {
    db.inventoryDao().upsert(InventoryItem(productId=productId, location="FRIDGE", quantity=1.0))
    refresh()
  }

  fun consume(id: Long, delta: Double) = viewModelScope.launch {
    db.inventoryDao().consume(id, delta); refresh()
  }

  fun seedIfEmpty() = viewModelScope.launch {
    repo.seedIfEmpty(getApplication())
    refresh()
  }
}
