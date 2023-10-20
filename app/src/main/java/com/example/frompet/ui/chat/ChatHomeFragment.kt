package com.example.frompet.ui.chat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.frompet.MatchSharedViewModel
import com.example.frompet.ui.chat.adapter.ChatHomeAdapter
import com.example.frompet.databinding.FragmentChatHomeBinding

class ChatHomeFragment : Fragment() {

    private var _binding: FragmentChatHomeBinding? = null
    private lateinit var  adapter: ChatHomeAdapter
    private val binding get() = _binding

    private val matchSharedViewModel: MatchSharedViewModel by activityViewModels()
    private val chatViewModel: ChatViewModel by viewModels()
    private val chatMessageActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val chatRoomId = result.data?.getStringExtra("chatRoomId")
                chatRoomId?.let {
                    chatViewModel.goneNewMessages(it)
                }
            }
        }

    companion object {
        const val USER = "user"
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ChatHomeAdapter(requireContext(), chatViewModel, viewLifecycleOwner).apply {
            onChatItemClick = { user ->
                val intent = Intent(requireContext(), ChatMessageActivity::class.java)
                intent.putExtra(USER, user)
                chatMessageActivityResult.launch(intent)
            }
        }

        binding?.apply {
            rvChatHome.adapter = adapter
            rvChatHome.layoutManager = LinearLayoutManager(context)
        }
        matchSharedViewModel.loadMatchedUsers()
        matchSharedViewModel.matchedList.observe(viewLifecycleOwner) { users ->
           chatViewModel.getlastTimeSorted(users){
                adapter.submitList(it)
                binding?.tvPossibleText?.text = "${it.size}명과 대화가 가능해요"
            }
        }
        chatViewModel.loadNewChats()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
