package com.example.frompet.ui.chat.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.frompet.ui.chat.fragment.ChatFragment
import com.example.frompet.ui.chat.fragment.ChatHomeFragment
import com.example.frompet.ui.chat.fragment.ChatLikeListFragment

class ViewPagerAdapter(private val fragmentActivity: ChatFragment, private val fragments:List<Fragment>):FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
       return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
       return when(position){
           0 ->return ChatHomeFragment()
           1 ->return ChatLikeListFragment()
           else ->return ChatHomeFragment()
       }
    }

}