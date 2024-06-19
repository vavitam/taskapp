package com.example.taskapp.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskapp.R
import com.example.taskapp.model.MessageModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MessageAdapter(val context: Context, val messageList: ArrayList<MessageModel>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECEIVE = 1;
    val ITEM_SENT = 2;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            // Receive
            val view: View = LayoutInflater.from(context).inflate(R.layout.receive, parent, false)
            return ReceiveViewHolder(view)

        } else {
            // Sent
            val view: View = LayoutInflater.from(context).inflate(R.layout.sent, parent, false)
            return SentViewHolder(view)
        }

    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var day = ""

        if (position != 0) {
            val currentMessaged = messageList[position-1]
            // Thời gian tin nhắn trước
            val dateTimeString = currentMessaged.time
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
            val dateTime = LocalDateTime.parse(dateTimeString, formatter)
            day = String.format("%02d", dateTime.dayOfMonth)+"/"+String.format("%02d", dateTime.monthValue)+"/"+String.format("%04d", dateTime.year)
        }

        val currentMessage = messageList[position]
        // Thời gian tức thì
        val dateTimeString1 = currentMessage.time
        val formatter1 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        val dateTime1 = LocalDateTime.parse(dateTimeString1, formatter1)
        val time1 = String.format("%02d", dateTime1.hour) + ":" + String.format("%02d", dateTime1.minute)
        val day1 = String.format("%02d", dateTime1.dayOfMonth) +"/"+String.format("%02d", dateTime1.monthValue)+"/"+String.format("%04d", dateTime1.year)

        if (holder.javaClass == SentViewHolder::class.java) {
            // do the stuff for sent view holder
            val viewHolder = holder as SentViewHolder
            holder.sentMessage.text = currentMessage.message
            holder.timeSent.setText(time1)
            holder.dateSent.visibility = View.VISIBLE

            if (day == day1) {
                holder.dateSent.visibility = View.GONE
            } else {
                holder.dateSent.setText(day1)
            }

        } else {
            // do dtuff for receive view holder
            val viewHolder = holder as ReceiveViewHolder
            holder.receiveMessage.text = currentMessage.message
            holder.timeReceive.setText(time1)
            holder.dateReceive.visibility = View.VISIBLE

            if (day == day1) {
                holder.dateReceive.visibility = View.GONE
            } else {
                holder.dateReceive.setText(day1)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]

        if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)) {
            return ITEM_SENT
        } else {
            return ITEM_RECEIVE
        }
    }

    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sentMessage = itemView.findViewById<TextView>(R.id.txt_sent_message)
        val timeSent = itemView.findViewById<TextView>(R.id.txt_sent_time_message)
        val dateSent = itemView.findViewById<TextView>(R.id.txt_date_sent_message)
    }

    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receiveMessage = itemView.findViewById<TextView>(R.id.txt_receive_message)
        val timeReceive = itemView.findViewById<TextView>(R.id.txt_receive_time_message)
        val dateReceive = itemView.findViewById<TextView>(R.id.txt_date_receive_message)
    }
}