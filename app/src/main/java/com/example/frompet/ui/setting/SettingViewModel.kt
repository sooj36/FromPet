package com.example.frompet.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SettingViewModel : ViewModel() {
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val firestore = FirebaseFirestore.getInstance()

    private val _petProfile = MutableLiveData<String?>()
    val petProfile: LiveData<String?> = _petProfile

    private val _petName = MutableLiveData<String?>()
    val petName: LiveData<String?> = _petName

    private val _petType = MutableLiveData<String?>()
    val petType: LiveData<String?> = _petType

    private val _petGender = MutableLiveData<String?>()
    val petGender: LiveData<String?> = _petGender

    private val _petAge = MutableLiveData<Int?>()
    val petAge: LiveData<Int?> = _petAge

    private val _petIntroduction = MutableLiveData<String?>()
    val petIntroduction: LiveData<String?> = _petIntroduction

    private val _petDescription = MutableLiveData<String?>()
    val petDescription: LiveData<String?> = _petDescription

    private val _petNeuter = MutableLiveData<String?>()
    val petNeuter: LiveData<String?> = _petNeuter

    fun loadUserPetProfile() {
        val userId = currentUser?.uid

        if (userId != null) {
            firestore.collection("User").document(userId).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        _petProfile.value = documentSnapshot.getString("petProfile")
                        _petName.value = documentSnapshot.getString("petName")
                        _petType.value = documentSnapshot.getString("petType")
                        _petGender.value = documentSnapshot.getString("petGender")
                        _petAge.value = documentSnapshot.getLong("petAge")?.toInt()
                        _petIntroduction.value = documentSnapshot.getString("petIntroduction")
                        _petDescription.value = documentSnapshot.getString("petDescription")
                        _petNeuter.value = documentSnapshot.getString("petNeuter")
                    }
                }
        }
    }
}
