package com.example.frompet.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frompet.data.model.UserLocation
import com.example.frompet.data.repository.map.MapRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class MapViewModel(val repository: MapRepository) : ViewModel() {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val _userLocation = MutableLiveData<UserLocation>()
    val userLocation : LiveData<UserLocation> get() =_userLocation

    private val database = Firebase.database
    private val locationRef = database.getReference("location")

    init {
        loadUserLocation()
    }

    private fun loadUserLocation() {
        locationRef.child(currentUserId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _userLocation.value = snapshot.getValue(UserLocation::class.java)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        }
    }