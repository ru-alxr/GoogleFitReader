package mx.alxr.googlefitreader.repository

import android.app.IntentService
import android.content.Intent
import com.google.android.gms.fitness.data.DataUpdateNotification
import java.util.concurrent.TimeUnit

class GoogleFitUpdateIntentService : IntentService("GoogleFitUpdateIntentService") {

    init {
        println("GoogleFitUpdateIntentService##CONSTRUCTOR")
    }

    override fun onHandleIntent(intent: Intent?) {
        println("GoogleFitUpdateIntentService##onHandleIntent")
        val update = DataUpdateNotification.getDataUpdateNotification(intent!!)
        // Show the time interval over which the data points were collected.
        // To extract specific data values, in this case the user's weight,
        // use DataReadRequest.
        update?.apply {
            val start = getUpdateStartTime(TimeUnit.MILLISECONDS)
            val end = getUpdateEndTime(TimeUnit.MILLISECONDS)
            println("Data Update start: $start end: $end DataType: ${dataType.name}")
        }
    }

    override fun onCreate() {
        super.onCreate()
        println("GoogleFitUpdateIntentService##onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("GoogleFitUpdateIntentService##onDestroy")
    }
}