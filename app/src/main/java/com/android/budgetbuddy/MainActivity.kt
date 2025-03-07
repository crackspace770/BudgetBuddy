package com.android.budgetbuddy

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.budgetbuddy.databinding.ActivityMainBinding
import com.android.budgetbuddy.ui.budget.BudgetFragment
import com.android.budgetbuddy.ui.home.HomeFragment
import com.android.budgetbuddy.ui.profile.ProfileFragment
import com.android.budgetbuddy.ui.report.ReportFragment
import com.android.budgetbuddy.ui.tips.TipsFragment
import com.android.budgetbuddy.ui.transaction.InputTransactionActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var lastSelectedItemId: Int = R.id.reportFragment // Default to Home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val homeFragment = ReportFragment()
        val budgetFragment = BudgetFragment()
        val tipsFragment = TipsFragment()
        val profileFragment = ProfileFragment()

        // Set default fragment
        binding.chipAppBar.setItemSelected(lastSelectedItemId, true)
        makeCurrentFragment(homeFragment)

        binding.chipAppBar.setOnItemSelectedListener {
            when (it) {
                R.id.reportFragment -> {
                    lastSelectedItemId = R.id.reportFragment
                    makeCurrentFragment(homeFragment)
                }
                R.id.budgetFragment -> {
                    lastSelectedItemId = R.id.budgetFragment
                    makeCurrentFragment(budgetFragment)
                }
                R.id.tipsFragment -> {
                    lastSelectedItemId = R.id.tipsFragment
                    makeCurrentFragment(tipsFragment)
                }
                R.id.profileFragment -> {
                    lastSelectedItemId = R.id.profileFragment
                    makeCurrentFragment(profileFragment)
                }
                R.id.nav_transaction -> {
                    // Save last selected tab before navigating
                    val intent = Intent(this, InputTransactionActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Re-select the last selected item when returning from another activity
        binding.chipAppBar.setItemSelected(lastSelectedItemId, true)
    }

    private fun makeCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }
    }
}
