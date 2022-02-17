package mx.alxr.googlefitreader.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mx.alxr.googlefitreader.di.modules.MainActivityScope
import javax.inject.Inject
import javax.inject.Provider

@MainActivityScope
class MainActivityViewModelFactory @Inject constructor(private val providers: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val creator: Provider<out ViewModel?>? = providers[modelClass]
        return try {
            modelClass.cast(creator?.get()) ?: throw NullPointerException()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

}