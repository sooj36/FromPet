package com.example.frompet.ui.commnunity.communityhome

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.frompet.MatchSharedViewModel
import com.example.frompet.R
import com.example.frompet.data.model.CommunityHomeData
import com.example.frompet.databinding.FragmentCommunityhomeBinding



class CommunityHomeFragment : Fragment() {

    private var _binding: FragmentCommunityhomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter : CommunityHomeAdapter
    private lateinit var communityHomeData : MutableList<CommunityHomeData>
    private val viewModel : MatchSharedViewModel by viewModels()
    private val _viewModel by lazy {
        ViewModelProvider(
            this
        )[CategoryViewModel::class.java]
    }
    private val imageSliderAdapter: ImageSliderAdapter by lazy { ImageSliderAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         _binding = FragmentCommunityhomeBinding.inflate(inflater,container,false)


        val recyclerView = binding.communicationrecyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 4)

        // data list
        communityHomeData = mutableListOf(
            CommunityHomeData("dog", "강아지"),
            CommunityHomeData("cat", "고양이"),
            CommunityHomeData("raccoon", "라쿤"),
            CommunityHomeData("fox", "여우"),
            CommunityHomeData("chick", "새"),
            CommunityHomeData("pig", "돼지"),
            CommunityHomeData("snake", "파충류"),
            CommunityHomeData("fish", "물고기"),
        )

        //adapter
        val adapter = CommunityHomeAdapter(communityHomeData)
        recyclerView.adapter = adapter
        adapter.submitList(communityHomeData)


        binding.imageSlider.adapter = imageSliderAdapter

        viewModel.getTopMatchedUsersThisWeek { topUsers->
            _binding?.let { binding ->
                imageSliderAdapter.submitList(topUsers)
                binding.dotsIndicator.setViewPager2(binding.imageSlider)
                startAutoScroll()
            }
        }

        viewModel.getTotalMatchedCount{matchedCount->
            _binding?.tvNoticeText?.text = " 총 ${matchedCount}쌍이 매칭되었습니다!"
        }

        startNoticeTextAniMation()

        return binding.root
    }
    private fun startAutoScroll() {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                _binding?.let {binding->
                    val nextItem = (binding.imageSlider.currentItem + 1) % imageSliderAdapter.itemCount
                    binding.imageSlider.setCurrentItem(nextItem, true)
                    handler.postDelayed(this, 3000)
                }
            }
        }
        handler.postDelayed(runnable, 3000)
    }
private fun startNoticeTextAniMation(){
    val slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up)
    val slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down)

    slideUp.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) {}

        override fun onAnimationEnd(animation: Animation?) {
            _binding?.tvNoticeText?.startAnimation(slideDown)
        }

        override fun onAnimationRepeat(animation: Animation?) {}
    })

    slideDown.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) {}

        override fun onAnimationEnd(animation: Animation?) {
            Handler(Looper.getMainLooper()).postDelayed({
                _binding?.tvNoticeText?.startAnimation(slideUp)
            }, 1000)
        }

        override fun onAnimationRepeat(animation: Animation?) {}
    })

    _binding?.tvNoticeText?.startAnimation(slideUp)

}




    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}