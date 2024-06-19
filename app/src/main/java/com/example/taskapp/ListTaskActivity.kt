package com.example.taskapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskapp.adapter.TaskAdapter
import com.example.taskapp.databinding.ActivityListTaskBinding
import com.example.taskapp.model.TaskModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ListTaskActivity : AppCompatActivity() {
    lateinit var binding: ActivityListTaskBinding
    private lateinit var dbRef: DatabaseReference
    private lateinit var dsListTaskCom:ArrayList<TaskModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityListTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.rcvListTaskCom.layoutManager = LinearLayoutManager(this)
        binding.rcvListTaskCom.setHasFixedSize(true)
        dsListTaskCom = arrayListOf<TaskModel>()
        getListTaskCom()

        if ((dsListTaskCom.isEmpty())) {
            binding.FolderEmpty.visibility = View.VISIBLE
        }

        binding.btnLeftTask.setOnClickListener {
            val i = Intent(this, HomeActivity::class.java)
            startActivity(i)
        }
    }

    private fun getListTaskCom() {
        val uId = FirebaseAuth.getInstance().currentUser!!.uid
        dbRef = FirebaseDatabase.getInstance().getReference("Task")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dsListTaskCom.clear()
                if (snapshot.exists()) {
                    for (taskSnap in snapshot.children) {
                        val taskData = taskSnap.getValue(TaskModel::class.java)
                        taskData?.let {
                            if ((it.userId == uId) && (it.isChecked!!)) {
                                dsListTaskCom.add(it)
                                binding.FolderEmpty.visibility = View.GONE
                            }
                        }
                    }

                    val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    dsListTaskCom.sortByDescending {
                        LocalDate.parse(it.taskTimeDead, dateTimeFormatter)
                    }

                    val cAdapter = TaskAdapter(dsListTaskCom)
                    binding.rcvListTaskCom.adapter = cAdapter

                    cAdapter.notifyDataSetChanged()

                    cAdapter.setOnItemClickListener(object : TaskAdapter.onItemClickListener {
                        override fun onItemClick(position: Int) {
                            val i1 = Intent(this@ListTaskActivity, DetailTaskActivity::class.java)
                            i1.putExtra("taskId",dsListTaskCom[position].taskId)
                            i1.putExtra("taskName",dsListTaskCom[position].taskTitle)
                            i1.putExtra("taskTimeDead",dsListTaskCom[position].taskTimeDead)
                            i1.putExtra("taskTimeHour",dsListTaskCom[position].taskTimeHour)
                            i1.putExtra("taskDesc",dsListTaskCom[position].taskDesc)
                            i1.putExtra("taskImage",dsListTaskCom[position].taskImage)
                            i1.putExtra("taskDateCom", dsListTaskCom[position].taskDateCom)
                            startActivity(i1)
                        }

                    })
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}