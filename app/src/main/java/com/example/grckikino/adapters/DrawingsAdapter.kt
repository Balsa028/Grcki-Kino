package com.example.grckikino.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.grckikino.R
import com.example.grckikino.models.Drawing
import com.example.grckikino.utils.REMAINING_TIME_WARNING
import com.example.grckikino.utils.formatRemainingTimeForDisplay
import com.example.grckikino.utils.formatDrawingTimeForDisplay

class DrawingsAdapter : RecyclerView.Adapter<DrawingsAdapter.DrawingViewHolder>() {

    private var drawingList: List<Drawing> = emptyList()
    private var adapterListener: DrawingsAdapterListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrawingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.drawing_list_item, parent, false)
        return DrawingViewHolder(view, adapterListener)
    }

    override fun getItemCount(): Int = drawingList.size

    override fun onBindViewHolder(holder: DrawingViewHolder, position: Int) {
        holder.bind(drawingList[position])
    }

    fun setDrawingList(list: List<Drawing>) {
        drawingList = list
        notifyDataSetChanged()
    }

    fun getDrawingList() = drawingList

    fun setAdapterListener(listener: DrawingsAdapterListener) {
        this.adapterListener = listener
    }

    class DrawingViewHolder(itemView: View, private val adapterListener: DrawingsAdapterListener?) :
        RecyclerView.ViewHolder(itemView) {

        val parent: ConstraintLayout
        private val drawingTimeTextView: TextView
        private val remainingTimeTextView: TextView

        init {
            parent = itemView.findViewById(R.id.parent)
            drawingTimeTextView = itemView.findViewById(R.id.drawing_time_text)
            remainingTimeTextView = itemView.findViewById(R.id.remaining_time_text)
        }

        fun bind(drawing: Drawing) {
            drawingTimeTextView.text = drawing.drawTime.formatDrawingTimeForDisplay()
            remainingTimeTextView.text = drawing.drawTime.formatRemainingTimeForDisplay()
            val remainingTimeText = remainingTimeTextView.text.toString()
            remainingTimeTextView.setTextColor(
                ContextCompat.getColor(
                    itemView.context,
                    if (remainingTimeText <= REMAINING_TIME_WARNING) R.color.red else R.color.white
                )
            )
            parent.setOnClickListener {
                adapterListener?.onToDrawingDetailsScreen(drawing.gameId, drawing.drawId)
            }
        }
    }
}