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
        return binding.root
    }

    private fun init() {
        manager = CardStackLayoutManager(requireContext(), object : CardStackListener{
            override fun onCardDragging(direction: Direction?, ratio: Float) {

            }

            override fun onCardSwiped(direction: Direction?) {
                if(manager!!.topPosition == homeAdapter.currentList.size){
                    Toast.makeText(requireContext(),"this is last card", Toast.LENGTH_SHORT).show()
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
        getDataFromFirestore()
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
