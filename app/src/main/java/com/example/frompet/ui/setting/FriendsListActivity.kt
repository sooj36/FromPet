package com.example.frompet.ui.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.example.frompet.databinding.ActivityFriendsListBinding


class FriendsListActivity : AppCompatActivity() {
    private var _binding: ActivityFriendsListBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityFriendsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}