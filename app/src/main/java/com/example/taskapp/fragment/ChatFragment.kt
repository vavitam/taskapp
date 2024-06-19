package com.example.taskapp.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskapp.R
import com.example.taskapp.adapter.UserChatAdapter
import com.example.taskapp.databinding.FragmentChatBinding
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

class ChatFragment : Fragment() {
    lateinit var binding: FragmentChatBinding
//    private lateinit var userList: ArrayList<UserModel>
    private lateinit var userList: ArrayList<UserChatModel>
    private lateinit var adapter: UserChatAdapter
    private lateinit var mAuth: FirebaseAuth
    private var db = Firebase.firestore
    private lateinit var mDbRef: DatabaseReference
    val userID = FirebaseAuth.getInstance().currentUser!!.uid



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment

        mDbRef = FirebaseDatabase.getInstance().getReference()
        mAuth = FirebaseAuth.getInstance()


        userList = ArrayList()
        adapter = UserChatAdapter(requireContext(), userList)

        binding.rcvUserChat.layoutManager = LinearLayoutManager(context)
        binding.rcvUserChat.adapter = adapter

//        val ref = db.collection("users")
//        ref.addSnapshotListener { value, error ->
//            if (error != null) {
//                Log.w(TAG, "Listen failed.", error)
//                return@addSnapshotListener
//            }
//
//            if (value != null) {
//                userList.clear()
//                for (document in value.documents) {
//                    if (document.id != userID) {
//                        val currentUser = document.toObject(UserModel::class.java)
//                        if (currentUser != null) {
//                            currentUser.uid = document.id
//                            userList.add(currentUser)
//                        }
//                    }
//
//                }
//                adapter.notifyDataSetChanged()
//            }
//        }

        mDbRef.child("UserChats").child(userID)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userList.clear()

                    for (postSnapshot in snapshot.children) {
                        val userChat = postSnapshot.getValue(UserChatModel::class.java)
                        userList.add(userChat!!)
                    }
                    val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                    userList.sortByDescending {
                        LocalDateTime.parse(it.time, dateTimeFormatter)
                    }

                    adapter.notifyDataSetChanged()

                    if (userList.size == 0) {
                        binding.FolderEmpty.visibility = View.VISIBLE
                    } else {
                        binding.FolderEmpty.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        binding.btnThemUser.setOnClickListener {
            showDialog()
        }

        return binding.root
    }

    @SuppressLint("MissingInflatedId")
    private fun showDialog() {
        val mDialog = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val mDialogView = inflater.inflate(R.layout.add_user_dialog, null)
        mDialog.setView(mDialogView)

        val edtEmail = mDialogView.findViewById<EditText>(R.id.edtMailDialog)
        val btnAdd = mDialogView.findViewById<TextView>(R.id.btnAddDialog)

        val alertDialog = mDialog.create()
        alertDialog.show()

        btnAdd.setOnClickListener {
            var check = 1

            // Kiểm tra có trùng tài khoản đã thêm
            mDbRef.child("UserChats").child(userID)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (postSnapshot in snapshot.children) {
                            val userChat = postSnapshot.getValue(UserChatModel::class.java)
                            Log.e("Email", userChat?.email.toString() + " " + edtEmail.text.toString() + " "+ check)
                            if (userChat?.email.toString() == edtEmail.text.toString()) {
                                check = 2
                            }
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

            val ref = db.collection("users")
            ref.addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                // Thời gian
                val currentDateTime = LocalDateTime.now()
                val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                val formattedDate = currentDateTime.format(dateFormatter)

                if (value != null) {
                    for (document in value.documents) {
                        val currentUser = document.toObject(UserChatModel::class.java)
                        Log.e("Check_Truoc", check.toString())
                        if ((currentUser?.email.toString() == edtEmail.text.toString()) && (document.id != userID) && (check == 1)) {
                            if (currentUser != null) {
                                currentUser.uid = document.id
                                currentUser.time = formattedDate
                                currentUser.chat = "Gửi lời chào đến với đối phương"
                                mDbRef.child("UserChats").child(userID).push()
                                    .setValue(currentUser)

                                val ref2 = db.collection("users").document(userID)
                                ref2.get()
                                    .addOnSuccessListener { documentSnapshot ->
                                        val currentUser2 = documentSnapshot.toObject(UserChatModel::class.java)
                                        currentUser2?.uid = userID
                                        currentUser2?.time = formattedDate
                                        currentUser.chat = "Gửi lời chào đến với đối phương"
                                        mDbRef.child("UserChats").child(document.id).push()
                                            .setValue(currentUser2)
                                    }

                                check = 0
                                Toast.makeText(requireContext(), "Thêm thành công!!", Toast.LENGTH_SHORT).show()
                            }
                        }

                    }
                    if (check == 1) {
                        Toast.makeText(requireContext(), "Tài khoản không tồn tại!!", Toast.LENGTH_SHORT).show()
                    }
                    if (check == 2) {
                        Toast.makeText(requireContext(), "Tài khoản đã thêm trước!!", Toast.LENGTH_SHORT).show()
                    }
                }

            }
            Log.e("Check_Sau", check.toString())
            check = 1
            alertDialog.dismiss()
        }
    }


}