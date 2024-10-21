package com.example.booktracker

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface BookDao {

    // Create
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBook(book: Book)

    // Read
    @Query("SELECT * FROM books")
    fun getAllBooks(): LiveData<List<Book>>

    @Query("SELECT * FROM books ORDER BY title COLLATE NOCASE ASC")
    fun sortBooksAscending(): List<Book>

    @Query("SELECT * FROM books ORDER BY title COLLATE NOCASE DESC")
    fun sortBooksDescending(): List<Book>

    @Query("SELECT * FROM books ORDER BY id DESC")
    fun sortBooksRecentlyAdded(): List<Book>

    @Query("SELECT * FROM books WHERE status = :status ORDER BY title ASC")
    fun getBooksByStatusSortedByTitle(status: String): List<Book>

    @Query("SELECT * FROM books WHERE status = :status ORDER BY title DESC")
    fun getBooksByStatusSortedByTitleDescending(status: String): List<Book>

    @Query("SELECT * FROM books WHERE status = :status ORDER BY id DESC")
    fun getBooksByStatusSortedByRecentlyAdded(status: String): List<Book>

     @Query("SELECT * FROM books WHERE title LIKE :query OR author LIKE :query")
    fun searchBooks(query: String): List<Book>

    //get by id
    @Query("SELECT * FROM books WHERE id = :bookId")
    fun getBookById(bookId: Int): Book

    // Update
    @Update
    suspend fun updateBook(book: Book)

    // Delete
    @Delete
    suspend fun deleteBook(book: Book)
}