package si.gregor.destfinder
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.support.constraint.ConstraintLayout
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Callback
import android.content.Intent
import com.airbnb.lottie.LottieAnimationView
import com.soywiz.klock.DateTime
import com.soywiz.klock.DateTimeTz


class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE = 3435

    private var touchy = true

    private var flights: KiwiApi.FlightResults? = null
    private var lastIndex = -1

    private var vars : VariableSaving? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.title="Daily offers"
        val mainLayout : ConstraintLayout = findViewById(R.id.mainLayout)

        mainLayout.setOnTouchListener(OnSwipeTouchListener(this@MainActivity, SwipingFuncs()))

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        vars = VariableSaving(getSharedPreferences("save", Context.MODE_PRIVATE))

        /////// start loading ///////
        isLoading(true)
        lastIndex = vars!!.getLastIndex()
        Log.d("LASTINDEX","$lastIndex")
        if (lastIndex == -2) {
            // fetch new 5 trips
            val kiwi = KiwiApi(this@MainActivity, vars)
            kiwi.fetchFlights()
            lastIndex = -1
        } else {
            flights = vars!!.getFlights()
            Log.d("Flights","$flights")

            val view = findViewById<ImageView>(R.id.imageView)
            view.post(Runnable {
                // run when layout is laid out on the screen
                nextDestination()
            })

        }

        ///////////////////////////
    }


    fun nextDestination() {

        isLoading(true)

        lastIndex++
        vars!!.setLastIndex(lastIndex)

        val view = findViewById<ImageView>(R.id.imageView)

        if (lastIndex >= flights!!.data.size) {
            view.visibility = View.INVISIBLE
            vars!!.setNoMoreOffers()
            val intent = Intent(this@MainActivity, HelloActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        class Cb(_currentFlight: KiwiApi.FlightDetails): Callback {
            // this gets called on image load
            val currentFlight = _currentFlight
            override fun onSuccess() {
                isLoading(false)
                val cityName = findViewById<TextView>(R.id.cityName)
                val additionalInfo = findViewById<TextView>(R.id.additionalInfo)
                val price = findViewById<TextView>(R.id.price)
                cityName.text = currentFlight.cityTo
                val depDate = DateTime.fromUnix(currentFlight.dTime*1000).format("dd.MM.YYYY")
                additionalInfo.text = "Flight time ${currentFlight.fly_duration} | Dep $depDate | ${currentFlight.nightsInDest} Nights"
                price.text = "${currentFlight.price} ${flights!!.currency}"
                vars!!.alreadyUsed(currentFlight.mapIdto)

            }

            override fun onError(e: Exception) {
                lastIndex--
                vars!!.setLastIndex(lastIndex)
                Log.e("Error", "$e")
                val intent = Intent(this@MainActivity, HelloActivity::class.java)
                intent.putExtra("error", true)
                startActivity(intent)
                finish()
            }
        }

        val currentFlight : KiwiApi.FlightDetails = flights!!.data[lastIndex]
        Picasso.get().load(KiwiApi.buildImageUrl(currentFlight.mapIdto)).noFade().resize(view.width,view.height).centerCrop().into(view,
                Cb(currentFlight))


    }

    fun isLoading(loading: Boolean) {
        var visibility = View.VISIBLE
        if (loading) {
            visibility = View.INVISIBLE
            touchy = false
        } else touchy = true
        val cityName = findViewById<TextView>(R.id.cityName)
        val additionalInfo = findViewById<TextView>(R.id.additionalInfo)
        val price = findViewById<TextView>(R.id.price)

        // hide all
        cityName.visibility = visibility
        additionalInfo.visibility = visibility
        price.visibility = visibility

        val loading = findViewById<LottieAnimationView>(R.id.loading)
        if(visibility==View.INVISIBLE) loading.visibility = View.VISIBLE
        else loading.visibility = View.INVISIBLE
    }



    fun FlightsAreReady(res: KiwiApi.FlightResults) {
        // cal
        flights = res
        vars!!.setFlights(flights)
        nextDestination()
    }

    inner class SwipingFuncs {
        fun onSwipeRight() {
            if (touchy) {
                val browserIntent = Intent(this@MainActivity, WebViewActivity::class.java)
                browserIntent.putExtra("URI", flights!!.data[lastIndex].deep_link)
                startActivityForResult(browserIntent, REQUEST_CODE)
            }

        }

        fun onSwipeLeft() {
            if(touchy) nextDestination()
        }
        fun onSwipeBottom() {}
        fun onSwipeTop() {}
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE) {
                nextDestination()
        }
    }




}


