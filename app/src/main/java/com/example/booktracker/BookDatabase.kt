package com.example.booktracker

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Define the version of the database
@Database(entities = [Book::class], version = 2, exportSchema = false)
abstract class BookDatabase : RoomDatabase() {

    // Abstract function to get the DAO
    abstract fun bookDao(): BookDao

    companion object {
        @Volatile
        private var INSTANCE: BookDatabase? = null // Holds the single instance of the database

        private const val TAG = "BookDatabase" // Logging tag for debugging

        // Function to get the database instance
        fun getDatabase(context: Context): BookDatabase {
            // Return existing instance if available
            return INSTANCE ?: synchronized(this) { // Synchronize to avoid multiple instances
                Log.d(TAG, "Creating new instance of BookDatabase")
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BookDatabase::class.java,
                    "book_database" // Name of the database
                )
                .fallbackToDestructiveMigration() // Allow destructive migration on version change
                .build() // Build the database instance
                INSTANCE = instance // Cache the instance for future use
                instance // Return the created instance
            }
        }
    }
}
