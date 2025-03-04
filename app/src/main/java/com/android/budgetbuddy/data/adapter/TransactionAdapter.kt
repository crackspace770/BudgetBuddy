package com.android.budgetbuddy.data.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.budgetbuddy.data.model.Transaction
import com.android.budgetbuddy.databinding.ItemTransactionBinding
import com.android.budgetbuddy.ui.transaction.DetailTransactionActivity
import com.bumptech.glide.Glide
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionAdapter: ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(DiffCallback) {

    class TransactionViewHolder(private val binding: ItemTransactionBinding): RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(transaction: Transaction){
            binding.apply {

                val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                val result = Date(transaction.date!!)

                val amount = transaction.amount.toString()
                val convertedAmount = BigDecimal(amount).toPlainString()

                Glide.with(itemView.context)
                    .load(transaction.icon)
                    .centerCrop()
                    .into(imgItemPhoto)

                tvTanggal.text = simpleDateFormat.format(result)
                tvAmount.text = "Rp. ${convertedAmount}"
                tvJudul.text = transaction.title
                tvKategori.text = transaction.category

                if (transaction.type == 1){
                    binding.tvAmount.setTextColor(Color.parseColor("#16423C"))
                }else{
                    binding.tvAmount.setTextColor(Color.parseColor("#FFFFFFFF"))
                }

                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, DetailTransactionActivity::class.java)
                    intent.putExtra(DetailTransactionActivity.EXTRA_TRANSACTION, transaction)
                    itemView.context.startActivity(intent)
                }

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DiffCallback: DiffUtil.ItemCallback<Transaction> =
            object : DiffUtil.ItemCallback<Transaction>() {

                override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
                    return oldItem.transactionID == newItem.transactionID
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
                    return oldItem == newItem
                }
            }
    }

}