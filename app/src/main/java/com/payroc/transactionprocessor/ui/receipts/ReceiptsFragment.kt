package com.payroc.transactionprocessor.ui.receipts

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.payroc.transactionprocessor.R
import com.payroc.transactionprocessor.databinding.FragmentPayBinding
import com.payroc.transactionprocessor.databinding.FragmentReceiptsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReceiptsFragment : Fragment(R.layout.fragment_receipts) {

    private lateinit var binding: FragmentReceiptsBinding

    private lateinit var receiptsViewModel: ReceiptsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentReceiptsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the view model
        receiptsViewModel = ViewModelProvider(this)[ReceiptsViewModel::class.java]

        // Specify layout for recycler view
        val linearLayoutManager = LinearLayoutManager(
            requireContext(), RecyclerView.VERTICAL, false)

        binding.recyclerView.layoutManager = linearLayoutManager

        lifecycleScope.launch(Dispatchers.IO) {
            val receipts = receiptsViewModel.getReceipts()
            binding.recyclerView.adapter = ReceiptRecyclerViewAdapter(receipts)
        }
    }

}