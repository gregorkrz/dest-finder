package si.gregor.destfinder

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class HelloActivity : AppCompatActivity() {
    var vars: VariableSaving? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hello)
        val text = findViewById<TextView>(R.id.helloText)

        val mPrefs = getSharedPreferences("save", Context.MODE_PRIVATE)
        vars = VariableSaving(mPrefs)

        if (vars!!.isFirstTime() || intent.hasExtra("error")) {
            vars!!.setFirstTime()
            if (intent.hasExtra("error")) {
                text.text = resources.getString(R.string.error_message)
            } else {
                text.text = resources.getString(R.string.welcome_message)
            }

            // TODO airport iata code
            val btn = findViewById<Button>(R.id.getstarted)
            btn.visibility= View.VISIBLE
        } else if(vars!!.noMoreOffers()) {
            text.text = resources.getString(R.string.no_more_offers)
        }  else {
            intent = Intent(this@HelloActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    fun getStarted(v: View) {
        intent = Intent(this@HelloActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
