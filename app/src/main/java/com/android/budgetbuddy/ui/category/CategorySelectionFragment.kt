package com.android.budgetbuddy.ui.category

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.android.budgetbuddy.R
import com.android.budgetbuddy.data.ViewModelFactory
import com.android.budgetbuddy.data.adapter.CategoryAdapter
import com.android.budgetbuddy.data.model.CategoryItem
import com.android.budgetbuddy.databinding.FragmentCategorySelectionBinding

class CategorySelectionFragment : DialogFragment() {

    interface OnCategorySelectedListener {
        fun onCategorySelected(categoryItem: CategoryItem)
    }

    private var categorySelectedListener: OnCategorySelectedListener? = null
    private lateinit var binding: FragmentCategorySelectionBinding
    private val categoryViewModel: CategoryViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }


    private var categories: MutableList<CategoryItem> = mutableListOf()
    private var selectedCategory: CategoryItem? = null
    private lateinit var categoryAdapter: CategoryAdapter
    private var categoryType: Int = 1 // Default to expense

    override fun onAttach(context: Context) {
        super.onAttach(context)
        categorySelectedListener = context as? OnCategorySelectedListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategorySelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryType = arguments?.getInt("categoryType") ?: 1 // Get category type

//        val categoryDao = CategoryDatabase.getDatabase(requireContext()).categoryDao()
//        val factory = CategoryViewModelFactory(categoryDao)
//        categoryViewModel = ViewModelProvider(this, factory)[CategoryViewModel::class.java]

        updateTitleBasedOnCategoryType()

        // Load categories (prepopulation happens in ViewModel)
        categoryViewModel.loadCategories(categoryType)

        // Set up RecyclerView and Adapter
        categoryAdapter = CategoryAdapter(
            requireContext(),
            categories,
            onCategorySelected = { selectedCategory ->
                this.selectedCategory = selectedCategory
            },
            onAddCategoryClicked = {
                showAddCategoryDialog()
            },
            showAddIcon = true,
            showItemName = true
        )

        binding.categoryRecyclerView.layoutManager = GridLayoutManager(context, 3)
        binding.categoryRecyclerView.adapter = categoryAdapter

        // Observe categories from ViewModel
        categoryViewModel.categories.observe(viewLifecycleOwner) { dbCategories ->
            categories.clear()
            categories.addAll(dbCategories)
            categoryAdapter.notifyDataSetChanged()
        }

        binding.saveButton.setOnClickListener {
            selectedCategory?.let {
                categorySelectedListener?.onCategorySelected(it)
            }
            dismiss()
        }

        binding.btnback.setOnClickListener {
            dismiss()
        }
    }


    private fun updateTitleBasedOnCategoryType() {
        val titleCategoryExpense = getString(R.string.title_expense)
        val titleCategoryIncome = getString(R.string.title_income)

        binding.tvTitleKategori.text = if (categoryType == 1) titleCategoryExpense else titleCategoryIncome
    }

    private fun showAddCategoryDialog() {
        val dialog = AddCategoryDialogFragment()
        dialog.setOnCategoryAddedListener(object : AddCategoryDialogFragment.OnCategoryAddedListener {
            override fun onCategoryAdded(newCategory: CategoryItem) {
                newCategory.copy(categoryType = categoryType).let { categorizedItem ->
                    categoryViewModel.addCategory(categorizedItem)
                }
            }
        })
        dialog.show(parentFragmentManager, "AddCategoryDialog")
    }

    override fun onDetach() {
        super.onDetach()
        categorySelectedListener = null
    }
}




