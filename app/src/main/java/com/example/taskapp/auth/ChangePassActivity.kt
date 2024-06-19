package com.example.taskapp.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.taskapp.AccountActivity
import com.example.taskapp.R
import com.example.taskapp.databinding.ActivityChangePassBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ChangePassActivity : AppCompatActivity() {
    lateinit var binding: ActivityChangePassBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChangePassBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        binding.btnSave.setOnClickListener {
            thayDoi()
        }

        binding.imageBtnLeft.setOnClickListener {
            chuyen()
        }
    }

    private fun chuyen() {
        val i = Intent(this, AccountActivity::class.java)
        startActivity(i)
    }

    private fun thayDoi() {
        val newPass = binding.edtNewPass.text.toString()
        val reNewPass = binding.edtReNewPass.text.toString()

        binding.progressBar.visibility = View.VISIBLE
        binding.linBackground.visibility = View.GONE

        if (newPass.isEmpty()) {
            Toast.makeText(this,"Chưa nhập mật khẩu mới!", Toast.LENGTH_SHORT).show()
            return
        }

        if (reNewPass.isEmpty()) {
            Toast.makeText(this,"Chưa xác nhận mật khẩu!", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPass != reNewPass) {
            Toast.makeText(this,"Mật khẩu chưa khớp với xác nhận!", Toast.LENGTH_SHORT).show()
            return
        }

        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            user.updatePassword(newPass)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this,"Thay đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show()
                        chuyen()
                    } else {
                        Toast.makeText(this, "Đã xảy ra lỗi khi thay đổi mật khẩu: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.linBackground.visibility = View.GONE
    }


}