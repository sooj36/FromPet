package com.example.frompet.ui.commnunity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.frompet.R
import com.example.frompet.databinding.ActivityCommunityBinding
import com.example.frompet.databinding.ActivityCommunityDetailBinding
import com.example.frompet.ui.commnunity.community.CommunityActivity

class CommunityDetailActivity : AppCompatActivity() {

    private var _binding : ActivityCommunityDetailBinding? = null
    private val binding get() = _binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityCommunityDetailBinding.inflate(layoutInflater)

        setContentView(binding?.root)

        binding?.ivDetailBackbtn?.setOnClickListener {
            val inent : Intent = Intent(this, CommunityActivity::class.java)
            startActivity(intent)

        }
    }
}