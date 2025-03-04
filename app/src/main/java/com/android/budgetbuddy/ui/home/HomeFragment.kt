package com.android.budgetbuddy.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.budgetbuddy.R
import com.android.budgetbuddy.data.adapter.TransactionAdapter
import com.android.budgetbuddy.data.model.Transaction
import com.android.budgetbuddy.databinding.FragmentHomeBinding
import com.android.budgetbuddy.ui.transaction.TransactionActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import java.util.Calendar

class HomeFragment:Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private var database: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var auth: FirebaseAuth = Firebase.auth
    private val transactionAdapter = TransactionAdapter()

    private lateinit var typeOption: Spinner
    private lateinit var timeSpanOption: Spinner
    private var selectedType: String = "Jenis"
    private var selectedTimeSpan: String = "Waktu"
    var dateStart: Long = 0
    var dateEnd: Long = 0
    private var transactionListener: ListenerRegistration? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getFullTransaction()
        setupRecyclerView()
        visibilityOptions()
        loadTransactionData()
        swipeRefresh()

    }

    private fun getFullTransaction(){

        binding.viewMore.setOnClickListener {
            val intent = Intent(requireContext(), TransactionActivity::class.java)
            startActivity(intent)
        }
    }

    private fun swipeRefresh() {
        val swipeRefreshLayout: SwipeRefreshLayout = binding.swipeRefresh
        swipeRefreshLayout.setOnRefreshListener {
            setupRecyclerView()
            visibilityOptions()
            loadTransactionData()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setupRecyclerView() {
        binding.rvTransaction.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = transactionAdapter
        }
    }

    private fun visibilityOptions() {
        // Update typeList to include "All"
        typeOption = requireView().findViewById(R.id.typeSpinner) as Spinner
        val typeList = arrayOf("All", "Pengeluaran", "Pemasukan")
        val typeSpinnerAdapter = ArrayAdapter<String>(
            this.requireActivity(), R.layout.selected_spinner, typeList
        )
        typeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1)
        typeOption.adapter = typeSpinnerAdapter

        // Update timeSpanList to include "All"
        timeSpanOption = requireView().findViewById(R.id.timeSpanSpinner) as Spinner
        val timeSpanList = arrayOf("All", "Bulan ini", "Minggu ini", "Hari ini")
        val timeSpanAdapter = ArrayAdapter<String>(
            this.requireActivity(), R.layout.selected_spinner, timeSpanList
        )
        timeSpanAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1)
        timeSpanOption.adapter = timeSpanAdapter

        // Type selection logic
        typeOption.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                when (typeList[p2]) {
                    "All" -> selectedType = "All"  // No filter for type
                    "Pengeluaran" -> selectedType = "Pengeluaran"
                    "Pemasukan" -> selectedType = "Pemasukan"
                }
                loadTransactionData()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // No action needed
            }
        }

        // Time span selection logic
        timeSpanOption.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                when (timeSpanList[p2]) {
                    "All" -> selectedTimeSpan = "All"  // No filter for time
                    "Bulan ini" -> {
                        selectedTimeSpan = "Bulan ini"
                        getRangeDate(Calendar.DAY_OF_MONTH)
                    }
                    "Minggu ini" -> {
                        selectedTimeSpan = "Minggu ini"
                        getRangeDate(Calendar.DAY_OF_WEEK)
                    }
                    "Hari ini" -> {
                        selectedTimeSpan = "Hari ini"
                        dateStart = System.currentTimeMillis()
                        dateEnd = System.currentTimeMillis()
                    }
                }
                loadTransactionData()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // No action needed
            }
        }
    }

    private fun getRangeDate(rangeType: Int) {
        val cal = Calendar.getInstance()

        when (rangeType) {
            Calendar.DAY_OF_MONTH -> {
                // Set to start of the current month
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                dateStart = cal.timeInMillis

                // Set to end of the current month
                cal.add(Calendar.MONTH, 1)
                cal.set(Calendar.DAY_OF_MONTH, 0) // Last day of the month
                dateEnd = cal.timeInMillis
            }
            Calendar.WEEK_OF_YEAR -> {
                // Set to start of the current week
                cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                dateStart = cal.timeInMillis

                // Set to end of the current week
                cal.add(Calendar.WEEK_OF_YEAR, 1)
                cal.add(Calendar.MILLISECOND, -1)
                dateEnd = cal.timeInMillis
            }
            Calendar.DAY_OF_YEAR -> {
                // Set to start of the current day
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                dateStart = cal.timeInMillis

                // Set to end of the current day
                cal.add(Calendar.DAY_OF_YEAR, 1)
                cal.add(Calendar.MILLISECOND, -1)
                dateEnd = cal.timeInMillis
            }
        }
    }

    private fun loadTransactionData() {

        transactionListener?.remove() // Remove any existing listener

        val userId = auth.currentUser?.uid
        if (userId != null) {
            val transactionRef = database
                .collection("user")
                .document(userId)
                .collection("transaction")
                .limit(5) // Fetch only 5 transactions


            var query: Query = transactionRef

            if (selectedType != "All") {
                val typeValue = if (selectedType == "Pengeluaran") 1 else 2
                query = query.whereEqualTo("type", typeValue)
            }

            if (selectedTimeSpan != "All") {
                // Ensure dateStart and dateEnd reflect inverted timestamps
                val invertedDateStart = -dateEnd  // Invert start
                val invertedDateEnd = -dateStart  // Invert end
                query = query.whereGreaterThanOrEqualTo("invertedDate", invertedDateStart)
                    .whereLessThanOrEqualTo("invertedDate", invertedDateEnd)
            }


            transactionListener = query.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("HomeFragment", "Error fetching transactions", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val transactionList = snapshot.documents.mapNotNull { it.toObject(Transaction::class.java) }

                    // Limit to only 5 items
                    val limitedTransactions = transactionList.take(5)

                    transactionAdapter.submitList(limitedTransactions)

                    binding.viewEmpty.root.visibility = if (limitedTransactions.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        } else {
            Toast.makeText(requireContext(), "User not logged in.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Remove the listener when the view is destroyed
        transactionListener?.remove()
    }

}