package com.example.frompet.ui.intro

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import com.example.frompet.R
import com.example.frompet.data.model.PageItem
import com.example.frompet.databinding.LayoutIntroPagerItemBinding

class PagerViewHolder(private val binding: LayoutIntroPagerItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bindWithView(pageItem: PageItem) {
        binding.pagerItemImage.setImageResource(pageItem.imageSrc)
        binding.pagerItemText.text = pageItem.content

        if (pageItem.bgColor != R.color.light_gray) {
            binding.pagerItemText.setTextColor(Color.BLACK)
        }

        binding.pagerItemBg.setBackgroundResource(pageItem.bgColor)
    }
}


