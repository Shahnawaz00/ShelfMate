package com.example.booktracker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BookListFragment : Fragment() {

    private lateinit var bookViewModel: BookViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var sortSpinner: Spinner
    private lateinit var filterSpinner: Spinner
    private lateinit var filterSortSection: LinearLayout
    private lateinit var toggleButton: Button
    private var isFilterVisible = false
    private lateinit var adapter: BookAdapter

    private val TAG = "BookListFragment" // Logging tag

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_book_list, container, false)

        // Find views by ID
        recyclerView = view.findViewById(R.id.recyclerView)
        searchView = view.findViewById(R.id.searchView)
        sortSpinner = view.findViewById(R.id.sortSpinner)
        filterSpinner = view.findViewById(R.id.filterSpinner)
        filterSortSection = view.findViewById(R.id.filterSortSection)
        toggleButton = view.findViewById(R.id.toggleButton)

        return view
    }

    override fun onResume() {
        super.onResume()
        bookViewModel.loadBooks() // Load the books when the fragment comes back into view
        Log.d(TAG, "Books loaded in onResume")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel to manage UI-related data
        bookViewModel = ViewModelProvider(requireActivity()).get(BookViewModel::class.java)

        // Initialize RecyclerView and Adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = BookAdapter(
            onDeleteClick = { book ->
                bookViewModel.deleteBook(book) // Delete the selected book
                Log.d(TAG, "Deleted book: ${book.title}")
            },
            onUpdateClick = { book ->
                // Launch EditBookActivity with the book ID passed
                val intent = Intent(requireActivity(), EditBookActivity::class.java).apply {
                    putExtra("BOOK_ID", book.id) // Pass the book ID to the edit activity
                }
                startActivity(intent)
                Log.d(TAG, "Navigated to EditBookActivity for book ID: ${book.id}")
            }
        )
        recyclerView.adapter = adapter

        // Toggle button click listener for showing/hiding filter section
        toggleButton.setOnClickListener {
            isFilterVisible = !isFilterVisible
            filterSortSection.visibility = if (isFilterVisible) {
                toggleButton.text = "Hide Filters"
                View.VISIBLE
            } else {
                toggleButton.text = "Show Filters"
                View.GONE
            }
            Log.d(TAG, "Filter visibility changed: $isFilterVisible")
        }

        // Observe filteredBooks LiveData from ViewModel
        bookViewModel.filteredBooks.observe(viewLifecycleOwner) { books ->
            adapter.submitList(books) // Update the adapter with filtered books
            Log.d(TAG, "Books updated in adapter: $books books")
        }

        // Set up search functionality using SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                bookViewModel.searchBooks(query.orEmpty()) // Search with query
                Log.d(TAG, "Search submitted: $query")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                bookViewModel.searchBooks(newText.orEmpty()) // Search with new text
                Log.d(TAG, "Search text changed: $newText")
                return true
            }
        })

        // Set up sorting options Spinner
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sort_options, // Array resource for sorting options
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            sortSpinner.adapter = adapter
        }

        // Sorting logic using Spinner selection
        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> {
                        bookViewModel.sortBooksAZ() // Sort A-Z
                        Log.d(TAG, "Sorting books A-Z")
                    }
                    1 -> {
                        bookViewModel.sortBooksZA() // Sort Z-A
                        Log.d(TAG, "Sorting books Z-A")
                    }
                    2 -> {
                        bookViewModel.sortBooksRecentlyAdded() // Sort by Recently Added
                        Log.d(TAG, "Sorting books by recently added")
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No action needed when nothing is selected
            }
        }

        // Set up filtering options Spinner
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.filter_options, // Array resource for filtering options
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            filterSpinner.adapter = adapter
        }

        // Filtering logic using Spinner selection
        filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedStatus = parent.getItemAtPosition(position).toString()
                bookViewModel.filterByStatus(selectedStatus) // Apply filter based on status
                Log.d(TAG, "Filtering by status: $selectedStatus")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No action needed when nothing is selected
            }
        }
    }
}
