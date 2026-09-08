package com.example

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.ConsoleMessage
import android.webkit.RenderProcessGoneDetail
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
    window.setFlags(
      WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
      WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
    )
    enableEdgeToEdge()

    // Clean up any incomplete HTTP Cache folder created manually by earlier runs
    // to allow Chromium SimpleCache to initialize cleanly
    try {
      val httpCache = File(cacheDir, "WebView/Default/HTTP Cache")
      if (httpCache.exists()) {
        val fakeIndex = File(httpCache, "fake-index")
        val realIndex = File(httpCache, "the-real-index")
        if (!fakeIndex.exists() && !realIndex.exists()) {
          httpCache.deleteRecursively()
        }
      }
    } catch (e: Exception) {
      Log.w("MainActivity", "Cache check: ${e.message}")
    }

    val view = WebView(this).apply {
      layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
      )
      setLayerType(View.LAYER_TYPE_HARDWARE, null)
      setBackgroundColor(0xFF060814.toInt())

      settings.apply {
        javaScriptEnabled = true
        domStorageEnabled = true
        databaseEnabled = true
        mediaPlaybackRequiresUserGesture = false
        cacheMode = WebSettings.LOAD_DEFAULT
        allowFileAccess = true
        allowContentAccess = true
      }
      webViewClient = object : WebViewClient() {
        override fun onRenderProcessGone(view: WebView?, detail: RenderProcessGoneDetail?): Boolean {
          Log.w("MainActivity", "Render process gone; didCrash=${detail?.didCrash()}")
          return true
        }
      }
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

