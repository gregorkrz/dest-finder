package si.gregor.destfinder

import android.content.SharedPreferences
import com.soywiz.klock.DateTimeTz
import com.google.gson.Gson





class VariableSaving (_mPrefs: SharedPreferences){
    private val mPrefs = _mPrefs

    fun isFirstTime(): Boolean {
        return !mPrefs.contains("already_opened")
    }
    fun noMoreOffers(): Boolean {
        val today = (DateTimeTz.nowLocal()).format("dd/MM/YYYY")
        return (mPrefs.contains("date.$today.lastIndex") && mPrefs.getInt("date.$today.lastIndex", 0) == -3)
    }

    fun setNoMoreOffers() {
        val today = (DateTimeTz.nowLocal()).format("dd/MM/YYYY")
        val prefsEditor = mPrefs.edit()
        prefsEditor.putInt("date.$today.lastIndex", -3)
        prefsEditor.remove("date.$today.flights")
        prefsEditor.commit()
    }

    fun getFlights(): KiwiApi.FlightResults {
        val gson = Gson()
        val today = (DateTimeTz.nowLocal()).format("dd/MM/YYYY")
        val json = mPrefs.getString("date.$today.flights", "")
        return gson.fromJson<KiwiApi.FlightResults>(json, KiwiApi.FlightResults::class.java)
    }

    fun getLastIndex(): Int {
        val today = (DateTimeTz.nowLocal()).format("dd/MM/YYYY")
        return mPrefs.getInt("date.$today.lastIndex", -2)
    }

    fun setFlights(flights: KiwiApi.FlightResults?) {
        val today = (DateTimeTz.nowLocal()).format("dd/MM/YYYY")
        val prefsEditor = mPrefs.edit()
        val gson = Gson()
        val json = gson.toJson(flights!!)
        prefsEditor.putString("date.$today.flights", json)
        prefsEditor.commit()
    }

    fun setLastIndex(lastIndex: Int) {
        val today = (DateTimeTz.nowLocal()).format("dd/MM/YYYY")
        val prefsEditor = mPrefs.edit()
        prefsEditor.putInt("date.$today.lastIndex", lastIndex)
        prefsEditor.commit()
    }

    fun setFirstTime() {
        val prefsEditor = mPrefs.edit()
        prefsEditor.putBoolean("already_opened", true)
        prefsEditor.commit()
    }

    fun alreadyUsed(city: String) {
        val prefsEditor = mPrefs.edit()
        val cities = mPrefs.getString("already_used_cities","")
        prefsEditor.putString("already_used_cities", cities.plus(",").plus(city))
        prefsEditor.commit()
    }

    fun canDisplay(city: String): Boolean {
        val count = mPrefs.getString("already_used_cities","").count{ ",".contains(it) }
        if (count > 40) {
            val prefsEditor = mPrefs.edit()
            prefsEditor.putString("already_opened", "")
            prefsEditor.commit()
        }
        return ! mPrefs.getString("already_used_cities","").contains(city, ignoreCase = true)
    }
}