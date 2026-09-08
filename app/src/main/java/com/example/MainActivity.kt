package com.example

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.ui.theme.MyApplicationTheme
import java.io.File

class MainActivity : ComponentActivity() {
  private var webView: WebView? = null

  @SuppressLint("SetJavaScriptEnabled")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Pre-create WebView code cache directories to prevent Chromium simple_file_enumerator opendir error
    try {
      val codeCache = File(cacheDir, "WebView/Default/HTTP Cache/Code Cache")
      File(codeCache, "js").mkdirs()
      File(codeCache, "wasm").mkdirs()
    } catch (e: Exception) {
      Log.w("MainActivity", "Cache dir init: ${e.message}")
    }

    val view = WebView(this).apply {
      layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
      )
      // In containerized Android emulator environments without /dev/dri/renderD*,
      // software layer on WebView disables direct GPU rendernode calls while rendering Canvas and CSS smoothly.
      setLayerType(View.LAYER_TYPE_SOFTWARE, null)
      setBackgroundColor(0xFF060814.toInt())

      settings.apply {
        javaScriptEnabled = true
        domStorageEnabled = true
        databaseEnabled = true
        mediaPlaybackRequiresUserGesture = false
        cacheMode = WebSettings.LOAD_NO_CACHE
        allowFileAccess = true
        allowContentAccess = true
      }
      webViewClient = WebViewClient()
      webChromeClient = object : WebChromeClient() {
        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
          Log.d("AstralWeb", "${consoleMessage?.message()} [${consoleMessage?.sourceId()}:${consoleMessage?.lineNumber()}]")
          return true
        }
      }
      loadUrl("file:///android_asset/index.html")
    }
    webView = view
    setContentView(view)
  }

  override fun onDestroy() {
    webView?.destroy()
    webView = null
    super.onDestroy()
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  MyApplicationTheme { Greeting("Android") }
}

