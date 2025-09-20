
package com.example.miam
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.miam.viewmodel.ShoppingViewModel

@Composable
fun ShoppingListScreen(nav: NavHostController, vm: ShoppingViewModel = hiltViewModel()) {
  val state by vm.state.collectAsState()
  var name by remember { mutableStateOf("") }
  var qty by remember { mutableStateOf("1.0") }
  var unit by remember { mutableStateOf("piece") }

  Column(Modifier.fillMaxSize().padding(16.dp)) {
    Row { OutlinedButton(onClick = { nav.popBackStack() }) { Text("← Retour") } }
    Spacer(Modifier.height(8.dp))
    Row(Modifier.fillMaxWidth()) {
      OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Produit") }, modifier = Modifier.weight(1f))
      Spacer(Modifier.width(6.dp))
      OutlinedTextField(value = qty, onValueChange = { qty = it }, label = { Text("Qté") }, modifier = Modifier.width(100.dp))
      Spacer(Modifier.width(6.dp))
      OutlinedTextField(value = unit, onValueChange = { unit = it }, label = { Text("Unité") }, modifier = Modifier.width(120.dp))
    }
    Spacer(Modifier.height(6.dp))
    Button(onClick = { name.takeIf { it.isNotBlank() }?.let { vm.add(it, unit, qty.toDoubleOrNull() ?: 1.0); name="" } }) { Text("Ajouter") }
    Spacer(Modifier.height(12.dp))
    LazyColumn {
      items(state.items) { it ->
        ListItem(
          headlineContent = { Text(it.productName) },
          supportingContent = { Text("${it.qty} ${it.unit}") },
          leadingContent = { Checkbox(checked = it.checked, onCheckedChange = { c -> vm.toggle(it.id, c) }) },
          trailingContent = { OutlinedButton(onClick = { vm.remove(it.id) }) { Text("Suppr") } }
        ); Divider()
      }
    }
  }
}
