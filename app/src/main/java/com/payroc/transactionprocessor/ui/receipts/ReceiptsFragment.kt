package com.payroc.transactionprocessor.ui.receipts

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.payroc.transactionprocessor.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReceiptsFragment : Fragment(R.layout.fragment_receipts) {

    companion object {
        fun newInstance() = ReceiptsFragment()
    }

    private lateinit var viewModel: ReceiptsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ReceiptsViewModel::class.java]
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ReceiptsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}