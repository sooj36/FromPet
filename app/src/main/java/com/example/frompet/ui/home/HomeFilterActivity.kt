package com.example.frompet.ui.home

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import com.example.frompet.R
import com.example.frompet.data.model.Filter
import com.example.frompet.databinding.ActivityHomeFilterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore


class HomeFilterActivity : AppCompatActivity() {
    private var _binding: ActivityHomeFilterBinding? = null
    private val filterViewModel: HomeFilterViewModel by viewModels()
    private val database : FirebaseDatabase = FirebaseDatabase.getInstance()
    private val store :FirebaseFirestore = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser?.uid?:""
    private val binding get() = _binding!!
    companion object {
        const val USER = "user"
        const val FILTER_DATA ="filter_data"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivClose.setOnClickListener {
            finish()
        }

        val adapter =ArrayAdapter.createFromResource(this,R.array.pet_types,android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spPetType.adapter=adapter



        binding.btComplete.setOnClickListener {
            val selectedPetType = binding.spPetType.selectedItem.toString()
            val selectedGenderChipId = binding.chipGroup.checkedChipId
            val selectedGender: String? = when (selectedGenderChipId) {
                R.id.chip_all -> "all"
                R.id.chip_male -> "남"
                R.id.chip_female -> "여"
                else -> "all"
            }

            val filter = Filter(petType = selectedPetType, petGender = selectedGender)
            Log.d("filter","$selectedPetType,펫성별:$selectedGender")
            filterViewModel.filterUsers(filter)
            val result = Intent()
            result.putExtra(FILTER_DATA, filter)
            setResult(Activity.RESULT_OK,result)
            finish()
        }
    }
}
