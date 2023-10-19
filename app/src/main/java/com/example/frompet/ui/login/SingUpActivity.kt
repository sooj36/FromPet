package com.example.frompet.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.frompet.databinding.ActivitySingUpBinding

class SingUpActivity : AppCompatActivity() {
    private var _binding: ActivitySingUpBinding? = null
    private val binding get() = _binding!!
    private val viewModel by lazy {
        ViewModelProvider(this, LoginViewModelFactory())[LoginViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySingUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.SignUpButton.setOnClickListener {
            val email = binding.etEmailUp.text.toString()
            val password = binding.etPasswordUp.text.toString()
            viewModel.signUp(email,password)

        }
        viewModel.loginResult.observe(this){signupSuccess->
            if (signupSuccess) {
                showSignUpResultToast(true)
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            } else {
                showSignUpResultToast(false)
            }
        }

    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    private fun showSignUpResultToast(isSuccess: Boolean) {
        val message = if (isSuccess) "회원가입 성공" else "회원가입 실패"
        showToast(message)
    }
}