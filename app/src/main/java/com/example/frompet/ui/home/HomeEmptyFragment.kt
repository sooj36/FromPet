package com.example.frompet.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.frompet.R
import com.example.frompet.databinding.FragmentHomeEmptyBinding

class HomeEmptyFragment : Fragment() {
    private var _binding: FragmentHomeEmptyBinding? = null

    private val binding get() = _binding!!




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeEmptyBinding.inflate(inflater,container,false)


        return binding.root
    }


}