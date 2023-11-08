package com.example.frompet.ui.map

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import coil.load
import com.example.frompet.MatchSharedViewModel
import com.example.frompet.R
import com.example.frompet.data.model.User
import com.example.frompet.databinding.ActivityMapUserDetailBinding
import com.example.frompet.ui.chat.activity.ChatPullScreenActivity
import com.example.frompet.util.showToast

class MapUserDetailActivity : AppCompatActivity() {
    companion object {
        const val USER = "user"
    }

    private lateinit var binding: ActivityMapUserDetailBinding
    private val viewModel: MatchSharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapUserDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        val user: User? = intent.getParcelableExtra(USER)
        user?.let {
            displayUserInfo(it)
        }
        binding.ivPetProfile.setOnClickListener {
            val intent = Intent(this@MapUserDetailActivity, ChatPullScreenActivity::class.java)
            intent.putExtra(ChatPullScreenActivity.IMAGE_URL, user?.petProfile)
            startActivity(intent)
        }
        setClickListeners(user)
    }


    private fun setClickListeners(user: User?) {
        binding.likeBtn.setOnClickListener {
            user?.let {
                viewModel.like(user.uid)
                showToast("${user.petName}님에게 친구신청을 걸었습니다!", Toast.LENGTH_SHORT)
                finish()
            }
        }
    }


    private fun displayUserInfo(user: User)=with(binding) {
            tvPetName.text = user.petName
            tvPetAge.text = "${user.petAge}세"
            tvPetGender.text = user.petGender
            tvPetType.text = user.petType
            tvPetDes.text = user.petDescription
            tvPetIntro.text = user.petIntroduction
            tvPetNeuter.text = user.petNeuter
            user.petProfile.let {
                ivPetProfile.load(user.petProfile) {
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

