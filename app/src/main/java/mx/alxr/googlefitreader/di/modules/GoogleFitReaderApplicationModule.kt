package mx.alxr.googlefitreader.di.modules

import android.content.Context
import dagger.Binds
import dagger.Module
import mx.alxr.googlefitreader.application.GoogleFitReaderApplication
import javax.inject.Singleton

@Module
abstract class GoogleFitReaderApplicationModule {

    @Binds
    @Singleton
    abstract fun providesApplicationContext(application: GoogleFitReaderApplication): Context

}