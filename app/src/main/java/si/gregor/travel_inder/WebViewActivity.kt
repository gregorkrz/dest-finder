package si.gregor.travel_inder

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient

class WebViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        val intent = intent
        val URI = intent.getStringExtra("URI")

        val webView = findViewById<WebView>(R.id.webview)

        webView.webViewClient = WebViewClient()
        webView.clearHistory()
        webView.settings.javaScriptEnabled = true

        webView.loadUrl(URI)
    }
}
