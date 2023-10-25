package com.example.frompet.ui.commnunity.community

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

    // FirebaseStorage 초기화
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCommunityBinding.inflate(layoutInflater)

        setContentView(binding?.root)

        // RecyclerView 설정
        val recyclerView = binding?.recyclerview
        recyclerView?.layoutManager = LinearLayoutManager(this)
        // adapter 초기화
        val adapter = CommunityAdapter { CommunityData ->
            val intent : Intent = Intent(this, CommunityDetailActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Firebase 현재 사용자 가져오기
        val currentUser = FirebaseAuth.getInstance().currentUser

        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.adapter = adapter

        // 데이터 가져오기
        val communitydb = FirebaseFirestore.getInstance()
        // Firebase 현재 사용자 가져오기
        val currentUserId = auth.currentUser?.uid

        communitydb.collection("Community")
            .get()
            .addOnSuccessListener { documents ->
                val communityList = mutableListOf<CommunityData>()

                for (document in documents) {
                    val data = document.toObject(CommunityData::class.java)
                    communityList.add(data)
                }
                adapter.submitList(communityList)

            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "실패", Toast.LENGTH_SHORT).show()
            }


        binding?.backBtn?.setOnClickListener {
            finish()
        }

        binding?.ivPen?.setOnClickListener {
            val intent: Intent = Intent(this@CommunityActivity, CommunityAddActivity::class.java)
            startActivity(intent)
        }


    }
}