package com.example.taskapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.taskapp.databinding.ActivityDetailTaskBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class DetailTaskActivity : AppCompatActivity() {
    lateinit var binding: ActivityDetailTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setValueToView()

        binding.btnLeftTask.setOnClickListener {
            val check = intent.getStringExtra("fragment_import")

            val i1 = Intent(this, HomeActivity::class.java)
            i1.putExtra("fragment_to_display", check)
            startActivity(i1)
        }

        binding.btnMoreTask.setOnClickListener {
            showPopupView(it)
        }
    }

    private fun showPopupView(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.menu_task_options, popup.menu)

        popup.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.menu_edit -> {
                    updateTask()
                    true
                }
                R.id.menu_delete -> {
                    deleteTask()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun updateTask() {
        val check = intent.getStringExtra("fragment_import")

        val i2 = Intent(this, UpdateTaskActivity::class.java)
        i2.putExtra("taskId",intent.getStringExtra("taskId"))
        i2.putExtra("taskName",intent.getStringExtra("taskName"))
        i2.putExtra("taskTimeDead",intent.getStringExtra("taskTimeDead"))
        i2.putExtra("taskTimeHour",intent.getStringExtra("taskTimeHour"))
        i2.putExtra("taskDesc",intent.getStringExtra("taskDesc"))
        i2.putExtra("taskImage",intent.getStringExtra("taskImage"))
        i2.putExtra("fragment_to_display", check)
        startActivity(i2)
    }

    private fun deleteTask() {
        val tId = intent.getStringExtra("taskId").toString()
        val dbRef = FirebaseDatabase.getInstance().getReference("Task").child(tId)
        val nTassk = dbRef.removeValue()
        nTassk.addOnSuccessListener {
            val storageRef = FirebaseStorage.getInstance().reference
            val desertRef = storageRef.child("images_task/${tId}.jpg")
            desertRef.delete().addOnSuccessListener {  }
                .addOnFailureListener {  }

            Toast.makeText(this, "Xóa thành công", Toast.LENGTH_SHORT).show()

            val check = intent.getStringExtra("fragment_import")

            val ii = Intent(this, HomeActivity::class.java)
            ii.putExtra("fragment_to_display", check)
            startActivity(ii)
        }
    }

    private fun setValueToView() {
        binding.tvTieuDeTask.text = intent.getStringExtra("taskName")
        binding.tvThoiGianTask.text = intent.getStringExtra("taskTimeDead")
        binding.tvNoiDungTask.text = intent.getStringExtra("taskDesc")
        binding.tvThoiGianTimeTask.text = intent.getStringExtra("taskTimeHour")
        if (intent.getStringExtra("taskImage") != null) {
            Glide.with(this)
                .load(intent.getStringExtra("taskImage"))
                .into(binding.imageViewTask)
        } else {
            binding.linearImage.visibility = View.GONE
        }

        if (intent.getStringExtra("taskDateCom").isNullOrEmpty()) {
            binding.linearTaskCom.visibility = View.GONE
        } else {
            binding.tvThoiGianTaskCom.text = intent.getStringExtra("taskDateCom")
        }
    }
}