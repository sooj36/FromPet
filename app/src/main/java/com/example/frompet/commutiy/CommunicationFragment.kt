package com.example.frompet.commutiy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.frompet.R
import com.example.frompet.databinding.FragmentChatHomeBinding
import com.example.frompet.databinding.FragmentCommunicationBinding
import com.example.frompet.databinding.FragmentHomeBinding


class CommunicationFragment : Fragment() {

    private var _binding: FragmentCommunicationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         _binding = FragmentCommunicationBinding.inflate(inflater,container,false)

        return binding?.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}