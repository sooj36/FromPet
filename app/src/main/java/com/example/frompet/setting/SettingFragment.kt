package com.example.frompet.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.example.frompet.databinding.FragmentSettingBinding
import com.example.frompet.login.LoginActivity
import com.example.frompet.login.viewmodel.LoginViewModel
import com.example.frompet.login.viewmodel.MatchViewModel
import com.google.firebase.auth.FirebaseAuth

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)

        viewModel.loadUserPetProfile { petProfile, petName, petType ->
            petProfile?.let {
                Glide.with(this)
                    .load(it)
                    .into(binding.ivPet)
            }
            binding.tvPetName.text = petName
            binding.tvPetType.text = petType
        }
        binding.ibLogOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }
        binding.btModify.setOnClickListener {
            val intent = Intent(requireActivity(), ProfileActivity::class.java)
            startActivity(intent)
        }
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}