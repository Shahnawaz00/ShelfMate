package com.example.booktracker

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class AddBookActivity : AppCompatActivity() {

    private lateinit var bookViewModel: BookViewModel
    private lateinit var titleEditText: EditText
    private lateinit var authorEditText: EditText
    private lateinit var statusDropdown: Spinner
    private lateinit var addButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)

        // Initialize ViewModel
        bookViewModel = ViewModelProvider(this).get(BookViewModel::class.java)

        // Find views by ID
        titleEditText = findViewById(R.id.bookTitleInput)
        authorEditText = findViewById(R.id.bookAuthorInput)
        statusDropdown = findViewById(R.id.bookStatusDropdown)
        addButton = findViewById(R.id.addBookButton)

        // Populate status spinner
        val statuses = resources.getStringArray(R.array.book_statuses)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statuses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        statusDropdown.adapter = adapter

        // Add button click listener
        addButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val author = authorEditText.text.toString()
            val status = statusDropdown.selectedItem.toString()

            if (title.isNotEmpty() && author.isNotEmpty()) {
                val newBook = Book(0, title, author, status)
                bookViewModel.addBook(newBook)

                Log.d("AddBookActivity", "Book added: $newBook")
                // Show confirmation message
                Toast.makeText(this, "Book added successfully", Toast.LENGTH_SHORT).show()

                finish()  // Close activity after adding the book
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
