package com.example.frompet.chating

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import coil.load
import com.example.frompet.R
import com.example.frompet.databinding.ActivityChatUserDetailBinding
import com.example.frompet.login.data.UserModel
import com.example.frompet.login.viewmodel.MatchViewModel

class ChatUserDetailActivity : AppCompatActivity() {
    companion object {
        const val MATCHED_USERS = "matchedUser"
        const val USER = "user"
        const val ACTION = "action"
        const val MATCH = "match"
        const val DISLIKE = "dislike"
    }

    private lateinit var binding: ActivityChatUserDetailBinding
    private val matchViewModel: MatchViewModel by viewModels()
    private val chatViewModel: ChatViewModel by viewModels()

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
                matchViewModel.matchUser(userId)
                Toast.makeText(this, "${user.petName} 와(과) 매치 되었습니다!", Toast.LENGTH_LONG).show()
                val resultIntent = Intent()
                resultIntent.putExtra(MATCHED_USERS, userId)
                resultIntent.putExtra(ACTION, MATCH)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }

        binding.dislikeBtn.setOnClickListener {
            user?.uid?.let { userId ->
                matchViewModel.dislike(userId)
                Toast.makeText(this, "${user.petName}와(과) 매칭에 실패 했습니다!", Toast.LENGTH_LONG).show()

                val result = Intent()
                result.putExtra(MATCHED_USERS, userId)
                result.putExtra(ACTION, DISLIKE)
                setResult(Activity.RESULT_OK, result)
                finish()
            }
        }
        binding.backBtn.setOnClickListener {
            finish()
        }
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
