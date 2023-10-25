package com.example.frompet.ui.setting
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.frompet.MatchSharedViewModel
import com.example.frompet.databinding.ActivityFriendsListBinding
import com.example.frompet.ui.chat.activity.ChatClickUserDetailActivity

class FriendsListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFriendsListBinding

    private val matchSharedViewModel: MatchSharedViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FriendsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.rvFriends
        adapter = FriendsListAdapter(this) { user ->
            val intent = Intent(this, ChatClickUserDetailActivity::class.java)
            intent.putExtra(ChatClickUserDetailActivity.USER, user)
            startActivity(intent)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        matchSharedViewModel.matchedList.observe(this) { users ->
            adapter.submitList(users)
        }
        matchSharedViewModel.loadMatchedUsers()

        binding.btBack.setOnClickListener {
            onBackPressed()
        }
    }
}
