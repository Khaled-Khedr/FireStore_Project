package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.classes.Note
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var saveButton: Button
    private lateinit var loadButton: Button
    private lateinit var textViewData: TextView
    private lateinit var updateTitleButton: Button
    private lateinit var deleteDescriptionButton: Button
    private lateinit var deleteNoteButton: Button

    //private lateinit var listener:ListenerRegistration  //allows us to remove a listener when we remove the app
    private val db: FirebaseFirestore =
        FirebaseFirestore.getInstance()  //getting an instance of the db
    private val docRf: DocumentReference = db.collection("Notebook").document("My first note")

    //a document reference in our db so we dont keep on typing this over and over again
    private val KEY_TITLE = "title"
    private val KEY_DESCRIPTION = "description"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextTitle = findViewById(R.id.edit_text_title)
        editTextDescription = findViewById(R.id.edit_text_description)
        saveButton = findViewById(R.id.button_save)
        loadButton = findViewById(R.id.load_button)
        deleteDescriptionButton = findViewById(R.id.button_delete_description)
        deleteNoteButton = findViewById(R.id.button_delete_note)
        textViewData = findViewById(R.id.text_view_data)
        updateTitleButton = findViewById(R.id.button_update_title)

        saveButton.setOnClickListener {
            save()
        }

        loadButton.setOnClickListener {
            load()
        }

        updateTitleButton.setOnClickListener {
            updateTitle()
        }

        deleteDescriptionButton.setOnClickListener {
            deleteDescription()
        }

        deleteNoteButton.setOnClickListener {
            deleteNote()
        }

    }

    override fun onStart() {
        super.onStart()
        //the snapshot listener is called when the application starts

        docRf.addSnapshotListener(this) { document, error ->  //listens to db changes in real time
            //by adding this it removes itself when app goes in the background (same as listener.remove() and listenerRegistration)
            error?.let {  //checking if error isn't null
                return@addSnapshotListener
            }

            document?.let {
                if (it.exists()) {
                  //  val title = it.getString(KEY_TITLE)
                  //  val description = it.getString(KEY_DESCRIPTION)

                    val note=it.toObject(Note::class.java) //converting this document snapshot to an object of type Note class

                    textViewData.text = "Title: " + "${note?.title} \nDescription: ${note?.description}"

                } else {
                    textViewData.text="" //we add it here cause if the document doesnt exist it wont display a textbox even
                    Toast.makeText(
                        this@MainActivity,
                        "The document doesn't exist",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

        }
    }

    /*override fun onStop() {
        super.onStop()
        listener.remove()

    }  */

    private fun save() {
        val title = editTextTitle.text.toString()
        val description = editTextDescription.text.toString()
    /*  //THE FIRST METHOD MAP
        val note =
            mutableMapOf<String, Any>()  //in firebase data is stored in pairs so we need a mutable map
        note.put(KEY_TITLE, title)
        note.put(KEY_DESCRIPTION, description)

     */
        val note= Note(title, description)

        db.collection("Notebook").document("My first note").set(note)
            //or u can type docRf.set(note) since we made a reference
            //setting a collection name and a document name firebase can do it auto tho then setting it to the note map
            .addOnSuccessListener {
                Toast.makeText(this@MainActivity, "Note added!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this@MainActivity, "Error: note was not added!", Toast.LENGTH_SHORT)
                    .show()

            }

    }

    private fun load() {
        docRf.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val title = editTextTitle.text.toString()
                    val description = editTextDescription.text.toString()
                    //val title = document.getString(KEY_TITLE)
                    //val description = document.getString(KEY_DESCRIPTION)
                    val note= Note(title, description)
                    textViewData.text = "Title: " + "${note.title} \nDescription: ${note.description}"

                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "The document doesn't exist",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this@MainActivity, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateTitle() {
        val title = editTextTitle.text.toString()
        val note = mutableMapOf<String, Any>()
        note[KEY_TITLE] = title //putting the edit text value into the key_title pair
        docRf.set(
            note,
            SetOptions.merge()
        ) //merge the data basically updates a single field without overriding the other fields (if there is no document it creates one)
        // docRf.update(note) //or you can do docRf.update(KEY_TITLE,title) if there is no document it auto creates one for you updates title without touching description

    }

    private fun deleteDescription() {
        /*
        val description=editTextDescription.text.toString()
        val note= mutableMapOf<String,Any>()
        note[KEY_DESCRIPTION]=description.replace(description," ")
        docRf.set(note, SetOptions.merge())   */ //my way of replacing the field value to empty and updating it in db
        val note = mutableMapOf<String, Any>()
        note[KEY_DESCRIPTION] = FieldValue.delete() //deletes the field value entirely u wont even see description in the db
        docRf.update(note)
    }

    private fun deleteNote() {

        docRf.delete()  //delete the whole note


    }


}