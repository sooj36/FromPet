package com.example.frompet.ui.setting

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.frompet.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileActivity : AppCompatActivity() {
    private var _binding: ActivityProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var storage: FirebaseStorage
    private lateinit var ViewModel: SettingViewModel
    private var selectedImageUri: Uri? = null // 이미지 선택을 저장할 변수

    companion object {
        const val GALLERY_REQUEST_CODE = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storage = FirebaseStorage.getInstance()

        ViewModel = ViewModelProvider(this)[SettingViewModel::class.java]

        ViewModel.loadUserPetProfile()

        ViewModel.petProfile.observe(this) { petProfile ->
            petProfile?.let {
                Glide.with(this)
                    .load(it)
                    .into(binding.ivPet2)
            }
        }
        ViewModel.petName.observe(this) { petName ->
            binding.etPetName.setText(petName)
        }
        ViewModel.petType.observe(this) { petType ->
            binding.etPetType.setText(petType)
        }
        ViewModel.petGender.observe(this) { petGender ->
            binding.etPetGender.setText(petGender)
        }
        ViewModel.petAge.observe(this) { petAge ->
            binding.etPetAge.setText(petAge?.toString() ?: "")
        }
        ViewModel.petIntroduction.observe(this) { petIntroduction ->
            binding.etPetIntroduction.setText(petIntroduction)
        }
        ViewModel.petDescription.observe(this) { petDescription ->
            binding.etPurpose.setText(petDescription)
        }

        binding.ibProfile.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
        }

        binding.btModify2.setOnClickListener {
            onProfileUpdateClick()
        }
        binding.ibBackButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun updateUserProfileOnly(
        userId: String,
        updatedPetName: String,
        updatedPetType: String,
        updatedPetGender: String,
        updatedPetAge: Int,
        updatedPetIntroduction: String,
        updatedPetDescription: String
    ) {
        val userDocRef = FirebaseFirestore.getInstance().collection("User").document(userId)

        val updateMap: Map<String, Any> = mapOf(
            "petName" to updatedPetName,
            "petType" to updatedPetType,
            "petGender" to updatedPetGender,
            "petAge" to updatedPetAge,
            "petIntroduction" to updatedPetIntroduction,
            "petDescription" to updatedPetDescription
        )

        userDocRef.update(updateMap)
            .addOnSuccessListener {
                showToast("사용자 정보 업데이트 성공")
            }
            .addOnFailureListener { e ->
                Log.e("lee", "사용자 정보 업데이트 실패", e)
                showToast("사용자 정보 업데이트 실패")
            }
    }
    private fun updateImageUrlInFirestore(userId: String, imageUrl: String) {
        val userDocRef = FirebaseFirestore.getInstance().collection("User").document(userId)

        // 이미지 URL을 Firestore에 업데이트
        userDocRef.update("petProfile", imageUrl)
            .addOnSuccessListener {
                showToast("이미지 URL 업데이트 성공")
                onBackPressed()
            }
            .addOnFailureListener {
                showToast("이미지 URL 업데이트 실패")
            }
    }

    private fun updateImageOnly(uri: Uri) {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "IMAGE_$timestamp.png"
        val storageRef: StorageReference = storage.reference.child("images").child(fileName)

        storageRef.putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                storageRef.downloadUrl
            }
            .addOnSuccessListener { downloadUri ->
                val imageUrl = downloadUri.toString()
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                userId?.let {
                    // 이미지 URL을 Firestore에 업데이트
                    updateImageUrlInFirestore(userId, imageUrl)
                }
            }
    }

    private fun updateImageAndUserProfile(
        userId: String,
        updatedPetName: String,
        updatedPetType: String,
        updatedPetGender: String,
        updatedPetAge: Int,
        updatedPetIntroduction: String,
        updatedPetDescription: String,
        uri: Uri
    ) {
        // 먼저 이미지를 업로드하고 Firestore에 이미지 URL을 업데이트
        updateImageOnly(uri)

        // 그런 다음 사용자 정보를 업데이트
        updateUserProfileOnly(userId, updatedPetName, updatedPetType, updatedPetGender, updatedPetAge, updatedPetIntroduction, updatedPetDescription)
    }

    private fun onProfileUpdateClick() {
        // 사용자가 수정한 정보 가져오기
        val updatedPetName = binding.etPetName.text.toString()
        val updatedPetType = binding.etPetType.text.toString()
        val updatedPetGender = binding.etPetGender.text.toString()
        val updatedPetAge = binding.etPetAge.text.toString().toInt()
        val updatedPetIntroduction = binding.etPetIntroduction.text.toString()
        val updatedPetDescription = binding.etPurpose.text.toString()

        // Firebase Firestore에서 현재 사용자 ID 가져오기
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        userId?.let {
            if (selectedImageUri != null) {
                // 이미지 선택 시 이미지와 사용자 정보 업데이트
                updateImageAndUserProfile(it, updatedPetName, updatedPetType, updatedPetGender, updatedPetAge, updatedPetIntroduction, updatedPetDescription, selectedImageUri!!)
            } else {
                // 이미지를 선택하지 않았을 때 사용자 정보만 업데이트
                updateUserProfileOnly(it, updatedPetName, updatedPetType, updatedPetGender, updatedPetAge, updatedPetIntroduction, updatedPetDescription)
                onBackPressed()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data // 이미지 Uri 저장
            Glide.with(this)
                .load(selectedImageUri)
                .into(binding.ivPet2)
        }
    }
}