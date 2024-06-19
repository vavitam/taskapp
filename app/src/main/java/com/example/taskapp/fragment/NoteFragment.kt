package com.example.taskapp.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskapp.CreateNoteActivity
import com.example.taskapp.DetailNoteActivity
import com.example.taskapp.adapter.NoteAdapter
import com.example.taskapp.databinding.FragmentNoteBinding
import com.example.taskapp.model.NoteModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class NoteFragment : Fragment() {
    lateinit var binding: FragmentNoteBinding
    private lateinit var dbRef: DatabaseReference
    private lateinit var dsNote:ArrayList<NoteModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoteBinding.inflate(layoutInflater)

        binding.rcvNote.layoutManager = LinearLayoutManager(context)
        binding.rcvNote.setHasFixedSize(true)
        dsNote = arrayListOf<NoteModel>()
        getDsNote()

        binding.btnThemNote.setOnClickListener {
            val i2 = Intent(requireContext(), CreateNoteActivity::class.java)
            startActivity(i2)
        }

        return binding.root
    }

    private fun getDsNote() {

        val uId = FirebaseAuth.getInstance().currentUser!!.uid
        dbRef = FirebaseDatabase.getInstance().getReference("Note")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dsNote.clear()
                if (snapshot.exists()) {
                    for (noteSnap in snapshot.children) {
                        val noteData = noteSnap.getValue(NoteModel::class.java)
                        noteData?.let {
                            if (it.userId == uId) {
                                dsNote.add(it)
                                binding.FolderEmpty.visibility = View.GONE
                            }
                        }
                    }

                    val nAdapter = NoteAdapter(dsNote)
                    binding.rcvNote.adapter = nAdapter

                    nAdapter.notifyDataSetChanged()

                   nAdapter.setOnItemClickListener(object : NoteAdapter.onItemClickListener{
                       override fun onItemClick(position: Int) {
                           val i1 = Intent(context, DetailNoteActivity::class.java)
                           i1.putExtra("noteId",dsNote[position].noteId)
                           i1.putExtra("noteName",dsNote[position].noteTitle)
                           i1.putExtra("noteDesc",dsNote[position].noteDesc)
                           i1.putExtra("noteTime",dsNote[position].noteTime)
                           startActivity(i1)
                       }

                   })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}