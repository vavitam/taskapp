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
import com.bumptech.glide.Glide
import com.example.taskapp.databinding.ActivityUpdateTaskBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.Calendar

class UpdateTaskActivity : AppCompatActivity() {
    lateinit var binding: ActivityUpdateTaskBinding
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUpdateTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.imageViewTask.visibility = View.GONE
        binding.btnDeleteImage.visibility = View.GONE

        setvalueToView()

        binding.btnLeftTask.setOnClickListener {
            backToHome()
        }

        binding.btnUpdateTask.setOnClickListener {
            updateTask()
        }

        binding.btnImageTask.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnDeleteImage.setOnClickListener {
            binding.imageViewTask.setImageDrawable(null)
            selectedImageUri = null
            binding.imageViewTask.visibility = View.GONE
            binding.btnDeleteImage.visibility = View.GONE
        }

        binding.edtThoiGianTask.setOnClickListener {
            val currentDate = Calendar.getInstance()
            val dateText = binding.edtThoiGianTask.text.toString()

            if (dateText.isNotEmpty()) {
                try {
                    val dateParts = dateText.split("/")
                    val day = dateParts[0].toInt()
                    val month = dateParts[1].toInt() - 1 // month is 0-based in Calendar
                    val year = dateParts[2].toInt()
                    currentDate.set(year, month, day)
                } catch (e: Exception) {
                    // Handle the exception if the date is not in the expected format
                    e.printStackTrace()
                }
            }

            val currentYear = currentDate.get(Calendar.YEAR)
            val currentMonth = currentDate.get(Calendar.MONTH)
            val currentDay = currentDate.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                val formattedDay = String.format("%02d", dayOfMonth)
                val formattedMonth = String.format("%02d", month + 1)
                binding.edtThoiGianTask.setText("$formattedDay/$formattedMonth/$year")
            }, currentYear, currentMonth, currentDay).show()
        }

        binding.edtThoiGianTimeTask.setOnClickListener {
            val currentTime = Calendar.getInstance()
            val timeText = binding.edtThoiGianTimeTask.text.toString()

            if (timeText.isNotEmpty()) {
                try {
                    val timeParts = timeText.split(":")
                    val hour = timeParts[0].toInt()
                    val minute = timeParts[1].toInt()
                    currentTime.set(Calendar.HOUR_OF_DAY, hour)
                    currentTime.set(Calendar.MINUTE, minute)
                } catch (e: Exception) {
                    // Handle the exception if the time is not in the expected format
                    e.printStackTrace()
                }
            }

            val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
            val currentMinute = currentTime.get(Calendar.MINUTE)

            TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                val formattedHour = String.format("%02d", hourOfDay)
                val formattedMinute = String.format("%02d", minute)
                binding.edtThoiGianTimeTask.setText("$formattedHour:$formattedMinute")
            }, currentHour, currentMinute, true).show()
        }
    }

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) {
            binding.imageViewTask.setImageURI(it)
            selectedImageUri = it
        }

        binding.imageViewTask.visibility = View.VISIBLE
        binding.btnDeleteImage.visibility = View.VISIBLE
    }

    private fun updateTask() {
        val taskName = binding.edtTieuDeTask.text.toString().trim()
        val taskTimeDead = binding.edtThoiGianTask.text.toString()
        val taskTimeHour = binding.edtThoiGianTimeTask.text.toString()
        val taskDesc = binding.edtNoiDungTask.text.toString().trim()
        val taskId = intent.getStringExtra("taskId")
        val dbRef = FirebaseDatabase.getInstance().getReference("Task").child(taskId.toString())

        if (selectedImageUri != null) {
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("images_task/${taskId}.jpg")
            val uploadTask = imageRef.putFile(selectedImageUri!!)

            uploadTask.addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    dbRef.child("taskImage").setValue(uri.toString())
                }
            }
        } else {
            dbRef.child("taskImage").setValue("")
        }

        dbRef.child("taskTitle").setValue(taskName)
        dbRef.child("taskTimeDead").setValue(taskTimeDead)
        dbRef.child("taskDesc").setValue(taskDesc)
        dbRef.child("taskTimeHour").setValue(taskTimeHour)

        backToHome()
    }

    private fun backToHome() {
        val i3 = Intent(this, HomeActivity::class.java)
        startActivity(i3)
    }

    private fun setvalueToView() {
        binding.edtTieuDeTask.setText(intent.getStringExtra("taskName"))
        binding.edtThoiGianTask.setText(intent.getStringExtra("taskTimeDead"))
        binding.edtThoiGianTimeTask.setText(intent.getStringExtra("taskTimeHour"))
        binding.edtNoiDungTask.setText(intent.getStringExtra("taskDesc"))
        if (intent.getStringExtra("taskImage") != "") {
            Glide.with(this)
                .load(intent.getStringExtra("taskImage"))
                .into(binding.imageViewTask)
            selectedImageUri = Uri.parse(intent.getStringExtra("taskImage"))
            binding.imageViewTask.visibility = View.VISIBLE
            binding.btnDeleteImage.visibility = View.VISIBLE
            Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show()
        }
    }
}