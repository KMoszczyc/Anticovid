package com.example.anticovid.ui.profile

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.anticovid.R
import com.example.anticovid.data.model.HealthDiaryEntry

class HealthDiaryEntriesAdapter : RecyclerView.Adapter<HealthDiaryEntriesAdapter.HeaderViewHolder>() {

    private var entries: List<HealthDiaryEntry> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_healt_diary_entry, parent, false)

        return HeaderViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: HeaderViewHolder, position: Int) {
        Log.d("HealthDiaryEntry", entries[position].header)
        viewHolder.bind(entries[position].header)
    }

    override fun getItemCount() = entries.size

    fun updateEntriesList(newEntries: List<HealthDiaryEntry>) {
        entries = newEntries
        notifyDataSetChanged()
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val entryHeader: TextView = itemView.findViewById(R.id.entry_header)

        fun bind(header: String) {
            entryHeader.text = header
            Log.d("HealthDiaryEntry", header)
        }
    }
}