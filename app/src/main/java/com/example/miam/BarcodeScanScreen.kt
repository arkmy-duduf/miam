
package com.example.miam
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun BarcodeScanScreen(nav: NavHostController) {
  val ctx = LocalContext.current
  val lifecycle = LocalLifecycleOwner.current
  val scope = rememberCoroutineScope()
  var lastScanned by remember { mutableStateOf<String?>(null) }

  val options = BarcodeScannerOptions.Builder()
    .setBarcodeFormats(Barcode.FORMAT_EAN_13, Barcode.FORMAT_EAN_8, Barcode.FORMAT_UPC_A, Barcode.FORMAT_UPC_E)
    .build()
  val scanner = remember { BarcodeScanning.getClient(options) }

  val controller = remember {
    LifecycleCameraController(ctx).apply {
      setEnabledUseCases(CameraController.ALL)
      imageAnalysisBackpressureStrategy = ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
      setImageAnalysisAnalyzer(ContextCompat.getMainExecutor(ctx)) { imageProxy: ImageProxy ->
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
          val img = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
          scanner.process(img)
            .addOnSuccessListener { list ->
              val code = list.firstOrNull()?.rawValue
              if (code != null && code != lastScanned) lastScanned = code
            }
            .addOnCompleteListener { imageProxy.close() }
        } else imageProxy.close()
      }
    }
  }

  Column(Modifier.padding(12.dp)) {
    Text("Scanner code-barres", style = MaterialTheme.typography.titleLarge)
    Spacer(Modifier.height(8.dp))
    AndroidView(factory = {
      PreviewView(it).apply {
        controller.bindToLifecycle(lifecycle)
        this.controller = controller
        this.scaleType = PreviewView.ScaleType.FILL_CENTER
      }
    }, modifier = Modifier.height(360.dp))

    lastScanned?.let { code ->
      Spacer(Modifier.height(8.dp)); Text("Trouvé: $code"); Spacer(Modifier.height(4.dp))
      Button(onClick = {
        scope.launch(Dispatchers.IO) {
          val db = com.example.miam.data.AppDatabase.get(ctx)
          val product = db.productDao().search(code).firstOrNull()
          val name = product?.name ?: "Produit ($code)"
          db.shoppingDao().insert(com.example.miam.data.ShoppingItem(productName = name, unit = product?.unit ?: "piece", qty = 1.0))
        }
      }) { Text("Ajouter à la liste d'achats") }
    }
    Spacer(Modifier.height(8.dp))
    OutlinedButton(onClick = { nav.popBackStack() }) { Text("Fermer") }
  }
}
