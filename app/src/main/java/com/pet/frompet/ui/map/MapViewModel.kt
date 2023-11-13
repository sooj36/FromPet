package com.pet.frompet.ui.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pet.frompet.data.model.UserLocation
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds

class MapViewModel() : ViewModel() {

    private val database = Firebase.database
    private val locationRef = database.getReference("location")

    private val _userLocationInfo = MutableLiveData<UserLocationInfo>()
    val userLocation : LiveData<UserLocationInfo> get() = _userLocationInfo

    // 상태 클래스 (상태패턴)
    data class UserLocationInfo(
        val userUid : List<String> = emptyList(),
        val userLocations : List<UserLocation> = emptyList()
    )


    fun getloadLocationData(bounds: LatLngBounds) {

        val userUids = mutableListOf<String>()
        val locationList = mutableListOf<UserLocation>()
        var userLocationInfo = UserLocationInfo()

        locationRef.get().addOnSuccessListener { snapshot ->

            for (locationSnapshot in snapshot.children) {
                val location =
                    locationSnapshot.getValue(com.pet.frompet.data.model.UserLocation::class.java)
                if (location != null && bounds.contains(
                        LatLng(
                            location.latitude,
                            location.longitude
                        )
                    )
                ) {
                    Log.d("LoadLocationData", "유저 아이디: ${locationSnapshot.key}")
                    // 지도 영역에 포함되는 위치만 처리
                    // null 방지 위해 orEmpty()
                    val userUid = locationSnapshot.key.orEmpty()
                    userUids.add(userUid)

                    // 위치 정보를 리스트에 추가
                    locationList.add(UserLocation(location.latitude, location.longitude))
                }
            }
            userLocationInfo = userLocationInfo.copy(userUids, locationList)
            _userLocationInfo.value = userLocationInfo
        }
    }
}