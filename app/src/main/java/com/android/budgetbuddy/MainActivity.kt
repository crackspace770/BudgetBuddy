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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val homeFragment = ReportFragment()
        val budgetFragment = BudgetFragment()
        val laporanFragment = TipsFragment()
        val profileFragment = ProfileFragment()

        binding.chipAppBar.setItemSelected(R.id.reportFragment,true)
        makeCurrentFragment(homeFragment)
        binding.chipAppBar.setOnItemSelectedListener {
            when (it){
                R.id.reportFragment -> makeCurrentFragment(homeFragment)
                R.id.budgetFragment -> makeCurrentFragment(budgetFragment)
                R.id.tipsFragment -> makeCurrentFragment(laporanFragment)
                R.id.profileFragment -> makeCurrentFragment(profileFragment)


                R.id.nav_transaction -> {
                    val intent = Intent(this, InputTransactionActivity::class.java)
                    startActivity(intent)
                }
            }

        }


    }
    private fun makeCurrentFragment(fragment: Fragment) { //method 2
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }
    }



}