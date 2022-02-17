package mx.alxr.googlefitreader.application

import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import mx.alxr.googlefitreader.di.components.DaggerGoogleFitReaderApplicationComponent

class GoogleFitReaderApplication : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }

    override fun applicationInjector(): AndroidInjector<GoogleFitReaderApplication?> {
        return DaggerGoogleFitReaderApplicationComponent.factory().create(this)
    }

}