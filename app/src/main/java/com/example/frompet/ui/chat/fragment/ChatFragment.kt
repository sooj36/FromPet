package com.example.frompet.ui.chat.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.frompet.ui.chat.adapter.ViewPagerAdapter
import com.example.frompet.databinding.FragmentChatBinding

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val chatHomeFrag: ChatHomeFragment by lazy { ChatHomeFragment() }
    private val chatListFrag: ChatLikeListFragment by lazy { ChatLikeListFragment() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater, container, false)

        val fragments = listOf(chatHomeFrag, chatListFrag)
        val adapter = ViewPagerAdapter(this, fragments)

        binding.viewPager.adapter = adapter
        binding.viewPager.let { binding.dots.setViewPager2(it) }

        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
