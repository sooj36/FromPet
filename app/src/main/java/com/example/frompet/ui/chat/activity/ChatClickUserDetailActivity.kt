package com.example.frompet.ui.chat.activity

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
    }

    private fun displayUserInfo(user: User) {
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
