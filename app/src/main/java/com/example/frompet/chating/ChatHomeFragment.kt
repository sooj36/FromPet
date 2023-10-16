package com.example.frompet.chating

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.frompet.R
import com.example.frompet.chating.adapter.ChatHomeAdapter
import com.example.frompet.databinding.FragmentChatHomeBinding
import com.example.frompet.login.viewmodel.MatchViewModel

class ChatHomeFragment : Fragment() {

    private var _binding: FragmentChatHomeBinding? = null
    private val adapter : ChatHomeAdapter by lazy { binding.rvChatHome.adapter as ChatHomeAdapter }
    private val binding get() = _binding!!
    private val viewModel: MatchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            rvChatHome.adapter = ChatHomeAdapter(requireContext())
            rvChatHome.layoutManager = LinearLayoutManager(context)
        }

        viewModel.loadMatchedUsers()
        viewModel.matchedList.observe(viewLifecycleOwner) { users ->
            users?.let {
                adapter.submitList(it)
                binding.tvPossibleText.text = getString(R.string.possible_text,it.size)

            }
        }
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
