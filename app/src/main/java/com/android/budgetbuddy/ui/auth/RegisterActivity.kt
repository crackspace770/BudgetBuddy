package com.android.budgetbuddy.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.budgetbuddy.data.model.User
import com.android.budgetbuddy.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity:AppCompatActivity() {

    private lateinit var binding:ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.signupBtn.setOnClickListener {
            val email = binding.email.text.toString()
            val pass = binding.password.text.toString()
            val confirmPass = binding.passwordRetype.text.toString()

            when {
                email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    Toast.makeText(this, "Invalid atau Email kosong", Toast.LENGTH_LONG).show()
                }
                pass.isEmpty() ||  confirmPass.isEmpty() -> {
                    Toast.makeText(this, "Tidak boleh kosong", Toast.LENGTH_LONG).show()
                }
                pass.length < 8 ->{
                    Toast.makeText(this, "Password harus lebih dari 8 karakter", Toast.LENGTH_LONG).show()
                }
                pass != confirmPass -> {
                    Toast.makeText(this, "Password tidak sama", Toast.LENGTH_LONG).show()
                }
                else -> {
                    binding.progressBar.visibility = View.VISIBLE
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                        binding.progressBar.visibility = View.GONE
                        when {
                            task.isSuccessful -> {
                                val intent = Intent(this, LoginActivity::class.java)
                                Toast.makeText(this, "Sign Up Sukses", Toast.LENGTH_LONG).show()
                                startActivity(intent)

                                val userId = firebaseAuth.currentUser!!.uid
                                val photoUrl =
                                    "https://ui-avatars.com/api/?background=8692F7&color=fff&size=100&rounded=true&name=$email"

                                val data = User(email, fotoProfil = photoUrl)

                                // Save user data to Firestore
                                db.collection("user").document(userId).set(data)
                            }
                            else -> {
                                Toast.makeText(this, task.exception.toString(), Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
        }

        binding.haveAccount.setOnClickListener {
            finish()
        }
    }


}