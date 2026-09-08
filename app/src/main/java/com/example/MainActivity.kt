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

    val view = WebView(this).apply {
      layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
      )
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

  override fun onPause() {
    super.onPause()
    webView?.onPause()
    webView?.pauseTimers()
  }

  override fun onResume() {
    super.onResume()
    webView?.onResume()
    webView?.resumeTimers()
  }

  override fun onStop() {
    super.onStop()
    webView?.onPause()
    webView?.pauseTimers()
  }

  override fun onDestroy() {
    webView?.let {
      it.loadUrl("about:blank")
      it.onPause()
      it.destroy()
    }
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

