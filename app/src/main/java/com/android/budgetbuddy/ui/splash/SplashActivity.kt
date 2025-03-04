package com.android.budgetbuddy.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.android.budgetbuddy.MainActivity
import com.android.budgetbuddy.data.ViewModelFactory
import com.android.budgetbuddy.databinding.ActivitySplashBinding
import com.android.budgetbuddy.ui.auth.LoginActivity

class SplashActivity:AppCompatActivity() {


    private lateinit var binding: ActivitySplashBinding
    private val viewModel: SplashViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }



    private val DURATION_TIME = 2000L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Check if the user is already logged in
        Handler().postDelayed({
            val isLoggedIn = viewModel.isUserLoggedIn()
            if (isLoggedIn) {
                // Redirect to MainActivity if already logged in
                startActivity(Intent(this, MainActivity::class.java))
                finish()  // Close WelcomeActivity
            } else {
                // Proceed with WelcomeActivity setup
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            finish()
        }, DURATION_TIME)

    }

}