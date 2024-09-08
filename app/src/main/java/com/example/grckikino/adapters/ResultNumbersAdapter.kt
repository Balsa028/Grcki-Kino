package com.example.grckikino.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.grckikino.R

class ResultNumbersAdapter(private val numbers: List<Int>) : RecyclerView.Adapter<ResultNumbersAdapter.ResultNumberViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultNumberViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.result_number_item, parent, false)
        return ResultNumberViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResultNumberViewHolder, position: Int) {
        holder.bind(numbers[position])
    }

    override fun getItemCount(): Int = numbers.size

    inner class ResultNumberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.result_number_text_view)

        fun bind(number: Int) {
            textView.text = number.toString()
            textView.setBackgroundResource(handleTextViewBackground(number))
        }

        private fun handleTextViewBackground(number: Int) =
            when (number) {
                in 1..10 -> R.drawable.circle_yellow
                in 11..20 -> R.drawable.circle_red
                in 21..30 -> R.drawable.circle_purple
                in 31..40 -> R.drawable.circle_pink
                in 41..50 -> R.drawable.circle_light_gray
                in 51..60 -> R.drawable.circle_light_green
                in 61..70 -> R.drawable.circle_orange
                in 71..80 -> R.drawable.circle_blue
                else -> R.drawable.default_circle_background
            }
    }
}