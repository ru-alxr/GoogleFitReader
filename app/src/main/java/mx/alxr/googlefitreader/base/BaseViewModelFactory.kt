package mx.alxr.googlefitreader.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

abstract class BaseViewModelFactory<T : ViewModel?> : ViewModelProvider.Factory {

    override fun <ExpectedType : ViewModel?> create(expectedClass: Class<ExpectedType>): ExpectedType {
        val viewModel: T = create()
        return cast(viewModel, expectedClass)
    }

    protected abstract fun create(): T

    private fun <ExpectedType : ViewModel?> cast(
        viewModel: T,
        expectedClass: Class<ExpectedType>
    ): ExpectedType {
        return try {
            expectedClass.cast(viewModel) ?: throw RuntimeException("It will never be thrown")
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

}