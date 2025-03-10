package com.android.budgetbuddy.ui.budget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.budgetbuddy.R
import com.android.budgetbuddy.data.adapter.BudgetAdapter
import com.android.budgetbuddy.data.model.Budget
import com.android.budgetbuddy.databinding.FragmentBudgetBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class BudgetFragment:Fragment() {

    private lateinit var binding: FragmentBudgetBinding
    private lateinit var budgetAdapter: BudgetAdapter
    private val list = ArrayList<Budget>()
    private val firestore = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private var budgetListener: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        budgetAdapter = BudgetAdapter(list, requireContext())
        binding.rvBudget.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = budgetAdapter
        }

        // Initialize the Firestore listener for budget data
        listenForBudgetUpdates()

        // Set up swipe refresh
        swipeRefresh()
    }

    private fun swipeRefresh() {
        val swipeRefreshLayout: SwipeRefreshLayout = binding.swipeRefresh
        swipeRefreshLayout.setOnRefreshListener {
            // Refresh by re-adding the listener
            listenForBudgetUpdates()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun getDefaultBudgetList(): ArrayList<Budget> {
        return arrayListOf(
            Budget("Makanan", R.drawable.ex_makanan, 0, 0, 0, ""),
            Budget("Transportasi", R.drawable.ex_kendaraan, 0, 0, 0, ""),
            Budget("Elektronik", R.drawable.ic_budget, 0, 0, 0, ""),
            Budget("Tagihan Listrik", R.drawable.ex_electric, 0, 0, 0, ""),
            Budget("Kesehatan", R.drawable.ex_kesehatan, 0, 0, 0, ""),
            Budget("Pendidikan", R.drawable.ex_pendidikan, 0, 0, 0, ""),
            Budget("Shopping", R.drawable.ex_shopping, 0, 0, 0, ""),
            Budget("Tagihan Air", R.drawable.ex_water, 0, 0, 0, ""),
            Budget("Rumah", R.drawable.ex_rumah, 0, 0, 0, ""),
        )
    }


    private fun listenForBudgetUpdates() {

        budgetListener?.remove()

        if (userId.isNotEmpty()) {
            budgetListener = firestore.collection("user")
                .document(userId)
                .collection("budget")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Toast.makeText(context, "Failed to listen for budget updates: ${e.message}", Toast.LENGTH_SHORT).show()
                        return@addSnapshotListener
                    }

                    list.clear() // Clear the existing list

                    if (snapshot == null || snapshot.isEmpty) {
                        // Firestore is empty, use default budgets
                        list.addAll(getDefaultBudgetList())
                    } else {
                        val defaultBudgetList = getDefaultBudgetList()
                        val budgetMap = defaultBudgetList.associateBy { it.title }.toMutableMap()

                        for (document in snapshot.documents) {
                            val title = document.getString("title") ?: continue
                            val iconValue = document.getString("icon") ?: ""
                            val isUrl = iconValue.startsWith("http")

                            val budget = Budget(
                                title = title,
                                image = if (isUrl) iconValue else "res://drawable/${
                                    resources.getIdentifier(iconValue, "drawable", requireContext().packageName)
                                }",
                                limit = document.getLong("limit")?.toInt() ?: 0,
                                pengeluaran = document.getLong("pengeluaran")?.toInt() ?: 0,
                                sisa = document.getLong("sisa")?.toInt() ?: 0,
                                tanggal = document.getString("tanggal") ?: ""
                            )

                            budgetMap[title] = budget
                        }

                        list.addAll(budgetMap.values)

                        // Sort list to move items with a budget limit to the top
                        list.sortWith(compareByDescending { it.limit > 0 })
                    }

                    budgetAdapter.notifyDataSetChanged()
                }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Remove the listener when the view is destroyed
        budgetListener?.remove()
    }

}