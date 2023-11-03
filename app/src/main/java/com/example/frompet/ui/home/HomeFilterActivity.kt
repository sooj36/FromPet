package com.example.frompet.ui.home

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewTreeObserver
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import com.example.frompet.R
import com.example.frompet.data.model.Filter
import com.example.frompet.databinding.ActivityHomeFilterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore


class HomeFilterActivity : AppCompatActivity() {
    private var _binding: ActivityHomeFilterBinding? = null
    private val binding get() = _binding!!

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    companion object {
        const val FILTER_DATA = "filter_data"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.chipGroup.check(R.id.chip_all)

        setupCloseButton()
        setupPetTypeSpinner()
        restoreFilterOptions()

        binding.btComplete.setOnClickListener {
            val selectedPetType = binding.spPetType.selectedItem.toString()
            val selectedGender = getSelectedGender()
            val filter = Filter(petType = selectedPetType, petGender = selectedGender)

            saveFilterOptions(selectedPetType, selectedGender)
            returnFilterResult(filter)
        }
    }

    private fun setupCloseButton() {
        binding.ivClose.setOnClickListener {
            finish()
        }
    }

    private fun setupPetTypeSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.pet_types,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spPetType.adapter = adapter
        val petTypes = resources.getStringArray(R.array.pet_types)
        val defaultPosition = petTypes.indexOf("전체")
        binding.spPetType.setSelection(defaultPosition)
        binding.spPetType.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // 스피너의 넓이
                val spinnerWidth = binding.spPetType.width
                // 드롭다운 리스트의 넓이를 스피너의 넓이로 설정
                binding.spPetType.dropDownWidth = spinnerWidth
                // 레이아웃 리스너를 제거(최종넓이를 얻기위해서 호출)
                binding.spPetType.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }


    private fun getSelectedGender(): String? {
        return when (binding.chipGroup.checkedChipId) {
            R.id.chip_male -> "남"
            R.id.chip_female -> "여"
            else -> "all"
        }
    }

    private fun saveFilterOptions(petType: String, petGender: String?) {
        val filterOptions = mapOf("petType" to petType, "petGender" to petGender)
        database.reference.child("userSaveFilter").child(currentUserUid).setValue(filterOptions)
    }

    private fun restoreFilterOptions() {
        database.reference.child("userSaveFilter").child(currentUserUid).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val petType = snapshot.child("petType").getValue(String::class.java) ?: "전체"
                val petGender = snapshot.child("petGender").getValue(String::class.java) ?: "all"
                val petTypePosition = resources.getStringArray(R.array.pet_types).indexOf(petType)

                binding.spPetType.setSelection(petTypePosition)

                val genderChipId = when (petGender) {
                    "남" -> R.id.chip_male
                    "여" -> R.id.chip_female
                    else -> R.id.chip_all
                }
                binding.chipGroup.check(genderChipId)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun returnFilterResult(filter: Filter) {
        val result = Intent().apply {
            putExtra(FILTER_DATA, filter)
        }
        setResult(Activity.RESULT_OK, result)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
