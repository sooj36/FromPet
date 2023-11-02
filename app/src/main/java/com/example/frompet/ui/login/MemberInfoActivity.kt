package com.example.frompet.ui.login

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.frompet.databinding.ActivityMemberInfoBinding
import com.example.frompet.data.model.User
import com.example.frompet.MainActivity
import com.example.frompet.R
import com.example.frompet.data.model.CommunityHomeData
import com.example.frompet.ui.commnunity.communityadd.CommunityAddActivity
import com.google.android.material.datepicker.MaterialDatePicker
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
    private var petGender: String = "" // 성별 정보 저장 변수
    private var petAge: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMemberInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val communityHomeData = mutableListOf(
            CommunityHomeData(R.drawable.dog, "강아지"),
            CommunityHomeData(R.drawable.cat, "고양이"),
            CommunityHomeData(R.drawable.raccoon, "라쿤"),
            CommunityHomeData(R.drawable.fox, "여우"),
            CommunityHomeData(R.drawable.chick, "새"),
            CommunityHomeData(R.drawable.pig, "돼지"),
            CommunityHomeData(R.drawable.snake, "파충류"),
            CommunityHomeData(R.drawable.fish, "물고기"),
        )
        val adapter = MemberInfoAdapter(this, communityHomeData)
        val spinner = binding.spPetType
        spinner.adapter = adapter



        binding.btSelectPetPhoto.setOnClickListener {
            goGallery()
        }

        binding.btComplete.setOnClickListener {
            val petName = binding.textInputEditTextAddPetName.text.toString()
            val petDescription = binding.textInputEditTextPetDescription.text.toString()
            val petIntroduction = binding.textInputEditTextPetCharacter.text.toString()
            // Firebase 현재 사용자 가져오기
            val currentUser = FirebaseAuth.getInstance().currentUser

            if (currentUser != null) {
                // Check if petProfileUri is not null before proceeding
                if (petProfile != null) {
                    contentUpload(petProfile)
                } else {
                    showToast(getString(R.string.profile_image_choose))
                }

                if (petName.isEmpty()) {
                    showToast("펫 이름을 입력하세요.")
                    return@setOnClickListener
                }

                if (petDescription.isEmpty()) {
                    showToast("펫 특징을 입력하세요.")
                    return@setOnClickListener
                }

                if (petIntroduction.isEmpty()) {
                    showToast("펫 소개를 입력하세요.")
                    return@setOnClickListener
                }


                val selectedGenderId = binding.toggleButtonPetGender.checkedButtonId
                if (selectedGenderId == R.id.buttonHe) {
                    petGender = "남"
                } else if (selectedGenderId == R.id.buttonShe) {
                    petGender = "여"
                }

                val selectedNeuterId = when (binding.toggleButtonNeuter.checkedButtonId) {
                    R.id.buttonNeuter -> "중성화"
                    R.id.buttonNoNeuter -> "중성화 안함"
                    else -> " "
                }

                val spinnerPetType = communityHomeData[spinner.selectedItemPosition].petName
                val petLogo = communityHomeData[spinner.selectedItemPosition].petLogo
                val collectionName = when (spinnerPetType) {
                    "강아지" -> "Dog"
                    "고양이" -> "Cat"
                    "라쿤" -> "Racoon"
                    "여우" -> "Fox"
                    "새" -> "Bird"
                    "돼지" -> "Pig"
                    "파충류" -> "Reptiles"
                    "물고기" -> "Fish"
                    else -> {}
                }

                // User 모델을 생성
                val user = User(
                    petAge,
                    petDescription,
                    petGender,
                    petIntroduction,
                    petName,
                    petProfile?.toString(),
                    spinnerPetType,
                    selectedNeuterId.toString()
                )

       /*         val com = CommunityHomeData(petLogo, spinnerPetType, currentUser.uid)
                val data = com.toMap()
                FirebaseFirestore.getInstance().collection(collectionName as String)
                    .add(data)
                    .addOnSuccessListener {
                    }
                    .addOnFailureListener {

                    }*/

                user.uid = currentUser.uid
                // Firestore의 "User" 컬렉션에 사용자 정보 저장
                FirebaseFirestore.getInstance().collection("User")
                    .document(currentUser.uid)
                    .set(user)
                    .addOnSuccessListener {
                        val mainIntent = Intent(this, MainActivity::class.java)
                        startActivity(mainIntent)
                        showToast(getString(R.string.update_info))
                        finish()
                    }
                    .addOnFailureListener {
                        // 정보 저장 실패
                        showToast(getString(R.string.failure_info))
                    }
            }
        }

        binding.textInputPetBirthText.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("날짜선택")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            datePicker.addOnPositiveButtonClickListener { selectedDateInMillis ->
                val selectDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(Date(selectedDateInMillis))
                binding.textInputPetBirthText.setText(selectDate)

                // 선택한 날짜를 이용하여 나이를 계산하고 petAge 변수에 할당
                val birthDate =
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(selectDate)
                if (birthDate != null) {
                    val currentDate = Date()
                    val ageMilliseconds = currentDate.time - birthDate.time
                    val ageYears = (ageMilliseconds / (1000L * 60 * 60 * 24 * 365.25)).toInt()
                    petAge = ageYears
                }
            }
            datePicker.show(supportFragmentManager, datePicker.toString())
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
            val timestamp = SimpleDateFormat(
                getString(R.string.member_info_timestamp),
                Locale.getDefault()
            ).format(Date())
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
                        .into(binding.imageViewAddPet)//ㅁㄴㅇㅁㄴㅇ
                }
                .addOnCanceledListener {
                    // 업로드 취소 시
                }

        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // 뒤로 가기 버튼 동작을 비활성화
        // 아무 동작도 하지 않도록 설정
    }

    private fun goGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, PICK_IMAGE_FROM_ALBUM)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}

fun StorageReference.putFile(petProfileUri: String): UploadTask {
    val file = Uri.parse(petProfileUri) // 문자열 URI를 Uri 객체로 변환
    return putFile(file)
}

