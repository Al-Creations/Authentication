package com.example.authentication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PlanningAdapter(
    private val items: MutableList<String>,
    private val onDeleteClick: (String) -> Unit
) : RecyclerView.Adapter<PlanningAdapter.PlanningViewHolder>() {

    inner class PlanningViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val planningText: TextView = itemView.findViewById(R.id.planning)
        val timeText: TextView = itemView.findViewById(R.id.txTime)  // Menampilkan waktu
        val deleteIcon: ImageView = itemView.findViewById(R.id.icDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanningViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return PlanningViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlanningViewHolder, position: Int) {
        val item = items[position]
        val parts = item.split(" - ")  // Menggunakan split untuk memisahkan perencanaan dan waktu
        holder.planningText.text = parts[0]
        holder.timeText.text = parts.getOrNull(1) ?: ""  // Menampilkan waktu jika ada
        holder.deleteIcon.setOnClickListener {
            onDeleteClick(item)
        }
    }

    override fun getItemCount(): Int = items.size
}

