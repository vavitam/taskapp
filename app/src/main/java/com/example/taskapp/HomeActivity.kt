package com.example.taskapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.taskapp.auth.AuthenActivity
import com.example.taskapp.databinding.ActivityHomeBinding
import com.example.taskapp.fragment.ChatFragment
import com.example.taskapp.fragment.ImportTaskFragment
import com.example.taskapp.fragment.NoteFragment
import com.example.taskapp.fragment.TaskFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.navView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.open_nav, R.string.close_nav)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction().replace(R.id.frameLayout, TaskFragment()).commit()
//            binding.navView.setCheckedItem(R.id.navTask)

            val fragmentToDisplay = intent.getStringExtra("fragment_to_display")
            if (fragmentToDisplay == "NoteFragment") {
                replaceFragment(NoteFragment())
                binding.toolbar.title = "Danh sách ghi chú"
                binding.navView.setCheckedItem(R.id.navNote)

            } else if (fragmentToDisplay == "ImportTaskFragment") {
                replaceFragment(ImportTaskFragment())
                binding.toolbar.title = "Nhiệm vụ quan trọng"
                binding.navView.setCheckedItem(R.id.navImport)

            } else if (fragmentToDisplay == "ChatFragment") {
                replaceFragment(ChatFragment())
                binding.toolbar.title = "Trao đổi thông tin"
                binding.navView.setCheckedItem(R.id.navChat)

            } else {
                replaceFragment(TaskFragment())
                binding.toolbar.title = "Danh sách nhiệm vụ"
                binding.navView.setCheckedItem(R.id.navTask)
            }
        }

//        replaceFragment(TaskFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }



    private fun taiKhoan() {
        val i = Intent(this, AccountActivity::class.java)
        startActivity(i)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navTask -> {
                replaceFragment(TaskFragment())
                binding.toolbar.title = "Danh sách nhiệm vụ"
            }
            R.id.navNote -> {
                replaceFragment(NoteFragment())
                binding.toolbar.title = "Danh sách ghi chú"
            }
            R.id.navImport -> {
                replaceFragment(ImportTaskFragment())
                binding.toolbar.title = "Nhiệm vụ quan trọng"
            }
            R.id.navChat -> {
                replaceFragment(ChatFragment())
                binding.toolbar.title = "Trao đổi thông tin"
            }
            R.id.navAccount -> taiKhoan()
            R.id.navFeed -> phanHoi()
            R.id.nav_logout -> showLogOutDialog()
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun phanHoi() {
        val email = "tamvinhxuan123@gmail.com"
        val subject = "Phản hồi về ứng dụng"
        val uriText = "mailto:$email" +
                "?subject=" + Uri.encode(subject)

        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.data = Uri.parse(uriText)

        // Kiểm tra xem thiết bị có ứng dụng email được cài đặt không
        if (emailIntent.resolveActivity(packageManager) != null) {
            startActivity(emailIntent)
        } else {
            // Xử lý trường hợp không có ứng dụng email
            Toast.makeText(this, "Không tìm thấy ứng dụng email.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLogOutDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to logout?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            logOut()
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun logOut() {
        // Đăng xuất khỏi Firebase
        val auth = FirebaseAuth.getInstance()
        auth.signOut()

        // Chuyển hướng về màn hình đăng nhập
        val intent = Intent(this, AuthenActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

}