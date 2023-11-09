package com.pet.frompet.ui.commnunity.communitydetail

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import coil.load
import com.pet.frompet.R
import com.pet.frompet.data.model.CommunityData
import com.pet.frompet.databinding.ActivityCommunityDetailUpdateBinding
import com.pet.frompet.util.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

class CommunityDetailUpdateActivity : AppCompatActivity() {

    private var _binding: ActivityCommunityDetailUpdateBinding? = null
    private val binding get() = _binding!!

    private val currentUid = FirebaseAuth.getInstance().currentUser?.uid
    private val store = FirebaseFirestore.getInstance()
    private var communityData: CommunityData? = null
    private val storageReference: StorageReference by lazy {
        FirebaseStorage.getInstance().reference.child("images")
    }
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val newFilePath = result.data?.data
            newFilePath?.let {
                uploadImage(it)
            }
        }
    }
    companion object {
        const val COMMUNITY_DATA = "communityData"
        const val DOCS_ID = "docsId"
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCommunityDetailUpdateBinding.inflate(layoutInflater)

        setContentView(binding.root)

        communityData = intent.getParcelableExtra(CommunityDetailActivity.DOCS_ID)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            communityData = intent.getParcelableExtra(
                CommunityDetailActivity.COMMUNITY_DATA,
                CommunityData::class.java
            )
        } else {
            communityData =
                intent.extras?.getParcelable(CommunityDetailActivity.COMMUNITY_DATA) as CommunityData?
        }
        Log.d("sooj", "데이터 ${communityData}")

        val delete = binding.ibImageDelete
        val image = binding.ivGoGalley
        val iamgeUpload = binding.ivUploadImage

        communityData?.let { data ->
            if (data.imageUrl.isNullOrEmpty()) {
                image.isVisible = false
                delete.isVisible = false
            } else {
                image.isVisible = true
                delete.isVisible = true
                image.load(data.imageUrl) {
                    error(R.drawable.kakaotalk_20230825_222509794_01)
                }
            }
        }

        delete.setOnClickListener {
            communityData?.imageUrl?.let { imageUrl ->
                deleteImageStorage(imageUrl)
            }
        }

        iamgeUpload.setOnClickListener {
            openGallery()
        }

        with(binding) {

            updateTitle.setText(communityData?.title)
            updateContents.setText(communityData?.contents)

            backBtn.setOnClickListener {
                finish()
            }

            when (communityData?.tag) {
                "나눔" -> chipGroup.check(R.id.chip_share)
                "산책" -> chipGroup.check(R.id.chip_walk)
                "사랑" -> chipGroup.check(R.id.chip_love)
                "정보교환" -> chipGroup.check(R.id.chip_exchange)
            }

            chipGroup.setOnCheckedChangeListener { group, checkedId ->

            }
            btnDone.setOnClickListener {
                updateCommunity(communityData?.docsId)

            }
        }
    }
    private fun uploadImage(fileUri: Uri) {
        val fileName = UUID.randomUUID().toString()
        val fileRef = storageReference.child("$fileName.jpg")

        fileRef.putFile(fileUri)
            .addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    binding.ivGoGalley.setImageURI(fileUri)
                    showToast("이미지 업로드에 성공!", Toast.LENGTH_SHORT)
                    communityData?.imageUrl = uri.toString()
                    updateImageUrlStorage(uri.toString())
                }
            }
            .addOnFailureListener {
                showToast("이미지업로드 실패: ${it.message}", Toast.LENGTH_SHORT)
            }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        pickImageLauncher.launch(intent)
    }
    private fun deleteImageStorage(imageUrl: String) {
        val photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
        photoRef.delete().addOnSuccessListener {

            showToast("이미지 삭제 성공", Toast.LENGTH_SHORT)
            updateImageUrlStorage(null)
        }.addOnFailureListener {
            showToast("이미지 삭제 실패: 권한이 없습니다.", Toast.LENGTH_SHORT)
        }
    }

    private fun updateImageUrlStorage(newImageUrl: String?) {
        communityData?.docsId?.let { docsId ->
            store.collection("Community")
                .document(docsId)
                .update("imageUrl", newImageUrl)
                .addOnSuccessListener {

                    if (newImageUrl.isNullOrEmpty()) {
                        binding.ivGoGalley.isVisible = false
                        binding.ibImageDelete.isVisible = false
                    }else{
                        binding.ivGoGalley.isVisible = true
                        binding.ibImageDelete.isVisible = true
                    }
                }
                .addOnFailureListener {
                    showToast("이미지 정보 업데이트 실패: ${it.message}", Toast.LENGTH_SHORT)
                }
        }
    }




    private fun updateCommunity(docsId: String?) {
        if (docsId != null) { // 널이면 add
            store.collection("Community")
                .document(docsId)
                .update(
                    "title",
                    binding.updateTitle.text.toString(),
                    "contents",
                    binding.updateContents.text.toString(),
                    "tag",
                    when (binding.chipGroup.checkedChipId) {
                        R.id.chip_share -> "나눔"
                        R.id.chip_walk -> "산책"
                        R.id.chip_love -> "사랑"
                        R.id.chip_exchange -> "정보교환"
                        else -> ""
                    }


                )
                .addOnSuccessListener {
                    showToast("게시글이 수정되었습니다", Toast.LENGTH_SHORT)
                    finish()
                }
                .addOnFailureListener {
                    showToast("게시글 수정 권한이 없습니다", Toast.LENGTH_SHORT)
                }
        }
    }
}