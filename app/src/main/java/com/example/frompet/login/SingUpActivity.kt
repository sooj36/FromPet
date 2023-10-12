package com.example.frompet.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.frompet.R
import com.example.frompet.databinding.ActivitySingUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class SingUpActivity : AppCompatActivity() {
    private lateinit var mBinding:ActivitySingUpBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySingUpBinding.inflate(layoutInflater)
        mAuth = FirebaseAuth.getInstance()
        setContentView(mBinding.root)
    }
}