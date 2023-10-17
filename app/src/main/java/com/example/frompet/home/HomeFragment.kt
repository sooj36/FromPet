package com.example.frompet.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.frompet.databinding.FragmentHomeBinding
import com.example.frompet.login.viewmodel.MatchViewModel
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MatchViewModel by viewModels()
    private val auth = FirebaseAuth.getInstance()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)


        binding.likeBtn.setOnClickListener {

            viewModel.like("Td4QjX4JQ2Y0EzUVmkY2JIYz8ML2")
            Toast.makeText(requireContext(),"하트뿅뿅",Toast.LENGTH_LONG).show()
        }

        binding.dislikeBtn.setOnClickListener {  }

        return binding.root
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
