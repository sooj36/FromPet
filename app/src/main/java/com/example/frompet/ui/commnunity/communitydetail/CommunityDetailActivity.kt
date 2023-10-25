package com.example.frompet.ui.commnunity.communitydetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.example.frompet.R
import com.example.frompet.databinding.ActivityCommunityDetailBinding

class CommunityDetailActivity : AppCompatActivity() {

    private var _binding : ActivityCommunityDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCommunityDetailBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            finish()
        }
    }
}