package com.example.frompet.map


import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.example.frompet.databinding.FragmentMapBinding
import com.naver.maps.map.MapView
import com.example.frompet.map.MapFragment
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource

class MapFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding
    private var mapView: MapView? = null
    private lateinit var mNaverMap : NaverMap

    private val PERMISSION_REQUEST_CODE : Int = 1000
    private val PERMISSIONS = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private lateinit var locationSource : FusedLocationSource // 위치 반환 구현체


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val rootView = _binding?.root
        mapView = _binding?.mapView
        mapView?.onCreate(savedInstanceState)

        /**지도를 비동기적으로 초기화하고 지도가 사용 가능해질 때, 수행할 작업의 정의한 onMapReady메서드 호출**/
        // NaverMap 초기화 처리
        mapView?.getMapAsync(this)

        locationSource = FusedLocationSource(requireActivity(), PERMISSION_REQUEST_CODE)

        return rootView
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

    override fun onMapReady(p0: NaverMap) {
        mNaverMap = p0
        mNaverMap.setLocationSource(locationSource)

        ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, PERMISSION_REQUEST_CODE )
        mNaverMap.locationTrackingMode = LocationTrackingMode.Follow
        Log.d("sooj", "여길 탔나요 ?")

    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
//                mNaverMap.setLocationTrackingMode(LocationTrackingMode.Follow)
//            }
//        }
//    }

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
