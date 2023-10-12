package com.example.frompet.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.frompet.R
import com.example.frompet.databinding.ActivityMemberInfoBinding

class MemberInfoActivity : AppCompatActivity() {
    private lateinit var mBinding:ActivityMemberInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMemberInfoBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }
}