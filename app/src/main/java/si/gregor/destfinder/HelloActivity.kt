package si.gregor.destfinder

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class HelloActivity : AppCompatActivity() {
    var vars: VariableSaving? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hello)
        val text = findViewById<TextView>(R.id.helloText)

        val mPrefs = getSharedPreferences("save", Context.MODE_PRIVATE)
        vars = VariableSaving(mPrefs)

        if(vars!!.noMoreOffers()) {
            text.text = resources.getString(R.string.no_more_offers)
        }
        else {
            val btn = findViewById<Button>(R.id.getstarted)
            val airport = findViewById<Button>(R.id.airportcode)

            btn.visibility= View.VISIBLE
            airport.visibility = View.VISIBLE

            if(vars!!.isFirstTime()) {
                vars!!.setFirstTime()
                text.text = resources.getString(R.string.welcome_message)
                btn.setOnClickListener({v -> setAirportCode(v)})

            }
            else if (intent.hasExtra("error")) {
                text.text = resources.getString(R.string.error_message)
            } else {
                text.text = "Press the button below to get your daily offers."
            }



        }

    }

    fun setAirportCode(v : View) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enter airport IATA code")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        if(android.os.Build.VERSION.SDK_INT >= 26) input.tooltipText = "e.g. LJU for Ljubljana Airport"
        builder.setView(input)
        val positiveButtonClick = { dialog: DialogInterface, _: Int ->
            vars!!.setAirport(input.text.toString())
            getStarted(window.decorView.rootView)

        }

        val negativeButtonClick = { dialog: DialogInterface, _: Int ->
            dialog.cancel()
        }
        builder.setPositiveButton("OK", positiveButtonClick)
        builder.setNegativeButton("Cancel", negativeButtonClick)

        builder.show()
    }

    fun getStarted(v: View) {
        intent = Intent(this@HelloActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
