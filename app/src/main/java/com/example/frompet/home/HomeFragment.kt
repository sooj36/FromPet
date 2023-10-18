package com.example.frompet.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import com.bumptech.glide.Glide.init
import com.example.frompet.chating.ChatUserDetailActivity
import com.example.frompet.home.adapter.HomeAdapter
import com.example.frompet.databinding.FragmentHomeBinding
import com.example.frompet.login.data.UserModel
import com.example.frompet.login.viewmodel.MatchViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction

class HomeFragment : Fragment() {

    companion object {
        const val MATCHED_USERS = "matchedUser"
        const val USER = "user"
        const val ACTION = "action"
        const val MATCH = "match"
        const val DISLIKE = "dislike"

    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var manager : CardStackLayoutManager
    private val viewModel: MatchViewModel by viewModels()
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val homeAdapter by lazy {
        HomeAdapter()
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        init()
        getDataFromFirestore()


        return binding.root
    }

    private fun init() {
        manager = CardStackLayoutManager(requireContext(), object : CardStackListener{
            override fun onCardDragging(direction: Direction?, ratio: Float) {

            }

            override fun onCardSwiped(direction: Direction?)  {
                when (direction) {
                    Direction.Left -> {
                        // 오른쪽으로 스와이프 (Like) 했을 때의 처리

                        val user  = homeAdapter.currentList[manager.topPosition-1]
                        user?.let {
                            // user를 이용하여 원하는 작업 수행
                            viewModel.like(user.uid)
                            Toast.makeText(requireContext(), "${user.petName}에게 좋아요를 보냈습니다", Toast.LENGTH_SHORT).show()
                        }

                    }
                    Direction.Right -> {
                        // 왼쪽으로 스와이프 (Dislike) 했을 때의 처리
                        Toast.makeText(requireContext(), "Disliked", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        // 다른 방향으로 스와이프한 경우
                    }
                }

                if (manager!!.topPosition == homeAdapter.currentList.size) {
                    // 이것이 마지막 카드인 경우 추가 처리 가능
                    Toast.makeText(requireContext(), "This is the last card", Toast.LENGTH_SHORT).show()
                }
            }


            override fun onCardRewound() {

            }

            override fun onCardCanceled() {

            }

            override fun onCardAppeared(view: View?, position: Int) {

            }

            override fun onCardDisappeared(view: View?, position: Int) {

            }

        })
        manager.setVisibleCount(3)
        manager.setTranslationInterval(0.6f)
        manager.setScaleInterval(0.8f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
        binding.cardStackView.layoutManager = manager
        binding.cardStackView.itemAnimator = DefaultItemAnimator()
        binding.cardStackView.adapter = homeAdapter
        Log.e("shshsh","Adapter set to cardStackCiew")

    }


    private fun getDataFromFirestore() {
        val allUsersData = mutableListOf<UserModel>()
        val currentUserId = auth.currentUser?.uid
        firestore.collection("User")
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    for (document in querySnapshot.documents) {
                        val user = document.toObject(UserModel::class.java)
                        user?.let {
                            if (it.uid != currentUserId) {
                                allUsersData.add(it)
                            }
                        }
                    }

                    homeAdapter.submitList(allUsersData)
                }
            }
            .addOnFailureListener { e ->
                Log.e("shsh", "Error getting documents: ", e)
            }
    }



    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
