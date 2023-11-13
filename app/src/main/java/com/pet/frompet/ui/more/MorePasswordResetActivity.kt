package com.pet.frompet.ui.more

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pet.frompet.R
import com.pet.frompet.databinding.ActivityMorePasswordResetBinding
import com.pet.frompet.databinding.ActivityPasswordResetBinding
import com.pet.frompet.ui.login.LoginViewModel
import kotlinx.coroutines.launch

class MorePasswordResetActivity : AppCompatActivity() {

    private var _binding: ActivityMorePasswordResetBinding? = null
    private val binding get() = _binding!!

    private val viewModel by lazy {
        ViewModelProvider(this).get(MorePasswordResetViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMorePasswordResetBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btSend.setOnClickListener {
            val email = binding.etEmilReset.text.toString()
            viewModel.viewModelScope.launch {
                viewModel.verifySendPasswordReset(email)
                viewModel.allEventsFlow.collect { event ->
                    when (event) {
                        is LoginViewModel.AllEvents.Message -> {
                            Toast.makeText(this@MorePasswordResetActivity, event.message, Toast.LENGTH_SHORT).show()
                        }
                        is LoginViewModel.AllEvents.ErrorCode -> {
                            when (event.code) {
                                1 -> Toast.makeText(this@MorePasswordResetActivity, "Error Code 1", Toast.LENGTH_SHORT).show()
                                2 -> Toast.makeText(this@MorePasswordResetActivity, "Error Code 2", Toast.LENGTH_SHORT).show()
                            }
                        }
                        is LoginViewModel.AllEvents.Error -> {
                            Toast.makeText(this@MorePasswordResetActivity, event.error, Toast.LENGTH_SHORT).show()
                        }

                        else -> {}
                    }
                }
            }
        }



    }
}