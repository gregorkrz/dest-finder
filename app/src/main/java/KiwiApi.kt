package si.gregor.destfinder

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.util.Log
import co.metalab.asyncawait.async
import com.google.gson.Gson
import com.soywiz.klock.DateTime
import com.soywiz.klock.DateTimeTz
import com.soywiz.klock.days
import com.squareup.picasso.Picasso

import java.net.URL



class KiwiApi (act: MainActivity, _vars: VariableSaving?) {
    var flights: FlightResults? = null
    var activity = act
    val vars = _vars
    companion object {
        fun buildImageUrl(mapIdto: String) : String {
            return "https://images.kiwi.com/photos/1280x720/$mapIdto.jpg"
        }
    }

    fun fetchFlights() {
        async {
            val currentDate = DateTimeTz.nowLocal()

            val dateFrom = (currentDate+14.days).format("dd/MM/YYYY")
            val dateTo = (currentDate+21.days).format("dd/MM/YYYY")

            Log.d("",currentDate.format("dd/MM/YYYY"))

            try {
                val airport = vars!!.getAirport()
                val apiResponse =  await {URL("https://api.skypicker.com/flights?v=2&sort=popularity&asc=0&price_to=350&locale=en&daysInDestinationFrom=4&daysInDestinationTo=7&affilid=&children=0&infants=0&flyFrom=$airport&to=anywhere&featureName=aggregateResults&dateFrom=$dateFrom&dateTo=$dateTo&typeFlight=return&one_per_date=0&oneforcity=1&wait_for_refresh=0&adults=1&limit=45&partner=BkGqKBguDC0OhGsiIGVuelX5cJT7IAQR").readText()}
                prepareFlights(apiResponse)
            } catch(e: Exception) {
                launchErrorScreen(e)
            }

        }
    }


    fun prepareFlights(ApiResponse: String) {
        async {
            try {
                flights = await { Gson().fromJson<KiwiApi.FlightResults>(ApiResponse, KiwiApi.FlightResults::class.java) }
                flights = FlightResults(currency = flights?.currency, data = getRandomElements(flights!!.data)!!)

                if(flights?.data != null && flights?.data!!.isNotEmpty()) {
                    // preload destination images into cache
                    for(destination in flights!!.data) Picasso.get().load(buildImageUrl(destination.mapIdto))

                } else {
                    flights?.error = true
                }
                activity.FlightsAreReady(flights!!)
            } catch(e: Exception) {
                launchErrorScreen(e)
            }

        }
    }



     class FlightResults (var currency: String?, var data: List<FlightDetails>, var error: Boolean? = false)
     class FlightDetails(var price: Int, var cityTo: String, var mapIdto: String, var deep_link: String, var fly_duration: String, var nightsInDest: Int, var dTime: Long)

    private fun getRandomElements(list: List<FlightDetails>): List<FlightDetails>? {
        val vars = VariableSaving(activity.getSharedPreferences("save", Context.MODE_PRIVATE))
        var tripList = arrayListOf<FlightDetails>()
        for (trip in list.shuffled()) {
            if(vars.canDisplay(trip.mapIdto)) {
                tripList.add(trip)
            }
            if (tripList.size >= activity.resources.getInteger(R.integer.max_daily_offers)) break
        }
        return tripList
    }

    fun launchErrorScreen(e: Exception) {
        Log.e("Error", "$e")
        val intent = Intent(activity, HelloActivity::class.java)
        intent.putExtra("error", true)
        activity.startActivity(intent)
        activity.finish()
    }
}
