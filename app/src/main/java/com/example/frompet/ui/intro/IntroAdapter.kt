package com.example.frompet.ui.intro

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.frompet.data.model.PageItem
import com.example.frompet.databinding.LayoutIntroPagerItemBinding

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

