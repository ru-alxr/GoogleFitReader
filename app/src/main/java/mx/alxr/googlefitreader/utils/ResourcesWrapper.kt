package mx.alxr.googlefitreader.utils

import android.content.Context
import androidx.core.content.ContextCompat
import javax.inject.Inject

class ResourcesWrapper @Inject constructor(private val context: Context) : IResourcesWrapper {

    override fun getString(id: Int): String {
        return context.getString(id)
    }

    override fun getString(id: Int, argument: String): String {
        return context.getString(id, argument)
    }

    override fun getColor(id: Int): Int {
        return ContextCompat.getColor(context, id)
    }

}