package com.example.taskapp

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.taskapp.adapter.MessageAdapter
import com.example.taskapp.databinding.ActivityChatBinding
import com.example.taskapp.model.MessageModel
import com.example.taskapp.model.UserChatModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.firestore
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatBinding
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<MessageModel>
    private lateinit var mDbRef: DatabaseReference
    private var db = Firebase.firestore

    var receiverRoom: String? = null
    var senderRoom: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnLeftChat.setOnClickListener {
            val i1 = Intent(this, HomeActivity::class.java)
            i1.putExtra("fragment_to_display", "ChatFragment")
            startActivity(i1)
        }

        val name = intent.getStringExtra("name")
        val image = intent.getStringExtra("image")
        val receiveUid = intent.getStringExtra("uid")

        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        mDbRef = FirebaseDatabase.getInstance().getReference()

        senderRoom = receiveUid + senderUid
        receiverRoom = senderUid + receiveUid

        binding.tvNameUser.setText(name.toString())
        Glide.with(this)
            .load(image)
            .into(binding.imageAvtAccountChat)

        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)

        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.adapter = messageAdapter

        //login for adding data to recyclerview
        mDbRef.child("Chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()

                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(MessageModel::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()

                    binding.chatRecyclerView.scrollToPosition(messageList.size - 1)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        // adding the message to database
        binding.sendMassage.setOnClickListener {
            // Thời gian
            val currentDateTime = LocalDateTime.now()
            val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
            val formattedDateTime = currentDateTime.format(dateTimeFormatter)

            val message = binding.messageBox.text.toString().trim()

            val messageObject = MessageModel(senderUid, message, formattedDateTime)



            mDbRef.child("Chats").child(senderRoom!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    mDbRef.child("Chats").child(receiverRoom!!).child("messages").push()
                        .setValue(messageObject)
                }

            binding.messageBox.setText("")

            thayThongTinChat(formattedDateTime, message)

        }

        // Option
        binding.btnMoreChat.setOnClickListener {
            showPopupMenu(it)
        }
    }

    private fun thayThongTinChat(dateTime: String?, message: String?) {
        val receiveUid = intent.getStringExtra("uid")

        val senderUid = FirebaseAuth.getInstance().currentUser!!.uid

        mDbRef.child("UserChats").child(senderUid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val userChat = postSnapshot.getValue(UserChatModel::class.java)
                        if (userChat?.uid == receiveUid) {
                            postSnapshot.ref.child("time").setValue(dateTime)
                            postSnapshot.ref.child("chat").setValue(message)
                            postSnapshot.ref.child("sent").setValue("Bạn")
                            break
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Xử lý lỗi nếu cần
                }
            })

//        mDbRef.child("UserChats").child(senderUid)
//            .addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    for (postSnapshot in snapshot.children) {
//                        val userChat = postSnapshot.getValue(UserChatModel::class.java)
//                        if (userChat?.uid == intent.getStringExtra("uid")) {
//
//                        }
//                    }
//                }
//                override fun onCancelled(error: DatabaseError) {
//                    TODO("Not yet implemented")
//                }
//
//            })


        mDbRef.child("UserChats").child(receiveUid!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val userChat = postSnapshot.getValue(UserChatModel::class.java)
                        if (userChat?.uid == senderUid) {
                            postSnapshot.ref.child("time").setValue(dateTime)
                            postSnapshot.ref.child("chat").setValue(message)
                            postSnapshot.ref.child("sent").setValue(userChat.name)
                            break
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Xử lý lỗi nếu cần
                }
            })

    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(this, view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.menu_chat_option, popup.menu)
        popup.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_set_name -> {
                    setNameUser()
                    true
                }
                R.id.menu_delete -> {
                    deleteChat()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun deleteChat() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete")
        builder.setMessage("Bạn có muốn xóa đoạn chat?")
        builder.setPositiveButton("Có") { dialog, _ ->
            delete()
            dialog.dismiss()
        }
        builder.setNegativeButton("Không") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun delete() {
        val userID = FirebaseAuth.getInstance().currentUser!!.uid

        // Xóa từ chính chủ
        mDbRef.child("UserChats").child(userID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val userChat = postSnapshot.getValue(UserChatModel::class.java)
                        if (userChat?.uid == intent.getStringExtra("uid")) {
                            postSnapshot.ref.removeValue()

                            val ii = Intent(this@ChatActivity, HomeActivity::class.java)
                            ii.putExtra("fragment_to_display", "ChatFragment")
                            startActivity(ii)

                            break
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    // Xử lý lỗi nếu cần
                }
            })

        // Xóa từ người chat
        mDbRef.child("UserChats").child(intent.getStringExtra("uid").toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val userChat = postSnapshot.getValue(UserChatModel::class.java)
                        if (userChat?.uid == userID) {
                            postSnapshot.ref.removeValue()
                            break
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    // Xử lý lỗi nếu cần
                }
            })
    }

    private fun setNameUser() {
        showNameDialog()
    }

    @SuppressLint("MissingInflatedId")
    private fun showNameDialog() {
        val mDialog = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val mDialogView = inflater.inflate(R.layout.set_user_dialog, null)
        mDialog.setView(mDialogView)

        val edtName = mDialogView.findViewById<EditText>(R.id.edtNameDialog)
        val btnUpdate = mDialogView.findViewById<TextView>(R.id.btnSetNameDialog)

        val alertDialog = mDialog.create()
        alertDialog.show()

        btnUpdate.setOnClickListener {
            val userID = FirebaseAuth.getInstance().currentUser!!.uid
            val newName = edtName.text.toString().trim()

            if (newName.isNotEmpty()) {
                mDbRef.child("UserChats").child(userID)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (postSnapshot in snapshot.children) {
                                val userChat = postSnapshot.getValue(UserChatModel::class.java)
                                if (userChat?.uid == intent.getStringExtra("uid")) {
                                    // Cập nhật tên tại vị trí chính xác trong cơ sở dữ liệu
                                    postSnapshot.ref.child("name").setValue(newName)

                                    val sentValue = postSnapshot.child("sent").getValue(String::class.java)
                                    if (sentValue != "Bạn") {
                                        postSnapshot.ref.child("sent").setValue(newName)
                                    }
                                    binding.tvNameUser.text = newName
                                    break
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Xử lý lỗi nếu cần
                        }
                    })
            } else {
                Toast.makeText(this, "Tên trống", Toast.LENGTH_SHORT).show()
            }
            alertDialog.dismiss()
        }

    }

}