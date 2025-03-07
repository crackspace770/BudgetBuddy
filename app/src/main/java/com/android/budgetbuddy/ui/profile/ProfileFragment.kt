package com.android.budgetbuddy.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.android.budgetbuddy.data.ViewModelFactory
import com.android.budgetbuddy.data.utils.loadImageUrl
import com.android.budgetbuddy.data.worker.NotificationUtils
import com.android.budgetbuddy.databinding.FragmentProfileBinding
import com.android.budgetbuddy.ui.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class ProfileFragment:Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel:ProfileViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private var database: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var auth: FirebaseAuth = Firebase.auth


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getProfile()
        notification()
        logout()

    }

    private fun notification(){

        viewModel.getNotificationSettings().observe(viewLifecycleOwner) { isNotificationActive ->
            binding.switchAlarm.isChecked = isNotificationActive
        }


        binding.switchAlarm.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveNotificationSetting(isChecked)
            if (isChecked) {
                NotificationUtils.scheduleDailyNotification(requireContext())
            } else {
                NotificationUtils.cancelNotifications(requireContext())
            }
        }

    }

    private fun getProfile(){
        val userId = auth.currentUser!!.uid


        val dataUser = database.collection("user").document(userId)
        dataUser.get().addOnSuccessListener {
            val email = it.get("email").toString()
            val fotoProfil = it.get("fotoProfil").toString()

            // Extract the part of the email before the "@"
            val userName = email.substringBefore("@")

            binding.apply {
                imgProfile.loadImageUrl(fotoProfil, requireContext())
                tvEmail.text = email
                tvName.text = userName
            }
        }

    }

    private fun logout(){
        binding.cardLogout.setOnClickListener {
            showExitConfirmationDialog()
        }
    }

    private fun showExitConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Konfirmasi")
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setPositiveButton("Ya") { _, _ ->

                //categoryViewModel.deleteAllCategories()
                // Clear the shared preference for login status
                viewModel.saveLoginState("isLoggedIn", false)
                // Logout from Firebase
                auth.signOut()
                // Redirect to LoginActivity
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)

            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

}