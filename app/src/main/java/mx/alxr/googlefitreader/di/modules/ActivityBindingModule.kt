package mx.alxr.googlefitreader.di.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import mx.alxr.googlefitreader.view.MainActivity

@Module
abstract class ActivityBindingModule {

    @ContributesAndroidInjector(modules = [MainActivityViewModelModule::class, MainActivityFeatures::class])
    @MainActivityScope
    abstract fun bindMyLocationActivity(): MainActivity

}