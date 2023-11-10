package com.pet.frompet.ui.more

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pet.frompet.R
import com.pet.frompet.databinding.ActivityMoreBinding

class MoreActivity : AppCompatActivity() {

    private var _binding: ActivityMoreBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more)
    }
}