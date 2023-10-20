package com.example.frompet.ui.map

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.frompet.R
import com.example.frompet.data.model.User
import com.example.frompet.data.model.UserLocation
import com.example.frompet.databinding.FragmentMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons

class NaverMapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val marker = Marker()

    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private lateinit var binding: FragmentMapBinding

    private val LOCATION_PERMISSION_REQUEST_CODE = 1000

    private val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true) {
                initMapView()
            }
        }

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: "" // 현재 uid 갖고 옴

    // onCreateView 에서 권한 확인하며 위치 권한 없을 시, 사용자에게 권한 요청
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!hasPermission()) {
            requestMultiplePermissions.launch(PERMISSIONS)
        } else {
            initMapView()
        }

    }

    private fun initMapView() {
        val fm = childFragmentManager
        val mapFragment = fm?.findFragmentById(R.id.naver_map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.naver_map_fragment, it).commit()

            }

        // fragment의 getMapAsync() 메서드로 OnMapReadyCallBack 콜백을 등록하면, 비동기로 NaverMap 객체를 얻을 수 있음
        mapFragment.getMapAsync(this)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }

    //hasPermission()에서는 위치 권한 있 -> true , 없 -> false
    private fun hasPermission(): Boolean {
        for (permission in PERMISSIONS) {
            if (activity?.let { ContextCompat.checkSelfPermission(it, permission) }
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        // 현 위치
        naverMap.locationSource = locationSource
        // 현 위치 버튼 기능
        naverMap.uiSettings.isLocationButtonEnabled = true
        // 위치를 추적하면서 카메라도 같이 움직임
        naverMap.locationTrackingMode = LocationTrackingMode.Follow
        Log.d("sooj", "onmapready")

        // 줌
        naverMap.maxZoom = 20.0  // (최대 21)
        naverMap.minZoom = 5.0

        // Firebase
        val database = Firebase.database
        val locationRef = database.getReference("location")

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireContext()) // 초기화
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling // 위치권한 없을 시
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                // 사용자 현재 위치 파베에 업로드
                val userLocation = UserLocation(location.latitude, location.longitude) // 사용자 위도 경도
                location?.let {
                    UserLocation(latitude = location.latitude, longitude = it.longitude)
                    locationRef.child(currentUserId).setValue(userLocation)
                    Log.d(
                        "sooj",
                        "${UserLocation(latitude = location.latitude, longitude = it.longitude)}"
                    )
                    Log.d("sooj", "${locationRef.child(currentUserId).setValue(userLocation)}")
                }
            }
        }
        locationRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snapshots in snapshot.children) {
                    val location = snapshots.getValue(UserLocation::class.java)
                    val userUid = snapshots.key // 사용자 uid

                    // 다른 사용자 위치 마커 표시
                    if (location != null && userUid != null && userUid != currentUserId) {
                        setMark(marker, location)
                    }

                }
            }


            override fun onCancelled(error: DatabaseError) {}
        })
    }


    private fun LatLng() { //경도 위도
        naverMap.addOnLocationChangeListener { location ->
            Toast.makeText(
                requireContext(), "${location.latitude}, ${location.longitude}",
                Toast.LENGTH_SHORT
            ).show()

            Log.d(TAG, "${location.latitude}, ${location.longitude}")
        }
    }

    private fun setMark(marker: Marker,location : UserLocation) {
        // 원근감 표시
        // marker.iconPerspectiveEnabled = true
        // 마커의 투명도
        marker.alpha = 0.8f
        // 마커 위치
        if (location != null) {
            marker.position = LatLng(location.latitude, location.longitude)
        }
        // 마커 우선순위
        marker.zIndex = 10
        // 마커 표시
        marker.map = naverMap
    }
}