package com.example.taskapp.auth

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
import com.example.taskapp.AccountActivity
import com.example.taskapp.R
import com.example.taskapp.databinding.ActivityEditAccountBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage

class EditAccountActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditAccountBinding
    lateinit var firebaseAuth: FirebaseAuth
    private var selectedImageUri: Uri? = null
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setData()

        binding.btnSave.setOnClickListener {
            updateData()
        }

        binding.imageBtnLeft.setOnClickListener {
            chuyenLai()
        }

        binding.tvEditPassWorld.setOnClickListener {
            val i = Intent(this, ChangePassActivity::class.java)
            startActivity(i)
        }

        binding.imageUserAccount.setOnClickListener {
            pickImage.launch("image/*")
        }
    }
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) {
            binding.imageUserAccount.setImageURI(it)
            selectedImageUri = it
        }
    }

    private fun chuyenLai() {
        val i = Intent(this, AccountActivity::class.java)
        startActivity(i)
    }

    private fun updateData() {
        binding.linBackground.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE

        val sName = binding.edtEditName.text.toString()
        val sPhone = binding.edtEditPhone.text.toString()
        val sMail = FirebaseAuth.getInstance().currentUser!!.email

        if (selectedImageUri != null) {
            val uID = FirebaseAuth.getInstance().currentUser!!.uid
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("images_user/${uID}.jpg")
            val uploadTask = imageRef.putFile(selectedImageUri!!)
            uploadTask.addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri->
                    db.collection("users").document(uID)
                        .update("image", uri)
                        .addOnSuccessListener {  }
                        .addOnFailureListener {  }
                }
            }
        }

        val updateMap = mapOf(
            "email" to sMail,
            "name" to sName,
            "phone" to sPhone
        )

        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        db.collection("users").document(userID).update(updateMap)

        binding.linBackground.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE

        chuyenLai()
        Toast.makeText(this, "Successfully", Toast.LENGTH_SHORT).show()
    }

    private fun setData() {
        binding.linBackground.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE

        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = db.collection("users").document(userID)
        ref.get().addOnSuccessListener {
            if (it != null) {
                val name = it.data?.get("name")?.toString()
                val phone = it.data?.get("phone")?.toString()
                val image = it.data?.get("image")?.toString()

                binding.edtEditName.setText(name)
                binding.edtEditPhone.setText(phone)
                Glide.with(this)
                    .load(image)
                    .into(binding.imageUserAccount)

                binding.linBackground.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
        }
            .addOnFailureListener {
                Toast.makeText(this,"Failed!", Toast.LENGTH_SHORT).show()
            }
    }
}