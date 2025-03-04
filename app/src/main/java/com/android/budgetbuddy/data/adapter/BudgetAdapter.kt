package com.android.budgetbuddy.data.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.android.budgetbuddy.R
import com.android.budgetbuddy.data.model.Budget
import com.android.budgetbuddy.ui.budget.BudgetLimitActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BudgetAdapter(
    private val listBudget: ArrayList<Budget>,
    private val context: Context
) : RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>() {

    private val database: FirebaseFirestore = Firebase.firestore
    private val auth: FirebaseAuth = Firebase.auth

    override fun getItemViewType(position: Int): Int {
        return if (listBudget[position].limit > 0) {
            VIEW_TYPE_WITH_BUDGET
        } else {
            VIEW_TYPE_SET_BUDGET
        }
    }

    class BudgetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgIcon: ImageView = itemView.findViewById(R.id.img_item_photo)
        val tvTitle: TextView = itemView.findViewById(R.id.tv_item_name)
        val tvLimit: TextView? = itemView.findViewById(R.id.tv_limit)
        val tvPengeluaran: TextView? = itemView.findViewById(R.id.tv_pengeluaran)
        val tvSisa: TextView? = itemView.findViewById(R.id.tv_sisa)
        val tvTanggal: TextView? = itemView.findViewById(R.id.tv_tanggal)
        val btnSetBudget: Button? = itemView.findViewById(R.id.buttonBudget)
        val progressBudgetUsage: ProgressBar? = itemView.findViewById(R.id.progress_budget_usage)
        val tvSisaBubble: TextView? = itemView.findViewById(R.id.tvSisaBubble)
        val btnDelete : ImageView? = itemView.findViewById(R.id.btnDeleteBudget)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val view = if (viewType == VIEW_TYPE_WITH_BUDGET) {
            LayoutInflater.from(parent.context).inflate(R.layout.item_budget_with_data, parent, false)
        } else {
            LayoutInflater.from(parent.context).inflate(R.layout.item_budget_set, parent, false)
        }
        return BudgetViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        val budget = listBudget[position]

        if (budget.image.startsWith("http")) {
            // Load URL using Glide
            Glide.with(context)
                .load(budget.image)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.broken_image)
                .into(holder.imgIcon)
        } else if (budget.image.startsWith("res://drawable/")) {
            // Load drawable resource
            val resourceId = budget.image.substringAfterLast("/").toIntOrNull()
            if (resourceId != null && resourceId != 0) {
                holder.imgIcon.setImageResource(resourceId)
            } else {
                holder.imgIcon.setImageResource(R.drawable.broken_image)
            }
        } else {
            // Default to broken image if format is incorrect
            holder.imgIcon.setImageResource(R.drawable.broken_image)
        }

        holder.tvTitle.text = budget.title
        if (getItemViewType(position) == VIEW_TYPE_WITH_BUDGET) {
            holder.tvLimit?.text = "Limit: ${budget.limit}"
            holder.tvPengeluaran?.text = "Pengeluaran: Loading..."

            fetchCategoryTransactionData(budget.title) { totalPengeluaran ->
                holder.tvPengeluaran?.text = "Pengeluaran: $totalPengeluaran"
                val sisa = budget.limit - totalPengeluaran
                holder.tvSisa?.text = "Sisa: $sisa"
                holder.tvSisaBubble?.text = "Rp. $sisa"

                val usagePercentage = if (budget.limit > 0) {
                    ((totalPengeluaran.toFloat() / budget.limit.toFloat()) * 100).toInt().coerceAtMost(100)
                } else 0
                holder.progressBudgetUsage?.progress = usagePercentage
            }
            holder.tvTanggal?.text = budget.tanggal

            holder.btnDelete?.setOnClickListener {
                showDeleteConfirmationDialog(budget)
            }
        } else {
            holder.btnSetBudget?.setOnClickListener {
                startAddBudgetActivity(budget)
            }
        }

        holder.itemView.setOnClickListener {
            startAddBudgetActivity(budget)
        }
    }


    private fun showDeleteConfirmationDialog(budget: Budget) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Reset Budget")
            .setMessage("Ingin Reset Budget?")
            .setPositiveButton("Ya") { dialog, _ ->

                deleteBudgetFields(budget)
                dialog.dismiss()

            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteBudgetFields(budget: Budget) {
        val userId = auth.currentUser?.uid ?: return
        val budgetRef = database.collection("user").document(userId).collection("budget").document(budget.title)


        val updates = hashMapOf<String, Any?>("limit" to null, "tanggal" to null)
        budgetRef.update(updates)
            .addOnSuccessListener {
                Toast.makeText(context, "Budget reset successfully.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to reset budget: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchCategoryTransactionData(category: String, callback: (Int) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        database.collection("user").document(userId)
            .collection("transaction")
            .whereEqualTo("category", category)
            .get()
            .addOnSuccessListener { documents ->
                var totalAmount = 0
                for (document in documents) {
                    val amount = document.getLong("amount")?.toInt() ?: 0
                    totalAmount += amount
                }
                callback(totalAmount) // Pass the calculated amount to the callback
            }
            .addOnFailureListener {
                callback(0) // If there's an error, return 0 as a fallback
            }
    }

    private fun startAddBudgetActivity(budget: Budget) {
        val intent = Intent(context, BudgetLimitActivity::class.java).apply {
            putExtra("BUDGET_TITLE", budget.title)
            putExtra("BUDGET_LIMIT", budget.limit)

            if (budget.image.startsWith("http")) {
                putExtra("BUDGET_ICON_URL", budget.image)
            } else {
                val resourceId = budget.image.substringAfterLast("/").toIntOrNull() ?: R.drawable.placeholder
                putExtra("BUDGET_ICON_RESOURCE_ID", resourceId)
            }
        }
        context.startActivity(intent)
    }


    override fun getItemCount(): Int = listBudget.size

    companion object {
        private const val VIEW_TYPE_SET_BUDGET = 1
        private const val VIEW_TYPE_WITH_BUDGET = 2
    }
}