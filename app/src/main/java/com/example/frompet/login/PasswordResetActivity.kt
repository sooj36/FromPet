package com.example.frompet.login

import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.frompet.R
import com.example.frompet.databinding.ActivityPasswordResetBinding

class PasswordResetActivity : AppCompatActivity() {
    private lateinit var mBinding:ActivityPasswordResetBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityPasswordResetBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }
}