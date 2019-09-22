package si.gregor.travel_inder

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

        if (vars!!.isFirstTime()) {
            vars!!.setFirstTime()
            text.text = "This app will show you 5 random trips to different destinations. It works like Tinder - swipe left to discard the current one, and swipe right to book the trip. Once you swipe left, the destination won't be shown again."
            // TODO airport iata code
            val btn = findViewById<Button>(R.id.getstarted)
            btn.visibility= View.VISIBLE
        } else if(vars!!.noMoreOffers()) {
            text.text = "No more offers for today. Come back tomorrow for more :)"
        } else {
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
