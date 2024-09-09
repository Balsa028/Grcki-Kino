package com.example.grckikino.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.grckikino.R
import com.example.grckikino.models.ResultAdapterItem
import com.example.grckikino.utils.HEADER_TYPE

class ResultsAdapter : RecyclerView.Adapter<ViewHolder>() {

    private var resultsList: List<ResultAdapterItem> = emptyList()

    override fun getItemViewType(position: Int): Int {
        return resultsList[position].type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == HEADER_TYPE) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.draw_header_item, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.result_draw_item, parent, false)
            DrawViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.bind(resultsList[position])
            is DrawViewHolder -> holder.bind(resultsList[position])
        }
    }

    override fun getItemCount(): Int = resultsList.size

    fun setResultItems(newList: List<ResultAdapterItem>) {
        resultsList = newList
        notifyDataSetChanged()
    }

    class HeaderViewHolder(itemView: View) : ViewHolder(itemView) {
        private val txtDrawTime: TextView = itemView.findViewById(R.id.drawing_time_result_textview)
        private val txtDrawId: TextView = itemView.findViewById(R.id.drawing_id_results_textview)

        fun bind(item: ResultAdapterItem) {
            txtDrawTime.text = item.drawTime
            txtDrawId.text = item.drawId?.toString() ?: ""
        }
    }

    class DrawViewHolder(itemView: View) : ViewHolder(itemView) {
        private val recycleView: RecyclerView = itemView.findViewById(R.id.result_number_rec_view)

        fun bind(item: ResultAdapterItem) {
            recycleView.setHasFixedSize(true)
            recycleView.layoutManager = GridLayoutManager(itemView.context, 7)
            recycleView.isNestedScrollingEnabled = false
            val adapter = ResultNumbersAdapter()
            adapter.setNumbers(item.winningNumbers?.sorted() ?: emptyList())
            recycleView.adapter = adapter
        }
    }
}