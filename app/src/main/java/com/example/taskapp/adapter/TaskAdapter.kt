package com.example.taskapp.adapter

import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.taskapp.R
import com.example.taskapp.model.TaskModel
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TaskAdapter(private val ds:ArrayList<TaskModel>) :RecyclerView.Adapter<TaskAdapter.ViewHolder>() {
    private lateinit var nListener: onItemClickListener
    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(clickListener: onItemClickListener) {
        nListener = clickListener
    }

    // Taoj class viewHolder
    class ViewHolder(itemView: View, clickListener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val taskName: TextView = itemView.findViewById(R.id.tvNameItemTask)
        val timeDeadline: TextView = itemView.findViewById(R.id.tvTimeDeadline)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkTask)
        val checkStar: TextView = itemView.findViewById(R.id.iconStar)

        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_item, parent, false)
        return ViewHolder(itemView, nListener)
    }

    override fun getItemCount(): Int {
        return ds.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var n = ds[position].taskTimeDead?.length ?: 0
        n-=5
        holder.itemView.apply {
            holder.taskName.text = ds[position].taskTitle
            holder.timeDeadline.text = ds[position].taskTimeDead?.take(n) ?: "null"

            val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val taskTimeDeadDate = LocalDate.parse(ds[position].taskTimeDead, dateTimeFormatter)

            val currentDate = LocalDate.now()

            if (taskTimeDeadDate.isAfter(currentDate)) {
                holder.timeDeadline.setTextColor(context.getColor(R.color.pigmentGreen))
            } else if (taskTimeDeadDate.isBefore(currentDate)) {
                holder.timeDeadline.setTextColor(Color.RED)
            } else {
                holder.timeDeadline.setTextColor(context.getColor(R.color.darkYellow))
            }
        }

        //Tránh lặp lại listener khi RecycleView tía sử dụng viewHolder
        holder.checkBox.setOnCheckedChangeListener(null)

        // Thiết lập trạng thái ban đầu cho checkBox
        holder.checkBox.isChecked = ds[position].isChecked!!
        if (holder.checkBox.isChecked) {
            holder.taskName.paintFlags = holder.taskName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }

        // Thiết lập listener cho checkBox
        holder.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            ds[position].isChecked = isChecked
            val dbRef = FirebaseDatabase.getInstance().getReference("Task").child(ds[position].taskId.toString())
            dbRef.child("checked").setValue(isChecked)
            if (isChecked) {
                val currentDate = LocalDate.now()
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val formattedDate = currentDate.format(formatter)
                dbRef.child("taskDateCom").setValue(formattedDate)
            } else {
                dbRef.child("taskDateCom").setValue("")
            }
        }


        // Thiết lập trạng thái ban đầu cho star
        if (ds[position].taskImport == true) {
            holder.checkStar.setBackgroundResource(R.drawable.icon_star_checked)
        } else {
            holder.checkStar.setBackgroundResource(R.drawable.icon_star_uncheck)
        }

        // Thiết lập listener cho star
        holder.checkStar.setOnClickListener {
            ds[position].taskImport = !ds[position].taskImport!!
            if (ds[position].taskImport == true) {
                holder.checkStar.setBackgroundResource(R.drawable.icon_star_checked)
            }
            val dbRef = FirebaseDatabase.getInstance().getReference("Task").child(ds[position].taskId.toString())
            dbRef.child("taskImport").setValue(ds[position].taskImport)
        }
    }
}