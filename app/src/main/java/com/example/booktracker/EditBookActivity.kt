package com.example.booktracker

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditBookActivity : AppCompatActivity() {

    private lateinit var bookViewModel: BookViewModel
    private var bookId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_book)

        // Initialize views
        val titleInput: EditText = findViewById(R.id.bookTitleInput)
        val authorInput: EditText = findViewById(R.id.bookAuthorInput)
        val statusDropdown: Spinner = findViewById(R.id.bookStatusDropdown)
        val saveButton: Button = findViewById(R.id.saveButton)

        // Retrieve the book ID from the Intent
        bookId = intent.getIntExtra("BOOK_ID", -1) // Default -1 for invalid ID

        // Initialize ViewModel
        bookViewModel = ViewModelProvider(this)[BookViewModel::class.java]

        // Populate Spinner with book statuses
        val statusArray = resources.getStringArray(R.array.book_statuses) // Retrieve status array from resources
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusArray) // Create Spinner adapter
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // Set dropdown layout
        statusDropdown.adapter = adapter // Set adapter to the Spinner

        if (bookId != -1) { // Valid book ID check
            // Fetch and populate the book data
            lifecycleScope.launch(Dispatchers.IO) {
                val book = bookViewModel.getBookById(bookId)
                withContext(Dispatchers.Main) {
                    if (book != null) {
                        titleInput.setText(book.title)
                        authorInput.setText(book.author)
                        // Set the dropdown to the current status
                        statusDropdown.setSelection(statusArray.indexOf(book.status))
                    } else {
                        // Handle case where the book is not found in the database
                        Toast.makeText(this@EditBookActivity, "Book not found", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, "Invalid Book ID", Toast.LENGTH_SHORT).show()
            finish() // Close the activity if the ID is invalid
        }

        // Save button click listener
        saveButton.setOnClickListener {
            val updatedBook = Book(
                id = bookId,
                title = titleInput.text.toString(),
                author = authorInput.text.toString(),
                status = statusDropdown.selectedItem?.toString() ?: "Unknown" // Handle null case
            )
            lifecycleScope.launch {
                bookViewModel.updateBook(updatedBook)
            }
            finish() // Close the activity and return to the previous one
        }
    }
}
