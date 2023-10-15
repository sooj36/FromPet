package com.example.frompet.chating

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.frompet.R
import com.example.frompet.databinding.ActivityChatUserDetailBinding
import com.example.frompet.login.data.UserModel
import com.example.frompet.login.viewmodel.MatchViewModel

class ChatUserDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatUserDetailBinding
    private val viewModel: MatchViewModel by lazy {
        ViewModelProvider(this).get(MatchViewModel::class.java)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatUserDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user: UserModel? = intent.getParcelableExtra("user")
        user?.let {
            displayUserInfo(it)
        }
        binding.likeBtn.setOnClickListener {

            viewModel.like("CmrOTtczqVMUzuCFpVgIp1zFkOH3")
            finish()
        }
        binding.dislikeBtn.setOnClickListener {
            binding.apply {
                dislikeBtn.apply {
                    setAnimation(R.raw.dislike)
                    playAnimation()
                }
            }
        }
    }

    private fun displayUserInfo(user: UserModel) {
        binding.apply {
            tvPetName.text = "이름: ${user.petName}"
            tvPetAge.text = "나이: ${user.petAge.toString()}세"
            tvPetGender.text = "성별: ${user.petGender}"
            tvPetType.text = "종류: ${user.petType}"
            tvPetDes.text = "특징: ${user.petDescription}"
            tvPetIntro.text = "소개: ${user.petIntroduction}"
        }
    }
}