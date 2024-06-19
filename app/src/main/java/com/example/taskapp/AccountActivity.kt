package com.example.taskapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.taskapp.adapter.TaskAdapter
import com.example.taskapp.auth.EditAccountActivity
import com.example.taskapp.databinding.ActivityAccountBinding
import com.example.taskapp.model.TaskModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.firestore

class AccountActivity : AppCompatActivity() {
    lateinit var binding: ActivityAccountBinding
    private var db = Firebase.firestore
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        getValue()

        binding.tvEditAccount.setOnClickListener {
            val i = Intent(this, EditAccountActivity::class.java)
            startActivity(i)
        }

        binding.btnLeftTask.setOnClickListener {
            val i = Intent(this, HomeActivity::class.java)
            startActivity(i)
        }
    }

    private fun getValue() {

        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = db.collection("users").document(userID)
        ref.get().addOnSuccessListener {
            if (it != null) {
                val name = it.data?.get("name")?.toString()
                val image = it.data?.get("image")?.toString()

                binding.tvNameAccount.setText(name)
                Glide.with(this)
                    .load(image)
                    .into(binding.imageAvtAccount)
            }
        }

        getDsTaskCom()
        getDsTaskUnCom()
    }

    private fun getDsTaskUnCom() {
        var n: Int = 0

        val uId = FirebaseAuth.getInstance().currentUser!!.uid
        dbRef = FirebaseDatabase.getInstance().getReference("Task")
        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (taskSnap in snapshot.children) {
                        val taskData = taskSnap.getValue(TaskModel::class.java)
                        taskData?.let {
                            if ((it.userId == uId) && (!it.isChecked!!)) {
                                n+=1
                            }
                        }
                    }

                    binding.tvChuaHoanThanh.setText(n.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun getDsTaskCom() {
        var n: Int = 0

        val uId = FirebaseAuth.getInstance().currentUser!!.uid
        dbRef = FirebaseDatabase.getInstance().getReference("Task")
        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (taskSnap in snapshot.children) {
                        val taskData = taskSnap.getValue(TaskModel::class.java)
                        taskData?.let {
                            if ((it.userId == uId) && (it.isChecked!!)) {
                                n+=1
                            }
                        }
                    }

                    binding.tvDaHoanThanh.setText(n.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}