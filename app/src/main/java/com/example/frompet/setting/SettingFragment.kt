package com.example.frompet.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.example.frompet.databinding.FragmentSettingBinding
import com.example.frompet.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class SettingFragment : Fragment() {

    private var _binding : FragmentSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         _binding = FragmentSettingBinding.inflate(inflater,container,false)

        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val firestore = FirebaseFirestore.getInstance()
            val usersCollection = firestore.collection("User")
            val userId = currentUser.uid

            usersCollection.document(userId).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val petProfile = documentSnapshot.getString("petProfile")
                        val petName = documentSnapshot.getString("petName")
                        val petType = documentSnapshot.getString("petType")

                        petProfile?.let {
                            Glide.with(requireContext())
                                .load(it)
                                .into(binding.ivPet)
                        }
                        _binding?.tvPetName?.text = petName
                        _binding?.tvPetType?.text = petType
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Error fetching Firestore data", exception)
                }
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