package com.example.frompet.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.frompet.R
import com.example.frompet.databinding.ActivitySingUpBinding

class SingUpActivity : AppCompatActivity() {
    private lateinit var mBinding:ActivitySingUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySingUpBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }
}