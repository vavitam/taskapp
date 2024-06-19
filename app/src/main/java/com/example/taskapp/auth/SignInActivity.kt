package com.example.taskapp.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.taskapp.HomeActivity
import com.example.taskapp.R
import com.example.taskapp.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignInBinding
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.tvDangKy.setOnClickListener {
            val i = Intent(this, RegisterActivity::class.java)
            startActivity(i)
        }

        firebaseAuth= FirebaseAuth.getInstance()
        binding.btnDangNhap.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val pass = binding.edtPassword.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                binding.progressBar.visibility = View.VISIBLE
                binding.linBackground.visibility = View.GONE

                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {

                        val i = Intent(this, HomeActivity::class.java)
                        startActivity(i)
                    } else {
                        Toast.makeText(this, "Đăng nhập không thành công", Toast.LENGTH_SHORT).show()
                    }

                }
                    .addOnFailureListener {
                        Toast.makeText(this, "Đăng nhập không thành công", Toast.LENGTH_SHORT).show()
                    }
                binding.progressBar.visibility = View.GONE
                binding.linBackground.visibility = View.VISIBLE
            } else {
                Toast.makeText(this, "Thông tin điền bị thiếu", Toast.LENGTH_SHORT).show()
            }
        }
    }
}