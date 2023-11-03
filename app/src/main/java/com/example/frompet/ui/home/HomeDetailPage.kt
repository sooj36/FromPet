package com.example.frompet.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import coil.load
import com.example.frompet.R
import com.example.frompet.data.model.CommunityHomeData
import com.example.frompet.data.model.User
import com.example.frompet.databinding.ActivityHomeDetailPageBinding
import com.example.frompet.databinding.ActivityProfileBinding
import com.example.frompet.ui.chat.activity.ChatClickUserDetailActivity

class HomeDetailPage : AppCompatActivity() {
    private var _binding: ActivityHomeDetailPageBinding? = null
    private val binding get() = _binding!!
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeDetailPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = intent.getParcelableExtra(ChatClickUserDetailActivity.USER)
        user?.let {
            displayUserInfo(it)
        }
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

    }

    private fun displayUserInfo(user: User) {
        with(binding) {
            tvNamePet.text = user.petName
            tvAgePet.text = "${user.petAge.toString()}세"
            tvPetGender.text = user.petGender.toString()
            tvTypePet.text = user.petType
            tvPetDes.text = user.petDescription
            tvPetIntro.text = user.petIntroduction
            tvPetNeuter.text = user.petNeuter
            user.petProfile.let {
                ivPetImage.load(user.petProfile) {
                    error(R.drawable.kakaotalk_20230825_222509794_01)
                }
            }
            val typeText = user.petType
            val typeImageView = ivPetType
            when (typeText) {
                "고양이" -> typeImageView.setImageResource(R.drawable.cat)
                "강아지" -> typeImageView.setImageResource(R.drawable.dog)
                "라쿤" -> typeImageView.setImageResource(R.drawable.raccoon)
                "여우" -> typeImageView.setImageResource(R.drawable.fox)
                "새" -> typeImageView.setImageResource(R.drawable.chick)
                "돼지" -> typeImageView.setImageResource(R.drawable.pig)
                "파충류" -> typeImageView.setImageResource(R.drawable.snake)
                "물고기" -> typeImageView.setImageResource(R.drawable.fish)
            }
            val genderText = user.petGender
            val genderImageView = ivPetGender
            when (genderText) {
                "수컷" -> genderImageView.setImageResource(R.drawable.icon_male)
                "암컷" -> genderImageView.setImageResource(R.drawable.icon_female)
            }
            val neuterText = user.petNeuter
            val neuterImageView = ivPetNeuter
            when (neuterText) {
                "중성화" -> neuterImageView.setImageResource(R.drawable.icon_neuter)
                "중성화 안함" -> neuterImageView.setImageResource(R.drawable.icon_non_neuter)
            }
        }


    }

        override fun onDestroy() {
            super.onDestroy()
            _binding = null
        }
    }