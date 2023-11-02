package com.example.frompet.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.example.frompet.R
import com.example.frompet.data.model.CommunityHomeData
import com.example.frompet.databinding.ActivityHomeFilterBinding
import com.example.frompet.ui.login.MemberInfoAdapter


class HomeFilterActivity : AppCompatActivity() {
    private var _binding: ActivityHomeFilterBinding? = null
    private val filterViewModel: HomeFilterViewModel by viewModels { HomeFilterViewModelFactory() }
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivClose.setOnClickListener {
            finish()
        }

        val communityHomeData = mutableListOf(
            CommunityHomeData(R.drawable.dog, "강아지"),
            CommunityHomeData(R.drawable.cat, "고양이"),
            CommunityHomeData(R.drawable.raccoon, "라쿤"),
            CommunityHomeData(R.drawable.fox, "여우"),
            CommunityHomeData(R.drawable.chick, "새"),
            CommunityHomeData(R.drawable.pig, "돼지"),
            CommunityHomeData(R.drawable.snake, "파충류"),
            CommunityHomeData(R.drawable.fish, "물고기"),
        )
        val adapter = MemberInfoAdapter(this, communityHomeData)
        val spinner = binding.spPetType
        spinner.adapter = adapter
        binding.chipGroup.setOnCheckedChangeListener { chipGroup, checkedId ->
            val selectedGender = when (checkedId) {
                R.id.chip_all -> "모든 성별"
                R.id.chip_male -> "수컷"
                R.id.chip_female -> "암컷"
                else -> ""
            }
        }


        binding.btComplete.setOnClickListener {

        }
    }
    }
