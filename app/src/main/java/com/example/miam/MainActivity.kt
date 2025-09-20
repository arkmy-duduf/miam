
package com.example.miam
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.miam.ui.theme.MiamTheme
import com.example.miam.viewmodel.InventoryViewModel
import com.example.miam.viewmodel.SuggestionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent { App() }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
  MiamTheme {
    val nav = rememberNavController()
    Scaffold(topBar = { TopAppBar(title = { Text("Miam") }) }) { padding ->
      NavHost(navController = nav, startDestination = "inventory", modifier = Modifier.padding(padding)) {
        composable("inventory") { InventoryScreen(nav) }
        composable("suggestions") { SuggestionsScreen(nav) }
        composable("shopping") { ShoppingListScreen(nav) }
        composable("scan") { BarcodeScanScreen(nav) }
      }
    }
  }
}

@Composable
fun InventoryScreen(nav: NavHostController, vm: InventoryViewModel = hiltViewModel()) {
  val state by vm.state.collectAsState()
  val scope = rememberCoroutineScope()
  Column(Modifier.fillMaxSize().padding(16.dp)) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
      Button(onClick = { nav.navigate("suggestions") }) { Text("Suggestions") }
      Button(onClick = { nav.navigate("shopping") }) { Text("Courses") }
      Button(onClick = { nav.navigate("scan") }) { Text("Scan") }
      Button(onClick = { scope.launch { vm.seedIfEmpty() } }) { Text("Charger exemples") }
    }
    Spacer(Modifier.height(8.dp))
    LazyColumn(Modifier.weight(1f)) {
      state.grouped.forEach { (loc, itemsList) ->
        item { Text(loc, style = MaterialTheme.typography.titleLarge) }
        items(itemsList) { it ->
          ListItem(
            headlineContent = { Text(it.productName) },
            supportingContent = { Text("${it.quantity} ${it.unit} • ${it.location}") },
            trailingContent = {
              Row {
                OutlinedButton(onClick = { vm.consume(it.id, 1.0) }) { Text("-1") }
                Spacer(Modifier.width(6.dp))
                OutlinedButton(onClick = { vm.addOne(it.productId) }) { Text("+1") }
              }
            }
          )
        }
      }
    }
    Button(onClick = { vm.addQuick("Yaourt", "piece", 1.0, "FRIDGE") }, modifier = Modifier.fillMaxWidth()) { Text("Ajouter +1 Yaourt (exemple)") }
  }
}
