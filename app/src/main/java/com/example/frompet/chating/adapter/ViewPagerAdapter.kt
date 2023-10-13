package com.example.frompet.chating.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.frompet.chating.ChatFragment
import com.example.frompet.chating.ChatMessageFragment
import com.example.frompet.chating.ChatListFragment

class ViewPagerAdapter(private val fragmentActivity: ChatFragment, private val fragments:List<Fragment>):FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
       return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
       return when(position){
           0 ->return ChatMessageFragment()
           1 ->return ChatListFragment()
           else ->return ChatMessageFragment()
       }
    }

}