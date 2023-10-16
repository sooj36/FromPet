package com.example.frompet.login

import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.frompet.R
import com.example.frompet.databinding.ActivityMemberInfoBinding
import com.example.frompet.databinding.ActivityPasswordResetBinding
import com.example.frompet.login.viewmodel.LoginViewModel
import com.example.frompet.login.viewmodel.LoginViewModelFactory
import com.example.frompet.login.viewmodel.UserViewModel
import com.google.firebase.firestore.auth.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PasswordResetActivity : AppCompatActivity() {
    private var _binding: ActivityPasswordResetBinding? = null
    private val binding get() = _binding!!

    private val viewModel by lazy {
        ViewModelProvider(this).get(UserViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPasswordResetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btSend.setOnClickListener {
            val email = binding.etEmilReset.text.toString()
            viewModel.viewModelScope.launch {
                viewModel.verifySendPasswordReset(email)
                viewModel.allEventsFlow.collect { event ->
                    when (event) {
                        is UserViewModel.AllEvents.Message -> {
                            Toast.makeText(this@PasswordResetActivity, event.message, Toast.LENGTH_SHORT).show()
                        }
                        is UserViewModel.AllEvents.ErrorCode -> {
                            when (event.code) {
                                1 -> Toast.makeText(this@PasswordResetActivity, "Error Code 1", Toast.LENGTH_SHORT).show()
                                2 -> Toast.makeText(this@PasswordResetActivity, "Error Code 2", Toast.LENGTH_SHORT).show()
                            }
                        }
                        is UserViewModel.AllEvents.Error -> {
                            Toast.makeText(this@PasswordResetActivity, event.error, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}