package com.example.frompet.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
                // 로그인 성공한 경우
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                // 로그인 실패한 경우, 처리를 하지 않음
                // 실패 메시지 표시나 다른 작업을 수행하려면 여기에 추가
            }
        }
    }
    }
