package com.pet.frompet.ui.intro

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pet.frompet.data.model.PageItem
import com.pet.frompet.databinding.LayoutIntroPagerItemBinding

class IntroAdapter(private var pageList: ArrayList<PageItem>):
    RecyclerView.Adapter<PagerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        return PagerViewHolder(LayoutIntroPagerItemBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        ))
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.bindWithView(pageList[position])
    }

    override fun getItemCount(): Int {
        return pageList.size
    }


}

