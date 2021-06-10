package com.example.anticovid.ui.profile.health_diary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.anticovid.R
import com.example.anticovid.data.model.HealthDiaryEntry

class HealthDiaryEntryRecyclerAdapter(private val onClickListener: OnHealthDiaryEntryClickListener) : RecyclerView.Adapter<HealthDiaryEntryRecyclerAdapter.HeaderViewHolder>() {

    private var entries: List<HealthDiaryEntry> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_healt_diary_entry, parent, false)

        return HeaderViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: HeaderViewHolder, position: Int) {
        viewHolder.bind(entries[position])
    }

    override fun getItemCount() = entries.size

    fun updateEntriesList(entriesList: List<HealthDiaryEntry>) {
        entries = entriesList
        notifyDataSetChanged()
    }

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val header: TextView = itemView.findViewById(R.id.entry_header)

        fun bind(entry: HealthDiaryEntry) {
            header.text = entry.header
            itemView.setOnClickListener {
                onClickListener.onHealthDiaryEntryClick(entry)
            }
        }
    }

    interface OnHealthDiaryEntryClickListener {
        fun onHealthDiaryEntryClick(entry: HealthDiaryEntry)
    }
}