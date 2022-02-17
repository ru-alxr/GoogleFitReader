package mx.alxr.googlefitreader.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mx.alxr.googlefitreader.databinding.ItemBloodPressureBinding
import mx.alxr.googlefitreader.models.BloodPressureItem

class BloodPressureAdapter : RecyclerView.Adapter<BloodPressureViewHolder>() {

    private val data = ArrayList<BloodPressureItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BloodPressureViewHolder {
        val binding = ItemBloodPressureBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return BloodPressureViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BloodPressureViewHolder, position: Int) {
        getRecord(position)?.apply {
            holder.setRecord(this)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun update(list: List<BloodPressureItem>?) {
        data.clear()
        list?.apply { data.addAll(this) }
        // TODO: use diff utils
        notifyDataSetChanged()
    }

    private fun getRecord(index: Int): BloodPressureItem? {
        return try {
            data[index]
        } catch (e: IndexOutOfBoundsException) {
            null
        }
    }

}