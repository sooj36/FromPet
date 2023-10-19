package com.example.frompet.ui.map

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.frompet.R
import com.example.frompet.databinding.FragmentMapBinding
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
        naverMap.maxZoom = 21.0 //(최대 21)
        naverMap.minZoom = 10.0

//        // 현재 위치 마커
//        marker.position = LatLng(37.5665, 126.9780)
//        marker.map = naverMap
//        marker.icon = MarkerIcons.BLUE
////        marker.iconTintColor = Color.RED // 마커 색상 변경


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



    private fun setMark(marker: Marker, lat: Double, lng: Double, resourceID: Int) {
        // 원근감 표시
//        marker.iconPerspectiveEnabled = true
        // 아이콘 지정
        marker.icon = OverlayImage.fromResource(resourceID)
        // 마커의 투명도
        marker.alpha = 0.8f
        // 마커 위치
        marker.position = LatLng(lat, lng)
        // 마커 우선순위
        marker.zIndex = 10
        // 마커 표시
        marker.map = naverMap
    }
}