package com.example.frompet.login

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.frompet.databinding.ActivityMemberInfoBinding
import com.example.frompet.login.data.UserModel
import com.example.frompet.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MemberInfoActivity : AppCompatActivity() {
    private var _binding: ActivityMemberInfoBinding? = null
    private val binding get() = _binding!!
    private val PICK_IMAGE_FROM_ALBUM = 1
    // FirebaseStorage 초기화
    val storage = FirebaseStorage.getInstance()
    private var petProfile: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMemberInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.petProfile.setOnClickListener {
            goGallery()
        }

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
                // Check if petProfileUri is not null before proceeding
                if (petProfile != null) {
                    contentUpload(petProfile)
                } else {
                    showToast("프로필 이미지를 선택하세요.")
                }

                // User 모델을 생성
                val userModel = UserModel(
                    petAge, petDescription, petGender, petIntroduction, petName, petProfile?.toString(), petType
                )
                userModel.uid = currentUser.uid


                // Firestore의 "User" 컬렉션에 사용자 정보 저장
                FirebaseFirestore.getInstance().collection("User")
                    .document(currentUser.uid)
                    .set(userModel)
                    .addOnSuccessListener {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_FROM_ALBUM && resultCode == RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                contentUpload(uri.toString())
            }
        }
    }
    // contentUpload() 함수 내부에서 이미지를 Firebase Storage에 업로드할 수 있습니다.
    private fun contentUpload(uri: String?) {
        uri?.let { petProfileUri ->
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "IMAGE_$timestamp.png"
            // 서버 스토리지에 접근하기
            val storageRef = storage.reference.child("images").child(fileName)
            // 서버 스토리지에 파일 업로드하기
            storageRef.putFile(petProfileUri)
                .continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let { throw it }
                    }
                    storageRef.downloadUrl
                }
                .addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    showToast("이미지 업로드 성공")
                    petProfile = imageUrl
                    Glide.with(this)
                        .load(imageUrl)
                        .into(binding.petProfile)//ㅁㄴㅇㅁㄴㅇ
                }
                .addOnCanceledListener {
                    // 업로드 취소 시
                }

        }
    }

    private fun goGallery(){
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent,PICK_IMAGE_FROM_ALBUM)
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

private fun StorageReference.putFile(petProfileUri: String): UploadTask {
    val file = Uri.parse(petProfileUri) // 문자열 URI를 Uri 객체로 변환
    return putFile(file)
}
