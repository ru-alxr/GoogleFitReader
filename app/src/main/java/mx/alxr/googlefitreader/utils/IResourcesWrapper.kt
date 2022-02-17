package mx.alxr.googlefitreader.utils

import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.StringRes

interface IResourcesWrapper {

    fun getString(@StringRes id: Int): String

    fun getString(@StringRes id: Int, argument: String): String

    @ColorInt
    fun getColor(@ColorRes id: Int): Int

}