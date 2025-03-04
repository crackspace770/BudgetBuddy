package com.android.budgetbuddy.ui.transaction

import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.android.budgetbuddy.R
import com.android.budgetbuddy.data.model.CategoryItem
import com.android.budgetbuddy.data.model.Transaction
import com.android.budgetbuddy.databinding.ActivityInputTransactionBinding
import com.android.budgetbuddy.ui.category.CategorySelectionFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class InputTransactionActivity:AppCompatActivity(), CategorySelectionFragment.OnCategorySelectedListener  {

    private lateinit var binding: ActivityInputTransactionBinding
    private lateinit var dbRef: DatabaseReference
    private var auth: FirebaseAuth = Firebase.auth
    private var database: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var etCategory: AutoCompleteTextView
    private var type: Int = 1
    private var amount: Double = 0.0
    private var date: Long = 0
    private var invertedDate: Long = 0
    private var isSubmitted: Boolean = false
    private var selectedCategoryItem: CategoryItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = Firebase.auth.currentUser
        val uid = user?.uid
        if (uid != null) {
            dbRef = FirebaseDatabase.getInstance().getReference(uid)
        }

        etCategory = binding.category // This is the AutoCompleteTextView

        etCategory.setOnClickListener {
            val categoryType = if (type == 1) 1 else 2  // Determine the category type based on selection
            val fragment = CategorySelectionFragment().apply {
                arguments = Bundle().apply {
                    putInt("categoryType", categoryType)
                }
            }
            fragment.show(supportFragmentManager, "CategorySelectFragment")
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.apply {
            typeRadioGroup.setOnCheckedChangeListener { _, checkedID ->
                etCategory.text.clear()

                if (checkedID == rbExpense.id) {
                    type = 1 // Expense
                    rbExpense.setBackgroundResource(R.drawable.radio_selected_expense)
                    rbIncome.setBackgroundResource(R.drawable.radio_not_selected)
                    toolbarLinear.setBackgroundResource(R.color.hijauu)
                    saveButton.backgroundTintList = getColorStateList(R.color.hijauu)
                    window.statusBarColor = ContextCompat.getColor(this@InputTransactionActivity, R.color.hijauu)
                }

                if (checkedID == rbIncome.id) {
                    type = 2 // Income
                    rbIncome.setBackgroundResource(R.drawable.radio_selected_income)
                    rbExpense.setBackgroundResource(R.drawable.radio_not_selected)
                    toolbarLinear.setBackgroundResource(R.color.hijautuaa)
                    saveButton.backgroundTintList = getColorStateList(R.color.hijautuaa)
                    window.statusBarColor = ContextCompat.getColor(this@InputTransactionActivity, R.color.hijautuaa)
                }
            }
        }

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        val currentDate = sdf.parse(sdf.format(System.currentTimeMillis()))
        date = currentDate!!.time

        binding.date.setOnClickListener {
            clickDatePicker()
        }

        binding.saveButton.setOnClickListener {
            if (!isSubmitted) {
                saveTransactionData()
            } else {
                Snackbar.make(findViewById(android.R.id.content), "Transaksi berhasil tersimpan", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onCategorySelected(categoryItem: CategoryItem) {
        selectedCategoryItem = categoryItem  // Store the selected category with its icon
        etCategory.setText(categoryItem.name)

        // Optionally, display the icon in the AutoCompleteTextView
        etCategory.setCompoundDrawablesWithIntrinsicBounds(categoryItem.icon, 0, 0, 0)
    }

    private fun clickDatePicker() {
        val myCalendar = Calendar.getInstance()
        val year = myCalendar.get(Calendar.YEAR)
        val month = myCalendar.get(Calendar.MONTH)
        val day = myCalendar.get(Calendar.DAY_OF_MONTH)

        val etDate = binding.date

        val dpd = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDayOfMonth ->
            val selectedDate = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
            etDate.text = null
            etDate.hint = selectedDate

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val theDate = sdf.parse(selectedDate)
            date = theDate!!.time

        }, year, month, day)
        dpd.show()
    }

    private fun saveTransactionData() {
        val title = binding.title.text.toString()
        val category = etCategory.text.toString()
        val amountEt = binding.amount.text.toString()
        val note = binding.note.text.toString()

        if (amountEt.isEmpty()) {
            binding.amount.error = "Please enter an amount!"
        } else if (title.isEmpty()) {
            binding.title.error = "Please enter a title!"
        } else if (category.isEmpty()) {
            etCategory.error = "Please select a category!"
        } else {
            amount = amountEt.toDouble()
            val transactionID = dbRef.push().key!!

            val userId = auth.currentUser?.uid ?: return
            invertedDate = date * -1

            if (selectedCategoryItem != null) {
                // Upload icon and save transaction
                uploadIcon(selectedCategoryItem!!.icon) { iconUrl ->
                    if (iconUrl != null) {
                        val transaction = Transaction(
                            transactionID = transactionID,
                            type = type,
                            title = title,
                            category = category,
                            amount = amount,
                            date = date,
                            note = note,
                            invertedDate = invertedDate,
                            icon = iconUrl
                        )

                        database.collection("user").document(userId).collection("transaction")
                            .add(transaction)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Transaction saved!", Toast.LENGTH_SHORT).show()
                                Log.d("InputActivity", "Transaction data: $transaction")

                                if(type == 1){
                                    addCategoryToBudgetIfNotExists(category, iconUrl)
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.w("InputActivity", "Error adding document", e)
                                Toast.makeText(this, "Failed to save transaction data.", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Icon upload failed.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Category icon not found.", Toast.LENGTH_SHORT).show()
            }

            isSubmitted = true
            finish()
        }
    }

    // Add this helper function to add a new category to the budget if it doesn't already exist
    private fun addCategoryToBudgetIfNotExists(categoryName: String, iconUrl: String) {
        val userId = auth.currentUser?.uid ?: return
        val budgetRef = database.collection("user").document(userId).collection("budget").document(categoryName)

        budgetRef.get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    val newBudget = hashMapOf(
                        "title" to categoryName,
                        "icon" to iconUrl, // Store URL instead of resource name
                        "limit" to 0,
                        "pengeluaran" to 0,
                        "sisa" to 0,
                        "tanggal" to ""
                    )
                    budgetRef.set(newBudget)
                        .addOnSuccessListener {
                            Log.d("InputActivity", "New budget category added: $categoryName")
                        }
                        .addOnFailureListener { e ->
                            Log.e("InputActivity", "Failed to add new budget category", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("InputActivity", "Failed to check budget category existence", e)
            }
    }


//    private fun getSelectedCategoryItem(): CategoryItem? {
//        val selectedCategoryName = etCategory.text.toString().trim()
//        val categoryList = if (type == 1) CategoryOptions.expenseCategory() else CategoryOptions.incomeCategory()
//
//        return categoryList.find { it.name.equals(selectedCategoryName, ignoreCase = true) }
//    }

    private fun uploadIcon(iconResId: Int, callback: (String?) -> Unit) {
        val drawable = AppCompatResources.getDrawable(this, iconResId)
        if (drawable == null) {
            Log.e("InputActivity", "Drawable resource not found for iconResId: $iconResId")
            callback(null)
            return
        }

        val bitmap = drawableToBitmap(drawable)
        if (bitmap == null) {
            Log.e("InputActivity", "Failed to convert drawable to Bitmap")
            callback(null)
            return
        }

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val data = baos.toByteArray()

        val storageRef = FirebaseStorage.getInstance().reference.child("icons/${UUID.randomUUID()}.png")
        val uploadTask = storageRef.putBytes(data)
        uploadTask.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                callback(uri.toString())
            }.addOnFailureListener {
                callback(null)
            }
        }.addOnFailureListener {
            callback(null)
        }
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap? {
        return if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else {
            val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }


}