package com.example.frompet.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.frompet.R
import com.example.frompet.databinding.ActivityLoginBinding
import com.example.frompet.databinding.ActivityMainBinding
import com.example.frompet.home.HomeFragment
import com.example.frompet.login.viewmodel.LoginViewModel
import com.example.frompet.login.viewmodel.LoginViewModelFactory
import com.example.frompet.main.MainActivity


class LoginActivity : AppCompatActivity() {
    private lateinit var mBinding:ActivityLoginBinding
    private val viewModel by lazy {
        ViewModelProvider(this,
            LoginViewModelFactory()
        )[LoginViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.btLogin.setOnClickListener {
            val email = mBinding.etEmail.text.toString()
            val password = mBinding.etPassword.text.toString()
            viewModel.singIn(email,password)

        }

        mBinding.btSingup.setOnClickListener {
            val intent = Intent(this, SingUpActivity::class.java)
            startActivity(intent)
        }


        viewModel.loginResult.observe(this) { loginSuccess ->
            if (loginSuccess) {
                showLoginResultToast(true)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                showLoginResultToast(false)
            }
        }
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

        private fun showLoginResultToast(isSuccess: Boolean) {
            val message = if (isSuccess) "로그인 성공" else "로그인 실패"
            showToast(message)
        }

    }

