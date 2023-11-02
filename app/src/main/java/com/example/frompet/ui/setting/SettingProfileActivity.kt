package com.example.frompet.ui.setting

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.frompet.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SettingProfileActivity : AppCompatActivity() {
    private var _binding: ActivityProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var storage: FirebaseStorage
    private lateinit var ViewModel: SettingViewModel
    private var selectedImageUri: Uri? = null // 이미지 선택을 저장할 변수임


    companion object {
        const val GALLERY_REQUEST_CODE = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val customProgressDialog = ProgressDialog(this)
        customProgressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

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
        ViewModel.petIntroduction.observe(this) { petDescription ->
            binding.etPurpose.setText(petDescription)
        }
        ViewModel.petDescription.observe(this) { petIntroduction ->
            binding.etPetIntroduction.setText(petIntroduction)
        }
        ViewModel.petNeuter.observe(this) { petNeuter ->
            binding.etPetNeuter.setText(petNeuter)
        }

        binding.ibProfile.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
        }

        binding.btModify2.setOnClickListener {
            customProgressDialog.show()
            onProfileUpdateClick()
        }
        binding.btBack.setOnClickListener {
            onBackPressed()
        }
        val textWatcherIntroduction = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 텍스트가 변경되기 전에 호출됩니다.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트가 변경될 때 호출됩니다.
                val charCount = s?.length ?: 0
                binding.tvCharCount.text = "$charCount/95" // 글자 수를 업데이트합니다.
            }

            override fun afterTextChanged(s: Editable?) {
                // 텍스트가 변경된 후에 호출됩니다.
            }
        }

        val textWatcherPurpose = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 텍스트가 변경되기 전에 호출됩니다.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트가 변경될 때 호출됩니다.
                val charCount = s?.length ?: 0
                binding.tvCharCount2.text = "$charCount/57" // 글자 수를 업데이트합니다.
            }

            override fun afterTextChanged(s: Editable?) {
                // 텍스트가 변경된 후에 호출됩니다.
            }
        }

        // EditText에 TextWatcher를 추가합니다.
        binding.etPetIntroduction.addTextChangedListener(textWatcherIntroduction)
        binding.etPurpose.addTextChangedListener(textWatcherPurpose)

    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun updateUserProfile(
        userId: String,
        updatedPetName: String,
        updatedPetType: String,
        updatedPetGender: String,
        updatedPetAge: Int,
        updatedPetIntroduction: String,
        updatedPetDescription: String,
        updatedPetNeuter: String,
        uri: Uri?
    ) {
        val userDocRef = FirebaseFirestore.getInstance().collection("User").document(userId)

        val updateMap: MutableMap<String, Any> = HashMap()
        updateMap["petName"] = updatedPetName
        updateMap["petType"] = updatedPetType
        updateMap["petGender"] = updatedPetGender
        updateMap["petAge"] = updatedPetAge
        updateMap["petIntroduction"] = updatedPetIntroduction
        updateMap["petDescription"] = updatedPetDescription
        updateMap["petNeuter"] = updatedPetNeuter

        if (uri != null) {
            // 이미지 선택한 경우 이미지 URL도 업데이트
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "IMAGE_$timestamp.png"
            val storageRef = storage.reference.child("images").child(fileName)

            storageRef.putFile(uri)
                .continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let { throw it }
                    }
                    storageRef.downloadUrl
                }
                .addOnSuccessListener { downloadUri ->
                    val imageUrl = downloadUri.toString()
                    updateMap["petProfile"] = imageUrl
                    updateUserInfo(userDocRef, updateMap)
                }
                .addOnFailureListener { e ->
                    Log.e("lee", "이미지 업로드 실패", e)
                    showToast("이미지 업로드 실패")
                }
        } else {
            // 이미지를 선택하지 않은 경우 사용자 정보만 업데이트
            updateUserInfo(userDocRef, updateMap)
        }
    }

    private fun updateUserInfo(userDocRef: DocumentReference, updateMap: Map<String, Any>) {
        userDocRef.update(updateMap)
            .addOnSuccessListener {
                onBackPressed()
            }
            .addOnFailureListener { e ->
                Log.e("lee", "사용자 정보 업데이트 실패", e)
                showToast("사용자 정보 업데이트 실패")
                onBackPressed()
            }
    }

    private fun onProfileUpdateClick() {
        val updatedPetName = binding.etPetName.text.toString()
        val updatedPetType = binding.etPetType.text.toString()
        val updatedPetGender = binding.etPetGender.text.toString()
        val updatedPetAge = binding.etPetAge.text.toString().toInt()
        val updatedPetIntroduction = binding.etPurpose.text.toString()
        val updatedPetDescription = binding.etPetIntroduction.text.toString()
        val updatedPetNeuter = binding.etPetNeuter.text.toString()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        userId?.let {
            updateUserProfile(it, updatedPetName, updatedPetType, updatedPetGender, updatedPetAge, updatedPetIntroduction, updatedPetDescription,updatedPetNeuter, selectedImageUri)
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