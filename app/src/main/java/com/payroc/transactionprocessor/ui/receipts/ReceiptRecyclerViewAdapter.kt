package com.payroc.transactionprocessor.ui.receipts

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.payroc.transaction.data.model.response.Receipts
import com.payroc.transactionprocessor.database.entities.Receipt
import com.payroc.transactionprocessor.databinding.ReceiptViewBinding
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

class ReceiptRecyclerViewAdapter(val receipts: List<Receipt>) :
    RecyclerView.Adapter<ReceiptRecyclerViewAdapter.ReceiptViewHolder>() {

    // Initialize context with overridden onAttachedToRecyclerView
    private lateinit var context: Context

    private lateinit var gson: Gson

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ViewAdapterEntryPoint {
        fun providesGsonInstance(): Gson
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptViewHolder {
        val binding = ReceiptViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReceiptViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReceiptViewHolder, position: Int) {

        //ToDo loop through merchantDetails and transactionData, create new textView and add view to linear layout

        val receipt = receipts[position].receipts
        val gson = Gson()
        val receiptData = gson.fromJson(receipt, Receipts::class.java)

        receiptData.merchantDetails.forEach { merchantDetails ->

            // Handle label - bold, etc
            val labelText = TextView(context)
            labelText.text = merchantDetails.label
            labelText.setTypeface(null, Typeface.BOLD)
            holder.binding.receiptLinearLayout.addView(labelText)

            // Handle value - standard
            val valueText = TextView(context)
            valueText.text = merchantDetails.value
            holder.binding.receiptLinearLayout.addView(valueText)
        }
        receiptData.transactionData.forEach { transactionData ->
            // Handle label - bold, etc
            val labelText = TextView(context)
            labelText.text = transactionData.label
            labelText.setTypeface(null, Typeface.BOLD)
            holder.binding.receiptLinearLayout.addView(labelText)

            // Handle value - standard
            val valueText = TextView(context)
            valueText.text = transactionData.value
            holder.binding.receiptLinearLayout.addView(valueText)
        }
    }


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context

        // We have context and can now use the entry point
        val viewAdapterEntryPoint =
            EntryPointAccessors.fromApplication(context, ViewAdapterEntryPoint::class.java)
        gson = viewAdapterEntryPoint.providesGsonInstance()
    }

    override fun getItemCount(): Int {
        return receipts.size
    }

    inner class ReceiptViewHolder(val binding: ReceiptViewBinding) :
        RecyclerView.ViewHolder(binding.root)
}

