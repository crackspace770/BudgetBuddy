package com.android.budgetbuddy.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.android.budgetbuddy.data.adapter.CategoryAdapter
import com.android.budgetbuddy.data.model.AddCategoryOption
import com.android.budgetbuddy.data.model.CategoryItem
import com.android.budgetbuddy.databinding.FragmentAddCategoryBinding

class AddCategoryDialogFragment : DialogFragment() {

    interface OnCategoryAddedListener {
        fun onCategoryAdded(categoryItem: CategoryItem)
    }

    private var onCategoryAddedListener: OnCategoryAddedListener? = null
    private lateinit var binding: FragmentAddCategoryBinding
    private var selectedCategory: CategoryItem? = null // Global selected category item

    // Hold references to all adapters
    private lateinit var foodCategoryAdapter: CategoryAdapter
    private lateinit var drinkCategoryAdapter: CategoryAdapter
    private lateinit var pakaianCategoryAdapter: CategoryAdapter
    private lateinit var rumahCategoryAdapter: CategoryAdapter
    private lateinit var kendaraanCategoryAdapter: CategoryAdapter
    private lateinit var keuanganCategoryAdapter: CategoryAdapter
    private lateinit var lainyaCategoryAdapter: CategoryAdapter

    fun setOnCategoryAddedListener(listener: OnCategoryAddedListener) {
        onCategoryAddedListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategoryRV()

        binding.saveButton.setOnClickListener {
            val inputCategoryName = binding.edtInputName.text.toString().trim()

            if (inputCategoryName.isEmpty()) {
                Toast.makeText(context, "Masukkan Nama Kategori", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            selectedCategory?.let { category ->
                val newCategoryItem = CategoryItem(
                    name = inputCategoryName,
                    icon = category.icon,
                    categoryType = arguments?.getInt("categoryType") ?: 1 // Pass category type
                )
                onCategoryAddedListener?.onCategoryAdded(newCategoryItem)
                dismiss()
            } ?: Toast.makeText(context, "Pilih Ikon Terlebih Dahulu", Toast.LENGTH_SHORT).show()
        }


        binding.btnback.setOnClickListener {
            dismiss()
        }

    }

    private fun setupCategoryRV() {
        // Initialize adapters for each category, passing a callback for when an item is selected
        foodCategoryAdapter = createCategoryAdapter(AddCategoryOption.makananCategory())
        drinkCategoryAdapter = createCategoryAdapter(AddCategoryOption.minumanCategory())
        pakaianCategoryAdapter = createCategoryAdapter(AddCategoryOption.pakaianCategory())
        rumahCategoryAdapter = createCategoryAdapter(AddCategoryOption.rumahCategory())
        kendaraanCategoryAdapter = createCategoryAdapter(AddCategoryOption.kendaraanCategory())
        keuanganCategoryAdapter = createCategoryAdapter(AddCategoryOption.keuanganCategory())
        lainyaCategoryAdapter = createCategoryAdapter(AddCategoryOption.lainyaCategory())

        // Set adapters for RecyclerViews
        binding.rvMakanan.apply {
            layoutManager = GridLayoutManager(context, 5)
            adapter = foodCategoryAdapter
        }
        binding.rvMinuman.apply {
            layoutManager = GridLayoutManager(context, 5)
            adapter = drinkCategoryAdapter
        }
        binding.rvPakaian.apply {
            layoutManager = GridLayoutManager(context, 5)
            adapter = pakaianCategoryAdapter
        }
        binding.rvRumah.apply {
            layoutManager = GridLayoutManager(context, 5)
            adapter = rumahCategoryAdapter
        }
        binding.rvKendaraan.apply {
            layoutManager = GridLayoutManager(context, 5)
            adapter = kendaraanCategoryAdapter
        }
        binding.rvKeuangan.apply {
            layoutManager = GridLayoutManager(context, 5)
            adapter = keuanganCategoryAdapter
        }
        binding.rvLainya.apply {
            layoutManager = GridLayoutManager(context, 5)
            adapter = lainyaCategoryAdapter
        }
    }

    // Helper to create adapters with a selection callback
    private fun createCategoryAdapter(categoryItems: List<CategoryItem>): CategoryAdapter {
        return CategoryAdapter(requireContext(), categoryItems.toMutableList(),
            onCategorySelected = { categoryItem ->
                // Update selected category and refresh all adapters
                selectedCategory = categoryItem
                updateInputNameDrawable() // Update drawable for selected icon
                refreshAdapters()
            },
            onAddCategoryClicked = {},
            showAddIcon = false,
            showItemName = false
        )
    }

    // Update drawableStart of edtInputName based on selected icon
    private fun updateInputNameDrawable() {
        selectedCategory?.icon?.let { iconResId ->
            binding.edtInputName.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0)
        }
    }


    private fun refreshAdapters() {
        foodCategoryAdapter.setSelectedCategory(selectedCategory)
        drinkCategoryAdapter.setSelectedCategory(selectedCategory)
        pakaianCategoryAdapter.setSelectedCategory(selectedCategory)
        rumahCategoryAdapter.setSelectedCategory(selectedCategory)
        kendaraanCategoryAdapter.setSelectedCategory(selectedCategory)
        keuanganCategoryAdapter.setSelectedCategory(selectedCategory)
        lainyaCategoryAdapter.setSelectedCategory(selectedCategory)
    }
}
