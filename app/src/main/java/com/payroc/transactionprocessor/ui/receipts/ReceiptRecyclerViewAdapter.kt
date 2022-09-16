package com.payroc.transactionprocessor.ui.receipts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.payroc.transaction.data.model.response.Receipts
import com.payroc.transactionprocessor.database.entities.Receipt
import com.payroc.transactionprocessor.databinding.ReceiptViewBinding

class ReceiptRecyclerViewAdapter(val receipts: List<Receipt>) :
    RecyclerView.Adapter<ReceiptRecyclerViewAdapter.ReceiptViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptViewHolder {
        val binding = ReceiptViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReceiptViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReceiptViewHolder, position: Int) {
        holder.binding.idTextView.text = receipts[position].uniqueReference.toString()
        holder.binding.receiptTextView.text = receipts[position].receipts
    }

    override fun getItemCount(): Int {
        return receipts.size
    }

    inner class ReceiptViewHolder(val binding: ReceiptViewBinding)
        :RecyclerView.ViewHolder(binding.root)
}