package com.payroc.transactionprocessor.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.payroc.transactionprocessor.R
import com.payroc.transactionprocessor.databinding.ActivityMainBinding
import com.payroc.transactionprocessor.ui.pay.PayFragment
import com.payroc.transactionprocessor.ui.receipts.ReceiptsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val payFragment = PayFragment()
        val receiptsFragment = ReceiptsFragment()

        setCurrentFragment(payFragment)

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_item_home -> setCurrentFragment(payFragment)
                R.id.menu_item_receipts -> setCurrentFragment(receiptsFragment)
            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame_layout_fragment, fragment).commit()
        }
}