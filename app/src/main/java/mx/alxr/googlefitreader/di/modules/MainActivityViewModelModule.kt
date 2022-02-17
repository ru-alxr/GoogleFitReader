package mx.alxr.googlefitreader.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import mx.alxr.googlefitreader.viewmodel.MainActivityViewModel
import mx.alxr.googlefitreader.viewmodel.MainActivityViewModelFactory

@Module
abstract class MainActivityViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: MainActivityViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    abstract fun bindMainActivityViewModel(viewModel: MainActivityViewModel): ViewModel

}