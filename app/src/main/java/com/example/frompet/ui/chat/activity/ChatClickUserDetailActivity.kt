package com.example.frompet.ui.chat.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import coil.load
import com.example.frompet.R
import com.example.frompet.databinding.ActivityChatClickUserDetailBinding
import com.example.frompet.data.model.User

class ChatClickUserDetailActivity : AppCompatActivity() {
    companion object {
        const val MATCHED_USERS = "matchedUser"
        const val USER = "user"

        const val ACTION = "action"
        const val MATCH = "match"
        const val DISLIKE = "dislike"
    }
    private lateinit var binding: ActivityChatClickUserDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatClickUserDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user: User? = intent.getParcelableExtra(USER)
        user?.let {
            displayUserInfo(it)
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.ivPetProfile.setOnClickListener {
            val intent = Intent(this@ChatClickUserDetailActivity, ChatPullScreenActivity::class.java)
            intent.putExtra(ChatPullScreenActivity.IMAGE_URL, user?.petProfile)
            startActivity(intent)
        }

    }

    private fun displayUserInfo(user: User)= with(binding) {

            tvPetName.text = user.petName
            tvPetAge.text = "${user.petAge}세"
            tvPetGender.text = user.petGender
            tvPetType.text = user.petType
            tvPetDes.text = user.petDescription
            tvPetIntro.text = user.petIntroduction
            tvPetNeuter.text = user.petNeuter
            user.petProfile.let {
                ivPetProfile.load(user.petProfile){
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

