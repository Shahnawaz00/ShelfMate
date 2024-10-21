package com.example.booktracker

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BookViewModel(application: Application) : AndroidViewModel(application) {

    private val bookDao: BookDao = BookDatabase.getDatabase(application).bookDao()
    private val _filteredBooks = MutableLiveData<List<Book>?>()
    val filteredBooks: LiveData<List<Book>?> get() = _filteredBooks

    private var currentFilterStatus: String = "All" // Default filter status
    private var currentSortOrder: SortOrder = SortOrder.RECENTLY_ADDED // Default sort order

    // Enum for sort options
    enum class SortOrder {
        A_TO_Z, Z_TO_A, RECENTLY_ADDED
    }

    init {
        // Initialize with all books sorted by recently added
        loadBooks()
    }

    fun addBook(book: Book) {
        viewModelScope.launch(Dispatchers.IO) {
            bookDao.insertBook(book)
            Log.d("BookViewModel", "Book added: ${book.title}")
            loadBooks()
        }
    }

    fun getBookById(bookId: Int): Book {
        return bookDao.getBookById(bookId)
    }

    fun updateBook(book: Book) {
        viewModelScope.launch(Dispatchers.IO) {
            bookDao.updateBook(book)
            loadBooks()
        }
    }

    fun deleteBook(book: Book) {
        viewModelScope.launch(Dispatchers.IO) {
            bookDao.deleteBook(book)
            loadBooks()
        }
    }

    // Apply both filter and sort logic using DAO methods
    fun loadBooks() {
        Log.d("BookViewModel", "loadBooks called") // Log when loadBooks is invoked
        viewModelScope.launch(Dispatchers.IO) {
            val filteredBooks = if (currentFilterStatus == "All") {
                // When "All" is selected, retrieve all books
                when (currentSortOrder) {
                    SortOrder.A_TO_Z -> bookDao.sortBooksAscending()
                    SortOrder.Z_TO_A -> bookDao.sortBooksDescending()
                    SortOrder.RECENTLY_ADDED -> bookDao.sortBooksRecentlyAdded()
                }
            } else {
                // When a specific status is selected, retrieve and sort based on the status
                when (currentSortOrder) {
                    SortOrder.A_TO_Z -> bookDao.getBooksByStatusSortedByTitle(currentFilterStatus)
                    SortOrder.Z_TO_A -> bookDao.getBooksByStatusSortedByTitleDescending(currentFilterStatus)
                    SortOrder.RECENTLY_ADDED -> bookDao.getBooksByStatusSortedByRecentlyAdded(currentFilterStatus)
                }
            }

            Log.d("BookViewModel", "Books loaded: ${filteredBooks.size}")
            _filteredBooks.postValue(filteredBooks)
        }
    }

    // Filter by status and apply the current sort order
    fun filterByStatus(status: String) {
        currentFilterStatus = status
        loadBooks() // Reload all books
    }

    // Sort books and apply the current status filter
    private fun sortBooks(sortOrder: SortOrder) {
        currentSortOrder = sortOrder
        loadBooks() // Reload all books
    }

    fun sortBooksAZ() {
        sortBooks(SortOrder.A_TO_Z)
    }

    fun sortBooksZA() {
        sortBooks(SortOrder.Z_TO_A)
    }

    fun sortBooksRecentlyAdded() {
        sortBooks(SortOrder.RECENTLY_ADDED)
    }

    // Search for books by title or description
    fun searchBooks(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val searchResults = if (query.isBlank()) {
                bookDao.sortBooksRecentlyAdded()
            } else {
                bookDao.searchBooks("%$query%")
            }
            _filteredBooks.postValue(searchResults)
        }
    }
}
