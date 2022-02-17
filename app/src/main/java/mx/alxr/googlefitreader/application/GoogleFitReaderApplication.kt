package mx.alxr.googlefitreader.application

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class GoogleFitReaderApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }

}