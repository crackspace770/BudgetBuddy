package com.android.budgetbuddy.ui.auth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.android.budgetbuddy.MainActivity
import com.android.budgetbuddy.data.ViewModelFactory
import com.android.budgetbuddy.data.model.UserModel
import com.android.budgetbuddy.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var auth: FirebaseAuth = Firebase.auth
    private var db: FirebaseFirestore = Firebase.firestore
    private lateinit var user: UserModel

    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupLoginObserver()

        binding.createAccount.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        if (loginViewModel.isUserLoggedIn()) {
            startAppropriateActivity()
        } else {
            setupLoginButton()
        }
    }

    private fun setupLoginButton() {
        binding.loginBtn.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString()

            if (TextUtils.isEmpty(email)) {
                binding.email.error = "Masukkan email yang benar"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                binding.password.error = "Masukkan kata sandi yang benar"
                return@setOnClickListener
            }

            binding.progressCircular.root.visibility = View.VISIBLE

            loginViewModel.login(email, password) { success ->
                binding.progressCircular.root.visibility = View.GONE
                if (success) {
                    startAppropriateActivity()
                } else {
                    Toast.makeText(this, "Login gagal, silahkan coba lagi", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun startAppropriateActivity() {
        val userId = auth.currentUser?.uid ?: return
        val dataUser = db.collection("user").document(userId)
        dataUser.get().addOnSuccessListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun setupLoginObserver() {
        loginViewModel.getUser().observe(this) { user ->
            this.user = user
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
}
