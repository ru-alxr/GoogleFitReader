package mx.alxr.googlefitreader.di.components


import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import mx.alxr.googlefitreader.application.GoogleFitReaderApplication
import mx.alxr.googlefitreader.di.modules.ActivityBindingModule
import mx.alxr.googlefitreader.di.modules.GoogleFitReaderApplicationModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        GoogleFitReaderApplicationModule::class,
        ActivityBindingModule::class
    ]
)
interface GoogleFitReaderApplicationComponent : AndroidInjector<GoogleFitReaderApplication> {

    @Component.Factory
    abstract class Factory : AndroidInjector.Factory<GoogleFitReaderApplication>

}