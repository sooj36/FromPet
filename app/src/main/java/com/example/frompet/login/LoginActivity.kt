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
                // 실패 메시지 표시나 다른 작업을 수행하려면 여기에 추가
            }
        }
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // ... (다른 코드 생략)
        private fun showLoginResultToast(isSuccess: Boolean) {
            val message = if (isSuccess) "로그인 성공" else "로그인 실패"
            showToast(message)
        }

        private fun showSignUpResultToast(isSuccess: Boolean) {
            val message = if (isSuccess) "회원가입 성공" else "회원가입 실패"
            showToast(message)
        }
    }

