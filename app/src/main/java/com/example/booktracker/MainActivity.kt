package com.example.booktracker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Load the BookListFragment as the default fragment, only if this is the first time activity is created
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, BookListFragment()) // Replacing fragment container with BookListFragment
                .commit()
        }

        // Find the FloatingActionButton and set a click listener to launch AddBookActivity
        val addButton = findViewById<FloatingActionButton>(R.id.fab_add_book)
        if (addButton != null) { // Null check to ensure button exists in layout
            addButton.setOnClickListener {
                // Create an intent to launch AddBookActivity
                val intent = Intent(this, AddBookActivity::class.java)
                startActivity(intent)
            }
        } else {
            // Handle case where the button is not found (e.g., in different layouts or screen orientations)
            Toast.makeText(this, "Add button not found", Toast.LENGTH_SHORT).show()
        }
    }
}
