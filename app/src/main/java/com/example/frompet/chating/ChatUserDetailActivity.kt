package com.example.frompet.chating

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.frompet.R
import com.example.frompet.databinding.ActivityChatUserDetailBinding
import com.example.frompet.login.data.UserModel
import com.example.frompet.login.viewmodel.MatchViewModel


class ChatUserDetailActivity : AppCompatActivity() {
    companion object{
        const val MATCHED_USERS = "matchedUser"
        const val USER = "user"
    }
    private lateinit var binding: ActivityChatUserDetailBinding
    private val viewModel: MatchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatUserDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user: UserModel? = intent.getParcelableExtra(USER)
        user?.let {
            displayUserInfo(it)
        }

        binding.likeBtn.setOnClickListener {
            user?.uid?.let { userId ->
                viewModel.matchWithUser(userId)
                Toast.makeText(this,"${user.petName} 와(과) 매치 되었습니다!",Toast.LENGTH_LONG).show()

                val resultIntent = Intent()
                resultIntent.putExtra(MATCHED_USERS, userId)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }

        binding.dislikeBtn.setOnClickListener {
            binding.apply {
                dislikeBtn.apply {

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
