package si.gregor.destfinder

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import android.content.DialogInterface
import android.support.v7.app.AlertDialog


class WebViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        supportActionBar?.title = "Booking"

        val URI = intent.getStringExtra("URI")

        val webView = findViewById<WebView>(R.id.webview)

        webView.webViewClient = WebViewClient()
        webView.clearHistory()
        webView.settings.javaScriptEnabled = true

        webView.loadUrl(URI)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_reload, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // handle button activities
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.mybutton) {
            val webView = findViewById<WebView>(R.id.webview)
            webView.reload()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit this page?")
                .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which -> finish() })
                .setNegativeButton("No", null)
                .show()
    }
}
