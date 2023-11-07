package com.example.frompet.ui.commnunity.communityhome

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
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
import com.example.frompet.data.model.CommunityData
import com.example.frompet.data.model.CommunityHomeData
import com.example.frompet.data.model.toCommunityData
import com.example.frompet.databinding.FragmentCommunityhomeBinding
import com.example.frompet.ui.commnunity.CategorySharedViewModel
import com.example.frompet.ui.commnunity.community.CommunityActivity

class CommunityHomeFragment : Fragment() {

    private var _binding: FragmentCommunityhomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MatchSharedViewModel by viewModels()
    private val sharedViewModel: CategorySharedViewModel by viewModels()
    private val _viewModel by lazy {
        ViewModelProvider(
            this,
            CategoryViewModelFactory(requireContext())
        )[CategoryViewModel::class.java]
    }
    private val imageSliderAdapter: ImageSliderAdapter by lazy { ImageSliderAdapter() }

    private val communityHomeAdapter by lazy {
        CommunityHomeAdapter(onClicked = { item ->
            toCategory(item)
            _viewModel.listClickCategory(item.petType)
            Log.e("sshHome", item.petType)
            Log.e("sshHome", item.toString())

        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommunityhomeBinding.inflate(inflater, container, false)

        binding.imageSlider.adapter = imageSliderAdapter

        viewModel.getTopMatchedUsersThisWeek { topUsers ->
            _binding?.let { binding ->
                imageSliderAdapter.submitList(topUsers)
                binding.dotsIndicator.setViewPager2(binding.imageSlider)
                startAutoScroll()
            }
        }
        _viewModel.selectPetType.observe(viewLifecycleOwner) { selectPetType ->
            if (!selectPetType.isNullOrEmpty()) {
                // 카테고리 변경 시 카테고리 데이터를 다시 로드
                _viewModel.getHomeCategory()
                Log.e("sshHome", "$selectPetType")
            }
        }

        viewModel.getTotalMatchedCount { matchedCount ->
            _binding?.tvNoticeText?.text = " 총 ${matchedCount}쌍이 매칭되었습니다!"
        }

        startNoticeTextAniMation()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
    }


    private fun initView() = with(binding) {
        categoryBt.layoutManager = GridLayoutManager(requireContext(), 4)
        categoryBt.adapter = communityHomeAdapter
        Log.e("sshHome", "Adapter set with ${communityHomeAdapter.itemCount} items")
        _viewModel.commuHomeDataList.observe(viewLifecycleOwner) { CateHomeList ->
            Log.d("sshHome", "CategoryList 변경됨")
            communityHomeAdapter.submitList(CateHomeList)
            Log.e("sshHome인데?", "$CateHomeList")
        }
        _viewModel.getHomeCategory()
        Log.e("ssh10", "${communityHomeAdapter}")

    }

    private fun toCategory(item: CommunityHomeData) = with(_viewModel) {
        val selectedData = _viewModel.getHomeCategory()
        selectedData?.let {
            val selectedCommunityDataList = clickedCategoryData
            selectedCommunityDataList.observe(viewLifecycleOwner) { communityData ->
                _viewModel.onCategoryClicked(communityData)
            }
            sharedViewModel.selectPetCategory(item.toCommunityData())
            Log.e("sshHome", "$item.toCommunityData()")
            val intent = Intent(requireContext(), CommunityActivity::class.java)
            intent.putExtra(CommunityActivity.EXTRA_PET_TYPE, item.petType)
            intent.putExtra(CommunityActivity.EXTRA_DATA,item.toCommunityData())
            startActivity(intent)
        } ?: run {
            Log.e("sshHome", "Selected Data is null")
        }
        _viewModel.listClickCategory(item.petType)
    }

    private fun initViewModel() {
        Log.e("sshHome", "initViewModel function called")
        with(_viewModel) {
            event.observe(viewLifecycleOwner) { event ->
                when (event) {
                    is CategoryClick.PetCategory -> {
                        Log.e("sshHome", "Event observed: $event")
                        // 클릭 이벤트 처리
                        val selectedCommunityData = communityList.value?.firstOrNull()
                        if (selectedCommunityData != null) {
                            Log.e("sshHome", "Selected Community Data: $selectedCommunityData")
                            Intent(requireContext(), CommunityActivity::class.java).apply {
                                putExtra(CommunityActivity.EXTRA_PET_TYPE, selectedCommunityData)
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }.run { requireContext().startActivity(this) }
                        }
                    }
                }
            }
        }
    }

    fun onCategoryClicked(data: CommunityData) {
        _viewModel.onCategoryClicked(data)
    }

    private fun startAutoScroll() {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                _binding?.let { binding ->
                    val itemCount = imageSliderAdapter.itemCount
                    if (itemCount > 0) {
                        val nextItem = (binding.imageSlider.currentItem + 1) % itemCount
                        binding.imageSlider.setCurrentItem(nextItem, true)
                        handler.postDelayed(this, 3000)
                    }
                }
            }
        }
        handler.postDelayed(runnable, 3000)
    }


    private fun startNoticeTextAniMation() {
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
