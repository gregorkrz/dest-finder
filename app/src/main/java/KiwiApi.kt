package si.gregor.travel_inder

import android.util.Log
import co.metalab.asyncawait.async
import com.beust.klaxon.Klaxon
import com.soywiz.klock.DateTime
import com.soywiz.klock.days
import com.squareup.picasso.Picasso
import java.net.URL



class KiwiApi (act: MainActivity) {
    var flights: FlightResults? = null
    var activity = act
    companion object {
        fun buildImageUrl(mapIdto: String) : String {
            return "https://images.kiwi.com/photos/1280x720/$mapIdto.jpg"
        }
    }

    fun fetchFlights() {
        async {
            val currentDate = DateTime.now()

            val dateFrom = (currentDate+15.days).format("dd/MM/YYYY")
            val dateTo = (currentDate+20.days).format("dd/MM/YYYY")

            Log.d("",dateFrom)
            Log.d("",dateTo)
            val apiResponse =  await {URL("https://api.skypicker.com/flights?v=2&sort=popularity&asc=0&locale=en&daysInDestinationFrom=4&daysInDestinationTo=9&affilid=&children=0&infants=0&flyFrom=LJU&to=anywhere&featureName=aggregateResults&dateFrom=$dateFrom&dateTo=$dateTo&typeFlight=return&one_per_date=0&oneforcity=1&wait_for_refresh=0&adults=1&limit=45&partner=BkGqKBguDC0OhGsiIGVuelX5cJT7IAQR").readText()}
            prepareFlights(apiResponse)
        }


    }

    fun prepareFlights(ApiResponse: String) {
        async {
            flights = await { Klaxon().parse<FlightResults>(ApiResponse) }
            flights = FlightResults(currency = flights?.currency, data = flights!!.data.take(5)) // only keep 5 random flights (TODO)
            if(flights?.data != null && flights?.data!!.size > 0) {
                // preload destination images into cache
                for(destination in flights!!.data) Picasso.get().load(buildImageUrl(destination.mapIdto))

            } else {
                flights?.error = true
            }
            activity.FlightsAreReady(flights!!)
        }




    }
     class FlightResults (var currency: String?, var data: List<FlightDetails>, var error: Boolean? = false)
     class FlightDetails(var price: Int, var cityTo: String, var mapIdto: String, var deep_link: String, var fly_duration: String, var nightsInDest: Int)
}
