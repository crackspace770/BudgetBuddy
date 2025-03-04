package com.android.budgetbuddy.ui.budget

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.budgetbuddy.R
import com.android.budgetbuddy.databinding.ActivityAddBudgetBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BudgetLimitActivity:AppCompatActivity() {

    private lateinit var binding: ActivityAddBudgetBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private val calendar = Calendar.getInstance()

    private var budgetIconResId: Int = -1
    private var budgetIconUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBudgetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve data from the intent
        val budgetTitle = intent.getStringExtra("BUDGET_TITLE") ?: "Unknown"
        val budgetAmount = intent.getIntExtra("BUDGET_LIMIT", 0)
        budgetIconResId = intent.getIntExtra("BUDGET_ICON_RESOURCE_ID", -1)
        budgetIconUrl = intent.getStringExtra("BUDGET_ICON_URL")

        // Set the title and amount
        binding.tvkategoriAdd.text = budgetTitle
        binding.edtLimit.setText(budgetAmount.toString())

        // Load the icon based on whether it's a URL or resource ID
        if (budgetIconResId != -1) {
            // If a valid drawable resource ID is provided, use it
            binding.icon.setImageResource(budgetIconResId)
        } else if (!budgetIconUrl.isNullOrEmpty()) {
            // If a URL is provided, load it with Glide
            Glide.with(this)
                .load(budgetIconUrl)
                .placeholder(R.drawable.placeholder) // Show a placeholder while loading
                .error(R.drawable.broken_image)      // Show a fallback if load fails
                .into(binding.icon)
        } else {
            // Default icon if none is provided
            binding.icon.setImageResource(R.drawable.placeholder)
        }

        // Initialize date text and month navigation
        updateDateText()
        binding.arrowLeft.setOnClickListener { changeMonth(-1) }
        binding.arrowRight.setOnClickListener { changeMonth(1) }

        // Set up save button click listener
        binding.btnSimpanLimit.setOnClickListener {
            simpanData(budgetTitle)
        }

        // Set up back button listener
        binding.btnBackArrow.setOnClickListener {
            finish()
        }
    }

    private fun updateDateText() {
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        binding.tvTanggalAdd.text = dateFormat.format(calendar.time)
    }

    private fun changeMonth(amount: Int) {
        calendar.add(Calendar.MONTH, amount)
        updateDateText()
    }

    private fun simpanData(budgetTitle: String) {
        // Get limit input value
        val limitBudgetText = binding.edtLimit.text.toString()
        val limitBudget = if (limitBudgetText.isNotEmpty()) limitBudgetText.toInt() else 0

        // Ensure userId is valid before attempting to save
        if (userId.isNotEmpty()) {
            val budgetRef = firestore.collection("user")
                .document(userId)
                .collection("budget")
                .document(budgetTitle)

            // Determine which icon information to save: URL or drawable resource name
            val iconToSave = if (!budgetIconUrl.isNullOrEmpty()) {
                budgetIconUrl // Save URL directly if it's a URL
            } else {
                // Save drawable resource name if it's a local resource
                resources.getResourceEntryName(budgetIconResId)
            }

            // Prepare data for saving to Firestore
            val budgetData = mapOf(
                "title" to budgetTitle,
                "icon" to iconToSave,
                "limit" to limitBudget,
                "tanggal" to binding.tvTanggalAdd.text.toString()  // Store formatted date
            )

            // Save to Firestore
            budgetRef.set(budgetData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Budget saved successfully!", Toast.LENGTH_SHORT).show()
                    finish()  // Close the activity after saving
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to save budget: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User ID not found. Please log in.", Toast.LENGTH_SHORT).show()
        }
    }

}