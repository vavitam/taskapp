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
import com.example.taskapp.databinding.ActivityDetailNoteBinding
import com.example.taskapp.model.NoteModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DetailNoteActivity : AppCompatActivity() {
    lateinit var binding: ActivityDetailNoteBinding
    private lateinit var dbRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        getValueNote()

        dbRef = FirebaseDatabase.getInstance().getReference("Note")

        binding.btnMoreNote.setOnClickListener {
            showPopupView(it)
        }

        binding.btnLeftTask.setOnClickListener {
            UpdateTask()
        }
    }

    private fun showPopupView(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.menu_note_options, popup.menu)

        popup.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.menu_copy -> {
                    copyTask()
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

    private fun copyTask() {
        val title = binding.edtTieuDeTask.text.toString() + " - copy"
        val desc = binding.edtNoiDungTask.text.toString()
        val noteId = dbRef.push().key!!
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        if ((title.isEmpty()) || (desc.isEmpty())) {
            Toast.makeText(this, "Chỗ điền đã trống!!", Toast.LENGTH_SHORT).show()
            return
        } else {

            val currentDate = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val formattedDate = currentDate.format(formatter)

            val note = NoteModel(noteId, userId, title, desc, formattedDate)
            dbRef.child(noteId).setValue(note)
                .addOnCompleteListener {
                    Toast.makeText(this, "Complete", Toast.LENGTH_SHORT).show()
                    thoat()
                }
                .addOnFailureListener {
                    Toast.makeText(this,"Error ${it.message}", Toast.LENGTH_SHORT).show()
                    thoat()
                }
        }
    }

    private fun deleteTask() {
        val tId = intent.getStringExtra("noteId").toString()
        val dbRef = FirebaseDatabase.getInstance().getReference("Note").child(tId)
        val nTassk = dbRef.removeValue()
        nTassk.addOnSuccessListener {
            Toast.makeText(this, "Xóa thành công", Toast.LENGTH_SHORT).show()

            thoat()
        }
    }

    private fun thoat() {
        val ii = Intent(this, HomeActivity::class.java)
        ii.putExtra("fragment_to_display", "NoteFragment")
        startActivity(ii)
    }

    private fun UpdateTask() {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val formattedDate = currentDate.format(formatter)

        val taskId = intent.getStringExtra("noteId")
        val dbRef = FirebaseDatabase.getInstance().getReference("Note").child(taskId.toString())
        dbRef.child("noteTitle").setValue(binding.edtTieuDeTask.text.toString())
        dbRef.child("noteDesc").setValue(binding.edtNoiDungTask.text.toString())
        if ((binding.edtTieuDeTask.text.toString() != intent.getStringExtra("noteName")) || (binding.edtNoiDungTask.text.toString() != intent.getStringExtra("noteDesc"))) {
            dbRef.child("noteTime").setValue(formattedDate)
        }
        thoat()
    }

    private fun getValueNote() {
        binding.edtTieuDeTask.setText(intent.getStringExtra("noteName"))
        binding.edtNoiDungTask.setText(intent.getStringExtra("noteDesc"))
        binding.tvTimeNote.text = intent.getStringExtra("noteTime")
    }
}