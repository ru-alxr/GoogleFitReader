package mx.alxr.googlefitreader.view

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import mx.alxr.googlefitreader.databinding.ItemBloodPressureBinding
import mx.alxr.googlefitreader.models.BloodPressureItem

class BloodPressureViewHolder(private val binding: ItemBloodPressureBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun setRecord(record: BloodPressureItem) {
        binding.timeStampView.text = record.dateLabel
        binding.systolicView.apply {
            text = record.topLabel
            setTextColor(record.labelColor)
        }
        binding.diastolicView.apply {
            text = record.bottomLabel
            setTextColor(record.labelColor)
        }
        binding.warningView.visibility = if (record.isExclamationMarkVisible) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

}