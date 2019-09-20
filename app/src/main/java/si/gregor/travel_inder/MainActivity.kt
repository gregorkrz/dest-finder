package si.gregor.travel_inder

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.support.constraint.ConstraintLayout
import android.transition.Visibility
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.squareup.picasso.Picasso
import com.squareup.picasso.Callback
import android.support.v4.content.ContextCompat.startActivity
import android.content.Intent
import android.net.Uri


class MainActivity : AppCompatActivity() {
    var flights: KiwiApi.FlightResults? = null
    val REQUEST_CODE = 3435
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val textView: ConstraintLayout = findViewById(R.id.mainLayout)

        textView.setOnTouchListener(OnSwipeTouchListener(this@MainActivity, SwipingFuncs()));

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    var currentIndex = 0

    fun nextDestination() {
        val view = findViewById<ImageView>(R.id.imageView)
        val cityName = findViewById<TextView>(R.id.cityName)
        val additionalInfo = findViewById<TextView>(R.id.additionalInfo)
        val price = findViewById<TextView>(R.id.price)

        // hide all
        cityName.visibility = View.INVISIBLE
        additionalInfo.visibility = View.INVISIBLE
        price.visibility = View.INVISIBLE

        if (currentIndex >= flights!!.data.size) {
            currentIndex = 0

            // that's all for today (TODO)
        };
        class Cb(_currentFlight: KiwiApi.FlightDetails): Callback {
            val currentFlight = _currentFlight
            override fun onSuccess() {
                cityName.setText(currentFlight.cityTo)
                additionalInfo.text = "Flight time ${currentFlight.fly_duration} | ${currentFlight.nightsInDest} Nights"
                price.text = "${currentFlight.price} ${flights!!.currency}"

                cityName.visibility = View.VISIBLE
                additionalInfo.visibility = View.VISIBLE
                price.visibility = View.VISIBLE

            }

            override fun onError(e: Exception) {
                Toast.makeText(getApplicationContext(), "An error occurred.", Toast.LENGTH_LONG).show()
            }
        }
        val currentFlight : KiwiApi.FlightDetails = flights!!.data[currentIndex]
        Picasso.get().load(KiwiApi.buildImageUrl(currentFlight.mapIdto)).noFade().resize(view.getWidth()*2,view.getHeight()*2).centerCrop().into(view,
                Cb(currentFlight));

        currentIndex++
    }

    fun FlightsAreReady(res: KiwiApi.FlightResults) {
        // cal
        flights = res
        nextDestination()
    }
    inner class SwipingFuncs {

        fun onSwipeRight() {

            val browserIntent = Intent(this@MainActivity, WebViewActivity::class.java)
            browserIntent.putExtra("URI", flights!!.data[currentIndex].deep_link)
            startActivityForResult(browserIntent, REQUEST_CODE)
        }

        fun onSwipeLeft() {
            nextDestination()
        }

        fun onSwipeTop() {

        }

        fun onSwipeBottom() {
            val kiwi = KiwiApi(this@MainActivity)
            kiwi.fetchFlights()
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE) {
                nextDestination()
        }
    }




}


