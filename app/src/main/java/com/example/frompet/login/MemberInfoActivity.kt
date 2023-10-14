package com.example.frompet.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.frompet.databinding.ActivityMemberInfoBinding
import com.example.frompet.login.data.UserModel
import com.example.frompet.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MemberInfoActivity : AppCompatActivity() {
    private var _binding: ActivityMemberInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMemberInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btUpdate.setOnClickListener {
            val petName = binding.etPetName.text.toString()
            val petAge = binding.etPetAge.text.toString().toIntOrNull() ?: 0
            val petDescription = binding.etPetDetail.text.toString()
            val petGender = binding.etPetSex.text.toString()
            val petIntroduction = binding.etPetIntroduce.text.toString()
            val petType = binding.etPetType.text.toString()
            // Firebase 현재 사용자 가져오기
            val currentUser = FirebaseAuth.getInstance().currentUser

            if (currentUser != null) {
                // User 모델을 생성
                val userModel = UserModel(
                    petAge, petDescription, petGender, petIntroduction, petName,petType
                )

                // Firestore의 "User" 컬렉션에 사용자 정보 저장
                FirebaseFirestore.getInstance().collection("User")
                    .document(currentUser.uid)
                    .set(userModel)
                    .addOnSuccessListener {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        showToast("회원 정보가 업데이트되었습니다.")
                        finish()
                    }
                    .addOnFailureListener {
                        // 정보 저장 실패
                        showToast("회원 정보 업데이트에 실패했습니다.")
                    }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}