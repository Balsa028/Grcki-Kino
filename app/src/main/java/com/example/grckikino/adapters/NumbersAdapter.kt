package com.example.grckikino.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.grckikino.R
import com.example.grckikino.models.GridNumber
import com.example.grckikino.utils.MAX_SELECTED_NUMBERS

class NumbersAdapter(
    private val onClick: (GridNumber) -> Unit
) : RecyclerView.Adapter<NumbersAdapter.NumberViewHolder>() {

    private var numbersList: List<GridNumber> = emptyList()
    private var selectedNumbersCounter = 0
    private var isMaximumSelected = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumberViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.talon_grid_item, parent, false)
        return NumberViewHolder(view)
    }

    override fun onBindViewHolder(holder: NumberViewHolder, position: Int) {
        holder.bind(numbersList[position])
    }

    fun increaseNumbersCounter(value: Int) {
        if (selectedNumbersCounter == MAX_SELECTED_NUMBERS) {
            isMaximumSelected = true
            return
        }
        selectedNumbersCounter += value
    }

    fun decreaseNumbersCounter(value: Int) {
        if (selectedNumbersCounter == 0) return
        isMaximumSelected = false
        selectedNumbersCounter -= value
    }

    inner class NumberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val numberTextView: TextView = itemView.findViewById(R.id.number_textview)

        fun bind(number: GridNumber) {
            numberTextView.text = number.value.toString()
            numberTextView.setBackgroundResource(handleNumberBackground(number))
            itemView.setOnClickListener {
                this@NumbersAdapter.notifyItemChanged(adapterPosition)
                onClick(number)
            }
        }

        private fun handleNumberBackground(number: GridNumber): Int {
            return if (number.isSelected && selectedNumbersCounter <= MAX_SELECTED_NUMBERS && !isMaximumSelected) {
                return when (number.value) {
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
            } else {
                R.drawable.default_circle_background
            }
        }
    }

    fun setNumbersList(newList: List<GridNumber>) {
        this.numbersList = newList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = numbersList.size
}