package com.pet.frompet

import com.pet.frompet.ui.setting.fcm.FCMTokenManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.pet.frompet.databinding.ActivityMainBinding
import com.pet.frompet.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import android.Manifest
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.pet.frompet.util.showToast

class MainActivity : AppCompatActivity() {
    //    private val fcmTokenManagerViewModel by lazy {
//        ViewModelProvider(this)[FCMTokenManagerViewModel::class.java]
//    }
    private var _binding: ActivityMainBinding? = null

    private val fcmTokenManager = FCMTokenManager()
    private val binding get() = _binding!!

    private val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all { it.value }
            if (granted) {

                showToast( "위치 권한이 승인되었습니다.", Toast.LENGTH_SHORT)
                // 위치 기능을 초기화하거나 위치 기반 서비스 시작
            } else {
                // 사용자가 권한을 거부했을 때의 처리
                showToast( "위치 권한이 필요합니다.", Toast.LENGTH_LONG)
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.myBottomNav.itemIconTintList = null
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)




        // Android 13 PostNotification 처리
        checkAppPushNotification()

        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let {
            fcmTokenManager.fetchAndBaseFCMToken(it.uid)  // FCM 토큰을 가져와서 저장
        } //세준

        if (currentUser == null) {
            // 사용자가 로그인되어 있지 않은 경우
            // LoginActivity로 이동
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // 현재 화면 종료
        } else {
            setStartApp()
            checkLocationPermission()
        }
    }

    private fun setStartApp() {
        val navHostFragment = supportFragmentManager.findFragmentById(com.pet.frompet.R.id.my_nav_host) as NavHostFragment
        val navController = navHostFragment.navController
        _binding?.let { NavigationUI.setupWithNavController(it.myBottomNav, navController) }
    }

    private fun checkAppPushNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
            // 푸시 권한 요청
            permissionPostNotification.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        // 권한이 허용된 경우의 동작을 구현
    }

    private val permissionPostNotification = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            // 권한 허용된 경우의 동작
        } else {
            // 권한 비허용된 경우의 동작
            showToast( "푸시 알림 권한이 필요합니다.", Toast.LENGTH_LONG)
        }
    }

    private fun checkLocationPermission() {
        // 모든 위치 권한이 이미 승인되었는지 확인
        val allPermissionsGranted = locationPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
        if (!allPermissionsGranted) {
            // 하나라도 거부된 권한이 있다면 모든 위치 권한을 요청
            requestLocationPermissionLauncher.launch(locationPermissions)
        }
    }
    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

}