package com.payroc.transactionprocessor.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.payroc.transactionprocessor.R
import com.payroc.transactionprocessor.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        homeViewModel = ViewModelProvider(this@HomeActivity)[HomeViewModel::class.java]

        binding.payButtonId.setOnClickListener {
            homeViewModel.payClicked(binding.amountEditText.text.toString().toDouble())
        }
    }
}