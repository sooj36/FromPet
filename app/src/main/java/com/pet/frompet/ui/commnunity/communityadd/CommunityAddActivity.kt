package com.pet.frompet.ui.commnunity.communityadd

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintSet
import com.pet.frompet.R
import com.pet.frompet.data.model.CommunityData
import com.pet.frompet.data.model.User
import com.pet.frompet.databinding.ActivityCommunityAddBinding
import com.pet.frompet.ui.commnunity.AddExitDialog
import com.pet.frompet.util.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

class CommunityAddActivity : AppCompatActivity() {

    private var _binding: ActivityCommunityAddBinding? = null
    private val binding get() = _binding!!
    private var filePath: Uri? = null

    // FirebaseStorage 초기화
    private val firestore = FirebaseFirestore.getInstance()
    val currentUid = FirebaseAuth.getInstance().currentUser?.uid?:""
    val userRef = firestore.collection("User").document(currentUid)
    private val storageReference: StorageReference by lazy {
        FirebaseStorage.getInstance().reference.child("images")
    }

    // data class
    private var title: String = "" // 제목
    private var tag: String = "" // 카테고리
    private var timeStamp: String = "" // 시간
    private var contents: String = "" // 내용
    private var docsId: String? = null // 문서id
    private var user: User? = null
    private var imageUrl: String? = null
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            filePath = result.data?.data
            filePath?.let {
                uploadImage(it)
            }
        }
    }

    companion object {
        const val DOCS_ID = "docsId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCommunityAddBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initView()
        binding.ivUploadImage.setOnClickListener {
            openGallery()
        }


        binding.chipGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.chip_share -> tag = "나눔"
                R.id.chip_walk -> tag = "산책"
                R.id.chip_love -> tag = "사랑"
                R.id.chip_exchange -> tag = "정보교환"
            }
        }

        binding.btnAddEnroll.setOnClickListener {
            with(binding) {
                title = etAddTitle.text.toString()
                contents = etAddContents.text.toString()
            }

            if (title.isNotEmpty() && contents.isNotEmpty() && tag.isNotEmpty()) {
                // Firebase 현재 사용자 가져오기
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null) {
                    saveCommunityData()
                }
            } else {
                showToast("항목을 모두 기입해주세요", Toast.LENGTH_SHORT)
            }
        }
    }
    private fun initView() {
        with(binding) {
            btnAddCancel.setOnClickListener { showExitDialog() }
            backBtn.setOnClickListener { backToCommunity() }

        }
    }
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        pickImageLauncher.launch(intent)
    }
    private fun uploadImage(fileUri: Uri) {
        val fileName = UUID.randomUUID().toString()
        val fileRef = storageReference.child("$fileName.jpg")

        fileRef.putFile(fileUri)
            .addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    binding.ivGoGalley.setImageURI(fileUri)
                    showToast("이미지 업로드에 성공!", Toast.LENGTH_SHORT)
                    updateImageVisibility(true)
                    imageUrl = uri.toString()
                }
            }
            .addOnFailureListener {
                showToast("이미지업로드 실패: ${it.message}", Toast.LENGTH_SHORT)
                updateImageVisibility(false)
            }
    }
    private fun updateImageVisibility(isVisible: Boolean) {
        binding.ivGoGalley.visibility = if (isVisible) View.VISIBLE else View.GONE
    }


    private fun saveCommunityData() {
             userRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val petType = documentSnapshot.getString("petType") ?: return@addOnSuccessListener
                    val community = CommunityData(
                        title = title,
                        tag = tag,
                        contents = contents,
                        timestamp = System.currentTimeMillis(),
                        uid = currentUid,
                        docsId = docsId,
                        petType = petType,
                        imageUrl = imageUrl
                    )

                    val communityCollection = firestore.collection("Community")
                    if (docsId == null) {

                        communityCollection.add(community)
                            .addOnSuccessListener { documentReference ->
                                community.docsId = documentReference.id
                                documentReference.set(community)
                                    .addOnSuccessListener {
                                        showToast("게시글이 등록되었습니다", Toast.LENGTH_SHORT)
                                        val dataIntent = Intent()
                                        setResult(Activity.RESULT_OK, dataIntent)
                                        finish()
                                    }
                                    .addOnFailureListener {
                                        showToast("게시글 업데이트에 실패하였습니다", Toast.LENGTH_SHORT)
                                    }
                            }
                            .addOnFailureListener {
                                showToast("게시글을 작성하는데 실패하였습니다", Toast.LENGTH_SHORT)
                            }
                    } else {
                        communityCollection.document(docsId!!)
                            .set(community)
                            .addOnSuccessListener {
                                showToast("게시글이 업데이트되었습니다", Toast.LENGTH_SHORT)
                                val dataIntent = Intent()
                                setResult(Activity.RESULT_OK, dataIntent)
                                finish()
                            }
                            .addOnFailureListener {
                                showToast("게시글 업데이트에 실패하였습니다", Toast.LENGTH_SHORT)
                            }
                    }
                } else {
                    showToast("유저 컬렉션이 존재하지 않습니다.", Toast.LENGTH_SHORT)
                }
            }
            .addOnFailureListener {
                showToast("데이터 가져오기 실패 : ${it.message}", Toast.LENGTH_SHORT)
            }

        updateImageVisibility(!imageUrl.isNullOrEmpty())

    }




    private fun backToCommunity() {
        finish()
    }

    private fun showExitDialog() {
        AddExitDialog(this).showExitDialog {
            finish()
        }
    }


}