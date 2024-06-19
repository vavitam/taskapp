package com.example.taskapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskapp.R
import com.example.taskapp.model.NoteModel

class NoteAdapter(private val ds:ArrayList<NoteModel>) : RecyclerView.Adapter<NoteAdapter.ViewHolder>(){
    private lateinit var nListener: NoteAdapter.onItemClickListener
    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(clickListener: NoteAdapter.onItemClickListener) {
        nListener = clickListener
    }

    class ViewHolder(itemView: View, clickListener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvTitleNote)
        val desc: TextView = itemView.findViewById(R.id.tvContentNote)

        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.note_item, parent, false)
        return NoteAdapter.ViewHolder(itemView, nListener)
    }

    override fun getItemCount(): Int {
        return ds.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.apply {
            holder.title.text = ds[position].noteTitle
            holder.desc.text = ds[position].noteDesc
        }
    }
}