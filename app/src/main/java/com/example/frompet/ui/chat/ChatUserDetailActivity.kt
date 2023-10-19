package com.example.frompet.ui.chat

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import coil.load
import com.example.frompet.R
import com.example.frompet.util.showToast
import com.example.frompet.databinding.ActivityChatUserDetailBinding
import com.example.frompet.data.model.UserModel

class ChatUserDetailActivity : AppCompatActivity() {
    companion object {
        const val MATCHED_USERS = "matchedUser"
        const val USER = "user"
        const val ACTION = "action"
        const val MATCH = "match"
        const val DISLIKE = "dislike"
    }

    private lateinit var binding: ActivityChatUserDetailBinding
    private val matchSharedViewModel: MatchSharedViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatUserDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user: UserModel? = intent.getParcelableExtra(USER)
        user?.let {
            displayUserInfo(it)
        }
        setClickListeners(user)
    }

    private fun setClickListeners(user: UserModel?) {
        binding.likeBtn.setOnClickListener {
            user?.uid?.let { userId ->
                matchSharedViewModel.matchUser(userId)
                showToast("${user.petName}님과 매치 되었습니다\n대화방이 생성되었습니다!",Toast.LENGTH_LONG)
                setResultAndFinish(userId, MATCH)
            }
        }

        binding.dislikeBtn.setOnClickListener {
            user?.uid?.let { userId ->
                matchSharedViewModel.dislike(userId)
                showToast("${user.petName}님과 매칭에 실패 했습니다!",Toast.LENGTH_LONG)
                setResultAndFinish(userId, DISLIKE)
            }
        }
        binding.backBtn.setOnClickListener {
            finish()
        }
    }
    private fun setResultAndFinish(userId: String, action: String) {
        val resultIntent = Intent()
        resultIntent.putExtra(MATCHED_USERS, userId)
        resultIntent.putExtra(ACTION, action)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }


    private fun displayUserInfo(user: UserModel) {
        binding.apply {
            tvPetName.text = user.petName
            tvPetAge.text = "${user.petAge.toString()}세"
            tvPetGender.text = user.petGender
            tvPetType.text = user.petType
            tvPetDes.text = user.petDescription
            tvPetIntro.text = user.petIntroduction
            user.petProfile.let {
                ivPetProfile.load(user.petProfile){
                    error(R.drawable.kakaotalk_20230825_222509794_01)
                }
            }
        }
    }
}
