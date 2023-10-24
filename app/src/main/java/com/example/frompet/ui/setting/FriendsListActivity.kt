package com.example.frompet.ui.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.frompet.MatchSharedViewModel
import com.example.frompet.R
import com.example.frompet.data.model.User
import com.example.frompet.databinding.ActivityFriendsListBinding
class FriendsListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFriendsListBinding // 바인딩 변수 선언

    private val matchSharedViewModel: MatchSharedViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FriendsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.rvFriends
        adapter = FriendsListAdapter(this)

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
