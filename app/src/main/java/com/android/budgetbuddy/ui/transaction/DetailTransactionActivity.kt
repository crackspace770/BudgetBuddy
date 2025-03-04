package com.android.budgetbuddy.ui.transaction

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.budgetbuddy.R
import com.android.budgetbuddy.data.model.CategoryItem
import com.android.budgetbuddy.data.model.Transaction
import com.android.budgetbuddy.databinding.ActivityTransactionDetailBinding
import com.android.budgetbuddy.ui.category.CategorySelectionFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailTransactionActivity: AppCompatActivity(), CategorySelectionFragment.OnCategorySelectedListener {

    private lateinit var binding: ActivityTransactionDetailBinding
    private var etCategory: AutoCompleteTextView? = null
    private var selectedCategoryIconUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setDetailData()

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.deleteData.setOnClickListener {
            openDeleteDialog()
        }

        binding.updateData.setOnClickListener {
            openUpdateDialog()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setDetailData() {
        val transaction = intent.getParcelableExtra<Transaction>(EXTRA_TRANSACTION)

        if (transaction == null) {
            Toast.makeText(this, "Transaction data not found", Toast.LENGTH_SHORT).show()
            return
        }

        binding.apply {
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val convertedDate = transaction.date?.let { Date(it) }
            val amount = transaction.amount.toString()
            val convertedAmount = BigDecimal(amount).toPlainString()

            tvTitleDetails.text = transaction.title
            tvAmountDetails.text = "Rp. $convertedAmount"
            tvDateDetails.text = simpleDateFormat.format(convertedDate)
            tvCategoryDetails.text = transaction.category
            tvNoteDetails.text = transaction.note

            val type = transaction.type
            if (type == 1) {
                tvTypeDetails.text = "Transaksi Pengeluaran"
                tvAmountDetails.setTextColor(Color.parseColor("#16423C"))
                transactionDetailsTitle.setBackgroundResource(R.color.hijauu)
            } else {
                tvTypeDetails.text = "Transaksi Pemasukan"
                tvAmountDetails.setTextColor(Color.parseColor("#3F6A64"))
                transactionDetailsTitle.setBackgroundResource(R.color.hijautuaa)
                window.statusBarColor = ContextCompat.getColor(this@DetailTransactionActivity, R.color.hijautuaa)
            }
        }
    }

    private fun openDeleteDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_delete_transaction, null)
        val dialogBuilder = AlertDialog.Builder(this).setView(dialogView)
        val dialog = dialogBuilder.create()

        val transaction = intent.getParcelableExtra<Transaction>(EXTRA_TRANSACTION)

        if (transaction == null) {
            Toast.makeText(this, "Transaction data not found", Toast.LENGTH_SHORT).show()
            return
        }

        val transactionId = transaction.transactionID
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (transactionId == null || userId == null) {
            Toast.makeText(this, "Transaction or User ID is missing.", Toast.LENGTH_SHORT).show()
            return
        }

        // Reference to the collection to query for the specific transaction document
        val userRef = FirebaseFirestore.getInstance()
            .collection("user")
            .document(userId)
            .collection("transaction")
            .whereEqualTo("transactionID", transactionId)

        dialogView.findViewById<Button>(R.id.btnCancelDelete).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnDelete).setOnClickListener {
            userRef.get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        // Retrieve the document to delete
                        val transactionDoc = querySnapshot.documents[0]
                        val transactionRef = transactionDoc.reference

                        // Delete the document
                        transactionRef.delete()
                            .addOnSuccessListener {
                                Toast.makeText(this, "Transaction deleted successfully.", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                                recreate()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Failed to delete transaction: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Transaction not found in Firestore.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error checking document existence: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        dialog.show()
    }


    override fun onCategorySelected(categoryItem: CategoryItem) {
        etCategory?.setText(categoryItem.name)
        selectedCategoryIconUrl = categoryItem.icon.toString()
    }

    private fun openUpdateDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_update_transaction, null)
        val dialogBuilder = AlertDialog.Builder(this).setView(dialogView)
        val dialog = dialogBuilder.create()

        val transaction = intent.getParcelableExtra<Transaction>(EXTRA_TRANSACTION)
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val transactionId = transaction?.transactionID

        if (userId == null || transaction == null || transactionId == null) {
            Toast.makeText(this, "Transaction, User ID, or Unique ID is missing.", Toast.LENGTH_SHORT).show()
            return
        }

        val etTitle = dialogView.findViewById<EditText>(R.id.etTitle)
        val etAmount = dialogView.findViewById<EditText>(R.id.etAmount)
        etCategory = dialogView.findViewById(R.id.etCategory)
        val etNote = dialogView.findViewById<EditText>(R.id.etNote)

        // Pre-fill fields with transaction data
        etTitle.setText(transaction.title)
        etAmount.setText(transaction.amount.toString())
        etCategory?.setText(transaction.category)
        etNote.setText(transaction.note)

        etCategory?.setOnClickListener {
            val categoryType = if (transaction?.type == 1) 1 else 2
            val fragment = CategorySelectionFragment().apply {
                arguments = Bundle().apply {
                    putInt("categoryType", categoryType)
                }
            }
            fragment.show(supportFragmentManager, "CategorySelectFragment")
        }

        dialogView.findViewById<Button>(R.id.btnUpdate).setOnClickListener {
            val updatedTitle = etTitle.text.toString().trim()
            val updatedAmount = etAmount.text.toString().trim().toDoubleOrNull()
            val updatedCategory = etCategory?.text.toString().trim()
            val updatedNote = etNote.text.toString().trim()
            val updatedIconUrl = selectedCategoryIconUrl ?: transaction.icon

            if (updatedTitle.isEmpty() || updatedAmount == null || updatedCategory.isEmpty()) {
                Toast.makeText(this, "Please fill in all the fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userRef = FirebaseFirestore.getInstance()
                .collection("user")
                .document(userId)
                .collection("transaction")

            // Query to find the document with the matching transactionID field
            userRef.whereEqualTo("transactionID", transactionId).get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val transactionDoc = querySnapshot.documents[0]
                        val transactionRef = userRef.document(transactionDoc.id)

                        val updatedFields = hashMapOf<String, Any>(
                            "title" to updatedTitle,
                            "amount" to updatedAmount,
                            "category" to updatedCategory,
                            "note" to updatedNote,
                            "icon" to (updatedIconUrl ?: "")
                        )

                        Log.d("DetailInputActivity", "Updating transaction with ID: ${transaction.transactionID}")

                        transactionRef.update(updatedFields)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Transaction updated successfully.", Toast.LENGTH_SHORT).show()
                                setDetailData()
                                dialog.dismiss()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Failed to update transaction: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Transaction not found in Firestore.", Toast.LENGTH_SHORT).show()
                        Log.d("DetailInputActivity", "Document does not exist for transactionID: $transactionId")
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error querying Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }




    companion object {
        const val EXTRA_TRANSACTION = "extra_transaction"
    }

}