package si.gregor.travel_inder

import android.content.Context
import android.content.SharedPreferences
import com.soywiz.klock.DateTimeTz
import com.soywiz.klock.days
import android.R.id.edit
import android.util.Log
import com.google.gson.Gson





class VariableSaving (_mPrefs: SharedPreferences){
    val mPrefs = _mPrefs
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
}