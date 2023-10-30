package com.example.frompet.ui.home

//import FCMTokenManagerViewModel
import HomeBottomSheetFragment
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.example.frompet.databinding.FragmentHomeBinding
import com.example.frompet.MatchSharedViewModel
import com.example.frompet.R
import com.example.frompet.data.model.User
import com.example.frompet.ui.setting.fcm.FCMNotificationViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
    private val viewModel: MatchSharedViewModel by viewModels()
    private val FCMViewModel: FCMNotificationViewModel by viewModels()
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val homeAdapter by lazy {
        HomeAdapter(this@HomeFragment)
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
                            val currentUser = auth.currentUser
                            firestore.collection("User").document(currentUser?.uid!!).get().addOnSuccessListener { docs ->
                                val currentUserName = docs.getString("petName") ?:"nothing"
                                val title = "새로운 좋아요!"
                                val message = "${currentUserName}님이 당신을 좋아합니다."
                                FCMViewModel.sendFCMNotification(user.uid, title, message)
                            }
                            Toast.makeText(requireContext(), "${user.petName}에게 좋아요를 보냈습니다", Toast.LENGTH_SHORT).show()

                        }

                    }
                    Direction.Right -> {
                        // 왼쪽으로 스와이프 (Dislike) 했을 때의 처리
                        val user = homeAdapter.currentList[manager.topPosition -1]
                        user?.let {
                            viewModel.dislike(user.uid)
                            Toast.makeText(requireContext(), "${user.petName}(을)를 거절했습니다", Toast.LENGTH_SHORT).show()
                        }

                    }
                    Direction.Top -> {

                    }
                    else -> {
                        // 다른 방향으로 스와이프한 경우
                    }
                }

                if (manager!!.topPosition == homeAdapter.currentList.size) {
                    if (isResumed) {
                        Toast.makeText(requireContext(), "This is the last card", Toast.LENGTH_SHORT).show()
                        val transaction = parentFragmentManager.beginTransaction()
                        transaction.replace(R.id.home_container, HomeEmptyFragment())
                        transaction.addToBackStack(null)
                        transaction.commit()
                    }
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

        with(binding.cardStackView) {
            layoutManager = manager
            itemAnimator = DefaultItemAnimator()
            adapter = homeAdapter
            addOnChildAttachStateChangeListener(object : RecyclerView.OnChildAttachStateChangeListener {
                override fun onChildViewAttachedToWindow(view: View) {
                    val imageView: ImageView = view.findViewById(R.id.iv_pet_image)
                    imageView.setOnClickListener {
                        val currentPosition = manager.topPosition
                        if (currentPosition < homeAdapter.currentList.size) {
                            val user = homeAdapter.currentList[currentPosition]
                            user?.let {
                                showBottomSheet(it)
                            }
                        }
                    }
                }

                override fun onChildViewDetachedFromWindow(view: View) {
                    val imageView: ImageView = view.findViewById(R.id.iv_pet_image)
                    imageView.setOnClickListener(null)
                }
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
    private fun showBottomSheet(user: User) {

        val bottomSheetFragment = HomeBottomSheetFragment.newInstance(user)
        bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
    }




    private fun getDataFromFirestore() {
        viewModel.getExceptDislikeAndMe(
            onSuccess = { users ->
                if (users.isEmpty() && isResumed) {
                    homeAdapter.submitList(emptyList())
                    val transaction = parentFragmentManager.beginTransaction()
                    transaction.replace(R.id.home_container, HomeEmptyFragment())
                    transaction.addToBackStack(null)
                    transaction.commit()
                } else {
                    homeAdapter.submitList(users)
                }
            },
            onFailure = { e ->
                Log.e("shsh", "Error getting documents: ", e)
            }
        )
    }






    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
