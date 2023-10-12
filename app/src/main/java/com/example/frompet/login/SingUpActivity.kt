package com.example.frompet.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.frompet.R
import com.example.frompet.databinding.ActivitySingUpBinding
import com.example.frompet.login.viewmodel.LoginViewModel
import com.example.frompet.login.viewmodel.LoginViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class SingUpActivity : AppCompatActivity() {
    private lateinit var mBinding:ActivitySingUpBinding
    private val viewModel by lazy {
        ViewModelProvider(this, LoginViewModelFactory())[LoginViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySingUpBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.SignUpButton.setOnClickListener {
            val email = mBinding.etEmailUp.text.toString()
            val password = mBinding.etPasswordUp.text.toString()
            viewModel.signUp(email,password)
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)


        }

    }
}