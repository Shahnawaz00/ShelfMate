package com.example.booktracker

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

// Logging tag for debugging
private const val TAG = "BookAdapter"

class BookAdapter(
    private val onDeleteClick: (Book) -> Unit,
    private val onUpdateClick: (Book) -> Unit
) : ListAdapter<Book, BookAdapter.BookViewHolder>(BookDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        // Inflate the item layout for each book
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book, parent, false)
        Log.d(TAG, "ViewHolder created")
        return BookViewHolder(view, onDeleteClick, onUpdateClick)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = getItem(position) // Retrieve the book at the current position
        Log.d(TAG, "Binding book at position $position: $book")
        holder.bind(book) // Bind the book data to the ViewHolder
    }

    class BookViewHolder(
        itemView: View,
        private val onDeleteClick: (Book) -> Unit,
        private val onUpdateClick: (Book) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val authorTextView: TextView = itemView.findViewById(R.id.authorTextView)
        private val statusTextView: TextView = itemView.findViewById(R.id.statusTextView)
        private val deleteButton: AppCompatImageButton = itemView.findViewById(R.id.deleteButton)
        private val updateButton: AppCompatImageButton = itemView.findViewById(R.id.updateButton)

        init {
            // Set up click listeners for delete and update buttons
            deleteButton.setOnClickListener {
                val book = itemView.tag as Book // Retrieve the book object from the tag
                Log.d(TAG, "Delete button clicked for book: $book")
                onDeleteClick(book) // Call the delete function passed in
            }

            updateButton.setOnClickListener {
                val book = itemView.tag as Book // Retrieve the book object from the tag
                Log.d(TAG, "Update button clicked for book: $book")
                onUpdateClick(book) // Call the update function passed in
            }
        }

        // Binds the book data to the UI elements
        fun bind(book: Book) {
            // Set the tag to the current book for later reference in click listeners
            itemView.tag = book

            // Bind the book title and author to their respective TextViews
            titleTextView.text = book.title
            authorTextView.text = book.author
            statusTextView.text = book.status // Display the book status

            Log.d(TAG, "Book bound: ${book.title} by ${book.author}, Status: ${book.status}")
        }
    }

    // DiffUtil.Callback implementation for efficient list updates
    class BookDiffCallback : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
            Log.d(TAG, "Checking if items are the same: ${oldItem.id} vs ${newItem.id}")
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
            Log.d(TAG, "Checking if contents are the same: $oldItem vs $newItem")
            return oldItem == newItem
        }
    }
}
