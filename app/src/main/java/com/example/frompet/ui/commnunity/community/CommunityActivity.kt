package com.example.frompet.ui.commnunity.community

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.frompet.R
import com.example.frompet.databinding.ActivityCommunityBinding
import com.example.frompet.ui.commnunity.communityHome.CommunityHomeFragment

class CommunityActivity : AppCompatActivity() {

    private var _binding : ActivityCommunityBinding? = null
    private val binding get() = _binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCommunityBinding.inflate(layoutInflater)

        setContentView(binding?.root)

        val recyclerView = binding?.recyclerview
        recyclerView?.layoutManager = LinearLayoutManager(this)

        binding?.backBtn?.setOnClickListener {
            val fragment = CommunityHomeFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.communicationrecyclerView, fragment)
                .addToBackStack(null)
                .commit()
        }


    }
}