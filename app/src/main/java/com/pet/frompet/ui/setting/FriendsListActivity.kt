package com.pet.frompet.ui.setting
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pet.frompet.MatchSharedViewModel
import com.pet.frompet.databinding.ActivityFriendsListBinding
import com.pet.frompet.ui.chat.activity.ChatClickUserDetailActivity

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
        adapter = FriendsListAdapter(this) { user, imageView ->
            val intent = Intent(this, ChatClickUserDetailActivity::class.java)
            intent.putExtra(ChatClickUserDetailActivity.USER, user)
            val options: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                androidx.core.util.Pair(imageView, "imageTransition")
            )

            startActivity(intent, options.toBundle())
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
