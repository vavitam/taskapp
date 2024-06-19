package com.example.taskapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.taskapp.databinding.ActivityCreateTaskBinding
import com.example.taskapp.model.TaskModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.Calendar

class CreateTaskActivity : AppCompatActivity() {
    lateinit var binding: ActivityCreateTaskBinding
    private lateinit var dbRef: DatabaseReference
    private var selectedImageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreateTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbRef = FirebaseDatabase.getInstance().getReference("Task")
        // Xử lý sự kiện khi click vào nút
        binding.btnLeftTask.setOnClickListener {
            trangChu()
        }

        // Save
        binding.btnCreateTask.setOnClickListener {
            if (binding.edtTieuDeTask.text.toString().isEmpty()) {
                Toast.makeText(this, "Tiêu đề đang trống!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (binding.edtThoiGianTask.text.toString().isEmpty()) {
                Toast.makeText(this, "Bạn chưa chọn ngày hạn!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            saveTask()

        }

        binding.edtThoiGianTask.setOnClickListener {
            val currentDate = Calendar.getInstance()
            val currentYear = currentDate.get(Calendar.YEAR)
            val currentMonth = currentDate.get(Calendar.MONTH)
            val currentDay = currentDate.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this,DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                val formattedDay = String.format("%02d", dayOfMonth)
                val formattedMonth = String.format("%02d", month + 1)
                binding.edtThoiGianTask.setText("$formattedDay/$formattedMonth/$year")
            }, currentYear, currentMonth, currentDay).show()
        }

        binding.edtThoiGianTimeTask.setOnClickListener {
            val currentTime = Calendar.getInstance()
            val hour = currentTime.get(Calendar.HOUR_OF_DAY)
            val minute = currentTime.get(Calendar.MINUTE)

            TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                val formattedHour = String.format("%02d", hourOfDay)
                val formattedMinute = String.format("%02d", minute)
                binding.edtThoiGianTimeTask.setText("$formattedHour:$formattedMinute")
            }, hour, minute, true).show()
        }

        binding.btnImageTask.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnDeleteImage.setOnClickListener {
            binding.imageViewTask.setImageDrawable(null)
            binding.imageViewTask.visibility = View.GONE
            selectedImageUri = null
            binding.btnDeleteImage.visibility = View.GONE
        }
    }

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) {
            binding.imageViewTask.setImageURI(it)
            selectedImageUri = it
        }
        binding.btnDeleteImage.visibility = View.VISIBLE
        binding.imageViewTask.visibility = View.VISIBLE
    }

    private fun saveTask() {
        val taskId = dbRef.push().key!!
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val taskTitle = binding.edtTieuDeTask.text.toString().trim()
        val taskTimeDead = binding.edtThoiGianTask.text.toString()
        val taskTimeHour = binding.edtThoiGianTimeTask.text.toString()
        val taskDesc = binding.edtNoiDungTask.text.toString().trim()

        if (selectedImageUri != null) {
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("images_task/${taskId}.jpg")
            val uploadTask = imageRef.putFile(selectedImageUri!!)
            uploadTask.addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    val task = TaskModel(taskId, userId, taskTitle, taskTimeDead, taskTimeHour , taskDesc, downloadUrl.toString(), false,false, "")
                    dbRef.child(taskId).setValue(task)
                        .addOnCompleteListener {
                            Toast.makeText(this, "Complete", Toast.LENGTH_SHORT).show()
                            trangChu()
                        }
                        .addOnFailureListener {err ->
                            Toast.makeText(this,"Error ${err.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        } else {
            val task = TaskModel(taskId, userId, taskTitle, taskTimeDead, taskTimeHour , taskDesc, "", false,false, "")
            dbRef.child(taskId).setValue(task)
                .addOnCompleteListener {
                    Toast.makeText(this, "Complete", Toast.LENGTH_SHORT).show()
                    trangChu()
                }
                .addOnFailureListener {err ->
                    Toast.makeText(this,"Error ${err.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun trangChu() {
        val i = Intent(this, HomeActivity::class.java)
        startActivity(i)
    }
}