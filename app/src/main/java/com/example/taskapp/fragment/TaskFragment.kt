package com.example.taskapp.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskapp.CreateTaskActivity
import com.example.taskapp.DetailTaskActivity
import com.example.taskapp.ListTaskActivity
import com.example.taskapp.adapter.TaskAdapter
import com.example.taskapp.databinding.FragmentTaskBinding
import com.example.taskapp.model.TaskModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TaskFragment : Fragment() {
    lateinit var binding: FragmentTaskBinding
    lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    private lateinit var dsTaskCom:ArrayList<TaskModel>
    private lateinit var dsTaskUnCom:ArrayList<TaskModel>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTaskBinding.inflate(layoutInflater)

        binding.progressBar.visibility = View.VISIBLE

        binding.tvTaskComplete.visibility = View.GONE
        binding.tvTaskUnComplete.visibility = View.GONE

        binding.rcvTaskComplete.layoutManager = LinearLayoutManager(context)
        binding.rcvTaskComplete.setHasFixedSize(true)
        binding.rcvTaskUnComplete.layoutManager = LinearLayoutManager(context)
        binding.rcvTaskUnComplete.setHasFixedSize(true)
        dsTaskCom = arrayListOf<TaskModel>()
        dsTaskUnCom = arrayListOf<TaskModel>()
        getTaskUnCom()
        getTaskCom()

        if ((dsTaskUnCom.isEmpty()) && (dsTaskCom.isEmpty())) {
            binding.FolderEmpty.visibility = View.VISIBLE
        }


        binding.btnThemTask.setOnClickListener {
            val i = Intent(requireContext(), CreateTaskActivity::class.java)
            startActivity(i)
        }

        binding.btnCheckListTask.setOnClickListener {
            val i5 = Intent(requireContext(), ListTaskActivity::class.java)
            startActivity(i5)
        }

        return binding.root
    }

    private fun getTaskCom() {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val formattedDate = currentDate.format(formatter)

        val uId = FirebaseAuth.getInstance().currentUser!!.uid
        dbRef = FirebaseDatabase.getInstance().getReference("Task")
        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dsTaskCom.clear()
                if (snapshot.exists()) {
                    for (taskSnap in snapshot.children) {
                        val taskData = taskSnap.getValue(TaskModel::class.java)
                        taskData?.let {
                            if ((it.userId == uId) && (it.isChecked!!)  && (it.taskDateCom == formattedDate)) {
                                dsTaskCom.add(it)
                                binding.FolderEmpty.visibility = View.GONE
                            }
                        }
                    }

                    val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    dsTaskCom.sortByDescending {
                        LocalDate.parse(it.taskTimeDead, dateTimeFormatter)
                    }

                    val cAdapter = TaskAdapter(dsTaskCom)
                    binding.rcvTaskComplete.adapter = cAdapter

                    cAdapter.notifyDataSetChanged()

                    cAdapter.setOnItemClickListener(object : TaskAdapter.onItemClickListener {
                        override fun onItemClick(position: Int) {
                            val i1 = Intent(context, DetailTaskActivity::class.java)
                            i1.putExtra("taskId",dsTaskCom[position].taskId)
                            i1.putExtra("taskName",dsTaskCom[position].taskTitle)
                            i1.putExtra("taskTimeDead",dsTaskCom[position].taskTimeDead)
                            i1.putExtra("taskTimeHour",dsTaskCom[position].taskTimeHour)
                            i1.putExtra("taskDesc",dsTaskCom[position].taskDesc)
                            i1.putExtra("taskImage",dsTaskCom[position].taskImage)
                            i1.putExtra("taskDateCom",dsTaskCom[position].taskDateCom)
                            startActivity(i1)
                        }

                    })

                    if (dsTaskCom.size == 0) {
                        binding.tvTaskComplete.visibility = View.GONE
                    } else {
                        binding.tvTaskComplete.visibility = View.VISIBLE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun getTaskUnCom() {

        val uId = FirebaseAuth.getInstance().currentUser!!.uid
        dbRef = FirebaseDatabase.getInstance().getReference("Task")
        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dsTaskUnCom.clear()
                if (snapshot.exists()) {
                    for (taskSnap in snapshot.children) {
                        val taskData = taskSnap.getValue(TaskModel::class.java)
                        taskData?.let {
                            if ((it.userId == uId) && (!it.isChecked!!)) {
                                dsTaskUnCom.add(it)
                                binding.FolderEmpty.visibility = View.GONE
                            }
                        }
                    }

                    val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    dsTaskUnCom.sortByDescending {
                        LocalDate.parse(it.taskTimeDead, dateTimeFormatter)
                    }

                    val uAdapter = TaskAdapter(dsTaskUnCom)
                    binding.rcvTaskUnComplete.adapter = uAdapter

                    uAdapter.notifyDataSetChanged()

                    uAdapter.setOnItemClickListener(object : TaskAdapter.onItemClickListener {
                        override fun onItemClick(position: Int) {
                            val i1 = Intent(context, DetailTaskActivity::class.java)
                            i1.putExtra("taskId",dsTaskUnCom[position].taskId)
                            i1.putExtra("taskName",dsTaskUnCom[position].taskTitle)
                            i1.putExtra("taskTimeDead",dsTaskUnCom[position].taskTimeDead)
                            i1.putExtra("taskTimeHour",dsTaskUnCom[position].taskTimeHour)
                            i1.putExtra("taskDesc",dsTaskUnCom[position].taskDesc)
                            i1.putExtra("taskImage",dsTaskUnCom[position].taskImage)
                            i1.putExtra("taskDateCom", dsTaskUnCom[position].taskDateCom)
                            startActivity(i1)
                        }
                    })

                    if (dsTaskUnCom.size == 0) {
                        binding.tvTaskUnComplete.visibility = View.GONE
                    } else {
                        binding.tvTaskUnComplete.visibility = View.VISIBLE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Lỗi cơ sở dữ liệu: ${error.message}")
            }

        })

        binding.progressBar.visibility = View.GONE
    }
}