package com.android.budgetbuddy.data.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.budgetbuddy.R
import com.android.budgetbuddy.data.model.CategoryItem


class CategoryAdapter(
    private val context: Context,
    private val categories: MutableList<CategoryItem>,
    private val onCategorySelected: (CategoryItem?) -> Unit,
    private val onAddCategoryClicked: () -> Unit,
    private val showAddIcon: Boolean,
    private val showItemName: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var selectedCategory: CategoryItem? = null

    inner class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iconView: ImageView = view.findViewById(R.id.icon)
        val nameView: TextView = view.findViewById(R.id.name)
        private val itemLayout: View = view

        init {
            view.setOnClickListener {
                val category = categories[adapterPosition]
                onCategorySelected(category)
                setSelectedCategory(category)
            }
        }

        fun bind(category: CategoryItem, isSelected: Boolean) {
            iconView.setImageResource(category.icon)
            nameView.text = category.name
            nameView.visibility = if (showItemName) View.VISIBLE else View.GONE

            // Highlight selected item
            itemLayout.setBackgroundColor(
                ContextCompat.getColor(context, if (isSelected) R.color.grey else android.R.color.transparent)
            )
        }
    }

    inner class AddCategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            view.setOnClickListener { onAddCategoryClicked() }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < categories.size) VIEW_TYPE_CATEGORY else VIEW_TYPE_ADD_ICON
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_CATEGORY) {
            val view = LayoutInflater.from(context).inflate(R.layout.category_item, parent, false)
            CategoryViewHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.add_category_item, parent, false)
            AddCategoryViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CategoryViewHolder) {
            val category = categories[position]
            val isSelected = category == selectedCategory
            holder.bind(category, isSelected)
        }
    }

    override fun getItemCount(): Int = categories.size + if (showAddIcon) 1 else 0

    // Update selected category and refresh list
    fun setSelectedCategory(category: CategoryItem?) {
        selectedCategory = category
        notifyDataSetChanged()
    }

    companion object {
        private const val VIEW_TYPE_CATEGORY = 0
        private const val VIEW_TYPE_ADD_ICON = 1
    }

}
