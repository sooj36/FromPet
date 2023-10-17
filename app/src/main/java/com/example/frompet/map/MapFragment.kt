package com.example.frompet.map


import android.app.Instrumentation.ActivityResult
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.airbnb.lottie.model.Marker
import com.example.frompet.databinding.FragmentMapBinding
import com.naver.maps.map.MapView
import com.example.frompet.map.MapFragment
import com.google.firebase.database.collection.LLRBNode
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons

class MapFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding
    private var mapView: MapView? = null
    private lateinit var mNaverMap: NaverMap

    private val ACCESS_LOCATION_PERMISSION_REQUEST_CODE : Int = 1000
    private val PERMISSION_REQUEST_CODE: Int = 1000
    private val PERMISSIONS = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )

//    private val requestMultiplePermissions =
//        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
//            if (permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true) {
//                mNaverMap.locationTrackingMode = LocationTrackingMode.Follow
//            }
//        } // 세준님 추가

    private lateinit var locationSource: FusedLocationSource // 위치 반환 구현체

//    private val marker = Marker()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val rootView = _binding?.root
        mapView = _binding?.mapView
        mapView?.onCreate(savedInstanceState)

        /**지도를 비동기적으로 초기화하고 지도가 사용 가능해질 때, 수행할 작업의 정의한 onMapReady메서드 호출**/
        // NaverMap 초기화 처리
        mapView?.getMapAsync(this) //맵뷰방식 동기화처리

        locationSource = FusedLocationSource(requireActivity(), ACCESS_LOCATION_PERMISSION_REQUEST_CODE)

        return rootView
    }

    override fun onMapReady(p0: NaverMap) { //맵프래그먼트에서 쓰는 것들 / 동적으로 처리ㅐㅎ
        mNaverMap = p0
        mNaverMap.setLocationSource(locationSource)

//        requestMultiplePermissions.launch(PERMISSIONS) // 세준님 추가

        mNaverMap.uiSettings.isLocationButtonEnabled = true

        mNaverMap.addOnLocationChangeListener {
            Log.d("sooj", "it.${it.latitude}")

        }

        //  위치 권한 요청
        ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, ACCESS_LOCATION_PERMISSION_REQUEST_CODE)
        Log.d("sooj", "여길 탔나요 ?..")

        //현재 위치 마커 (애뮬테스트 - 서울시 시청)
//
//        marker.position = LatLng(37.566535, 126.977969)
//        marker.map = mNaverMap
//        marker.icon = MarkerIcons.BLACK
//        marker.iconTintColor = Color.RED

        // 위치 추적 모드 설정
        mNaverMap.locationSource = locationSource
        mNaverMap.locationTrackingMode = LocationTrackingMode.Follow

        val locationOverlay = mNaverMap.locationOverlay
        locationOverlay.isVisible = true


    }

    // 사용자 권한 요청 후 응답 시 호출 콜백 함수
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // 권한 부여 시, 네이버 지도 에서 위치추적 모드 활설화
        if (requestCode == ACCESS_LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                mNaverMap.locationSource = locationSource
                mNaverMap.locationTrackingMode = LocationTrackingMode.Follow
                Log.d("sooj", "위치모드 활성화")
            }
            else {
                ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, PERMISSION_REQUEST_CODE)
                Log.d("sooj", " 비활성화")
            }
        }
    }



    // 생명주기와 싱크 연동
    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()

    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
        mapView?.onDestroy()
    }


}


// 스크롤 가능한 뷰에서 스크롤할 때 지도가 무작위로 움직이지 않도록 하는 역할
class ScrollAwareMapView : MapView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    //  사용자 지정 지도 뷰 만들고, 터치 이벤트 처리 시 상위 뷰 그룹에서 이벤트 가로채지 않도록 함
    // ScrollAwareMapView가 지도 스크롤,확대/축소 할 때, 부모 뷰 그룹의 간섭 방지
    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        parent.requestDisallowInterceptTouchEvent(true)
        return super.dispatchTouchEvent(event)
    }
}
