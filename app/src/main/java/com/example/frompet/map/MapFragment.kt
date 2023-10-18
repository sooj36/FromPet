package com.example.frompet.map

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.example.frompet.R
import com.example.frompet.databinding.FragmentCommunicationBinding
import com.example.frompet.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapSdk


class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding
    private lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        mapView = binding!!.mapView
        mapView.onCreate(savedInstanceState)

        return binding?.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}

class MapFragemnt : Fragment() {

    private lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = rootView.findViewById(R.id.mapFragment) as MapView
        mapView.onCreate(savedInstanceState)

        return rootView
    }

    // 생명주기와 싱크 연동
    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }
}



// 스크롤 가능한 뷰에서 스크롤할 때 지도가 무작위로 움직이지 않도록 하는 역할
class ScrollAwareMapView : MapView {
    constructor(context : Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    //  사용자 지정 지도 뷰 만들고, 터치 이벤트 처리 시 상위 뷰 그룹에서 이벤트 가로채지 않도록 함
    // ScrollAwareMapView가 지도 스크롤,확대/축소 할 때, 부모 뷰 그룹의 간섭 방지
    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        parent.requestDisallowInterceptTouchEvent(true)
        return super.dispatchTouchEvent(event)
    }
}