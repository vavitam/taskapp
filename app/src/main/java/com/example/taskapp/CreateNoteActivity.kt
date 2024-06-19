package com.example.taskapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.taskapp.databinding.ActivityCreateNoteBinding
import com.example.taskapp.databinding.ActivityCreateTaskBinding
import com.example.taskapp.model.NoteModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CreateNoteActivity : AppCompatActivity() {
    lateinit var binding: ActivityCreateNoteBinding
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreateNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbRef = FirebaseDatabase.getInstance().getReference("Note")
        // Xử lý sự kiện khi click vào nút
        binding.btnLeftTask.setOnClickListener {
            val title = binding.edtTieuDeTask.text.toString().trim()
            val desc = binding.edtNoiDungTask.text.toString().trim()
            val noteId = dbRef.push().key!!
            val userId = FirebaseAuth.getInstance().currentUser!!.uid

            if ((title.isEmpty()) || (desc.isEmpty())) {
                trangChu()
            } else {

                val currentDate = LocalDate.now()
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val formattedDate = currentDate.format(formatter)

                val note = NoteModel(noteId, userId, title, desc, formattedDate)
                dbRef.child(noteId).setValue(note)
                    .addOnCompleteListener {
                        Toast.makeText(this, "Complete", Toast.LENGTH_SHORT).show()
                        trangChu()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this,"Error ${it.message}", Toast.LENGTH_SHORT).show()
                        trangChu()
                    }
            }

        }

    }

    private fun trangChu() {
        val i = Intent(this, HomeActivity::class.java)
        i.putExtra("fragment_to_display", "NoteFragment")
        startActivity(i)
    }
}