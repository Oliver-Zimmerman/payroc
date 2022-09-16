package com.payroc.transactionprocessor.ui.receipts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.payroc.transactionprocessor.R
import com.payroc.transactionprocessor.database.entities.Receipt

class RecyclerViewAdapter(val receipts: List<Receipt>)
    : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : RecyclerViewAdapter.ViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_view,parent,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerViewAdapter.ViewHolder, position: Int) {
        holder.id.text = receipts[position].uniqueReference.toString()
        holder.name.text = receipts[position].receipts
    }

    override fun getItemCount(): Int {
        return receipts.size
    }

    class ViewHolder(itemView:View): RecyclerView.ViewHolder(itemView){
        val id = itemView.tvId
        val name = itemView.tvName
    }
}