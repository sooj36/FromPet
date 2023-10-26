package com.example.frompet.ui.commnunity.community

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.frompet.data.model.CommunityData
import com.example.frompet.databinding.ActivityCommunityBinding
import com.example.frompet.ui.commnunity.communityadd.CommunityAddActivity
import com.example.frompet.ui.commnunity.communitydetail.CommunityDetailActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class CommunityActivity : AppCompatActivity() {

    private var _binding: ActivityCommunityBinding? = null
    private val binding get() = _binding
    private val auth = FirebaseAuth.getInstance()
    private val communityAdapter : CommunityAdapter by lazy { CommunityAdapter(
        ListClick = {item ->
            //전달하는 데이터
            val intent: Intent = Intent(this, CommunityDetailActivity::class.java)
            Log.d("sooj", "item == ${item}")
            intent.putExtra("communityData", item)
            startActivity(intent)
            finish()
        }
    ) }

    // FirebaseStorage 초기화
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCommunityBinding.inflate(layoutInflater)

        setContentView(binding?.root)

        binding?.recyclerview?.adapter = communityAdapter


        // Firebase 현재 사용자 가져오기
        val currentUser = FirebaseAuth.getInstance().currentUser


        // 데이터 가져오기
        val communitydb = FirebaseFirestore.getInstance()
        // Firebase 현재 사용자 가져오기
        val currentUserId = auth.currentUser?.uid

        communitydb.collection("Community")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val communityList = mutableListOf<CommunityData>()

                if (querySnapshot.isEmpty.not()) {
                    for (document in querySnapshot.documents) {
                        val data = document.toObject(CommunityData::class.java)
                        data?.let {
                                communityList.add(it)
                        }
                    }
                    communityAdapter.submitList(communityList)
                    Log.d("sooj", "커뮤니티 리스트${communityList}")

                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "실패", Toast.LENGTH_SHORT).show()
            }


        binding?.backBtn?.setOnClickListener {
            finish()
        }

        binding?.ivPen?.setOnClickListener {
            val intent: Intent =
                Intent(this@CommunityActivity, CommunityAddActivity::class.java)
            startActivity(intent)
        }
    }
}