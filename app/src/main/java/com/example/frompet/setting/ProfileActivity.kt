package com.example.frompet.setting

import SettingViewModel
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
import com.example.frompet.databinding.ActivitySingUpBinding
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
            // 사용자가 수정한 정보 가져오기
            val updatedPetName = binding.etPetName.text.toString()
            val updatedPetType = binding.etPetType.text.toString()
            val updatedPetGender = binding.etPetGender.text.toString()
            val updatedPetAge = binding.etPetAge.text.toString().toInt()
            val updatedPetIntroduction = binding.etPetIntroduction.text.toString()
            val updatedPetDescription = binding.etPurpose.text.toString()

            // Firebase Firestore에서 현재 사용자 ID 가져오기
            val userId = FirebaseAuth.getInstance().currentUser?.uid

            if (userId != null) {
                val userDocRef = FirebaseFirestore.getInstance().collection("User").document(userId)

                // 정보 업데이트
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
                        // 이미지가 선택되었을 때만 업로드하도록 수정
                        selectedImageUri?.let { uploadAndUpdateImage(it) }
                    }
                    .addOnFailureListener { e ->
                        Log.e("ProfileActivity", "사용자 정보 업데이트 실패", e)
                        Toast.makeText(this, "사용자 정보 업데이트 실패", Toast.LENGTH_SHORT).show()
                    }
            }
            onBackPressed()
        }
        binding.ibBackButton.setOnClickListener {
            onBackPressed()
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

    private fun uploadAndUpdateImage(uri: Uri?) {
        uri?.let { petProfileUri ->
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "IMAGE_$timestamp.png"
            val storageRef: StorageReference = storage.reference.child("images").child(fileName)

            // 이미지를 Firebase Storage에 업로드하고 Firestore에 업데이트하기
            storageRef.putFile(petProfileUri)
                .continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let { throw it }
                    }
                    storageRef.downloadUrl
                }
                .addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    if (userId != null) {
                        val userDocRef = FirebaseFirestore.getInstance().collection("User").document(userId)
                        val updateMap: Map<String, Any> = mapOf("petProfile" to imageUrl)
                        // Firestore에 이미지 URL 업데이트
                        userDocRef.update(updateMap)
                            .addOnSuccessListener {
                                showToast("이미지 업로드 성공")
                                onBackPressed()
                            }
                            .addOnFailureListener {
                                showToast("이미지 업로드 실패")
                            }
                    }
                }
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}