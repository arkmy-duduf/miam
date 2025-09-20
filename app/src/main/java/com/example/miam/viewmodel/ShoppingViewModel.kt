
package com.example.miam.viewmodel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.miam.data.AppDatabase
import com.example.miam.data.ShoppingItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ShoppingState(val items: List<ShoppingItem> = emptyList())

@HiltViewModel
class ShoppingViewModel @Inject constructor(app: Application): AndroidViewModel(app) {
  private val db = AppDatabase.get(app)
  private val _state = MutableStateFlow(ShoppingState())
  val state = _state.asStateFlow()
  init { refresh() }
  fun refresh() = viewModelScope.launch { _state.value = ShoppingState(db.shoppingDao().all()) }
  fun add(name: String, unit: String, qty: Double) = viewModelScope.launch { db.shoppingDao().insert(ShoppingItem(productName=name, unit=unit, qty=qty)); refresh() }
  fun toggle(id: Long, checked: Boolean) = viewModelScope.launch { db.shoppingDao().setChecked(id, checked); refresh() }
  fun remove(id: Long) = viewModelScope.launch { db.shoppingDao().delete(id); refresh() }
}
