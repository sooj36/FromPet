package com.example.frompet.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import coil.load
import com.example.frompet.R
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

    }

    private fun displayUserInfo(user: User) {
        with(binding) {
            tvNamePet.text = user.petName
            tvAgePet.text = "${user.petAge.toString()}세"
            tvPetGender.text = user.petGender.toString()
            tvTypePet.text = user.petType
            tvPetDes.text = user.petDescription
            tvPetIntro.text = user.petIntroduction

            user.petProfile.let {
                ivPetImage.load(user.petProfile) {
                    error(R.drawable.kakaotalk_20230825_222509794_01)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}