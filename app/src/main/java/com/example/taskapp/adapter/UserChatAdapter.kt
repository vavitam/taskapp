package com.example.taskapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.taskapp.ChatActivity
import com.example.taskapp.R
import com.example.taskapp.model.UserChatModel
import com.google.android.material.imageview.ShapeableImageView
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class UserChatAdapter(val context: Context, val userList: ArrayList<UserChatModel> ):
    RecyclerView.Adapter<UserChatAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]

        // Thời gian hiện tại
        val currentDateTime = LocalDateTime.now()
        val dateTimeFormatternow = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        val formattedCurrentDateTime = currentDateTime.format(dateTimeFormatternow)

        val DateTimeString = currentUser.time
        val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        val DateTime = LocalDateTime.parse(DateTimeString, dateTimeFormatter)
//        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM")
//        val formattedDate = DateTime.format(dateFormatter)

        val result: String = if (DateTime.toLocalDate() == currentDateTime.toLocalDate()) {
            //Cùng ngày tháng năm, lấy giờ phút
            DateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        } else if (DateTime.year == currentDateTime.year) {
            //Cùng năm, khác ngày tháng, lấy ngày tháng
            DateTime.format(DateTimeFormatter.ofPattern("dd/MM"))
        } else {
            //Khác ngày tháng năm, lấy ngày tháng năm
            DateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        }

        holder.textName.text = currentUser.name
        Glide.with(holder.itemView.context)
            .load(currentUser.image)
            .into(holder.imgAvt)
        holder.textTime.text = result
        if (currentUser.sent == null) {
            holder.textChat.text = currentUser.chat
        } else {
            holder.textChat.text = currentUser.sent +": "+ currentUser.chat
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("name", currentUser.name)
            intent.putExtra("uid", currentUser.uid)
            intent.putExtra("image", currentUser.image)

            context.startActivity(intent)
        }
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textName = itemView.findViewById<TextView>(R.id.txt_name)
        val imgAvt = itemView.findViewById<ShapeableImageView>(R.id.imageAvtAccountChat)
        val textTime = itemView.findViewById<TextView>(R.id.txt_time_chat)
        val textChat = itemView.findViewById<TextView>(R.id.txt_content_chat)
    }
}