package com.payroc.transactionprocessor.ui.receipts

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.payroc.transactionprocessor.R

class ReceiptsFragment : Fragment() {

    companion object {
        fun newInstance() = ReceiptsFragment()
    }

    private lateinit var viewModel: ReceiptsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_receipts, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ReceiptsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}