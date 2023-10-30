package com.example.frompet

import com.example.frompet.ui.setting.fcm.FCMTokenManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.frompet.databinding.ActivityMainBinding
import com.example.frompet.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import android.Manifest

class MainActivity : AppCompatActivity() {
    //    private val fcmTokenManagerViewModel by lazy {
//        ViewModelProvider(this)[FCMTokenManagerViewModel::class.java]
//    }
    private var _binding: ActivityMainBinding? = null

    private val fcmTokenManager = FCMTokenManager()
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.myBottomNav.itemIconTintList = null

        // FCM 설정 및 토큰 가져오기
//        FCMTokenManagerViewModel().getFirebaseToken()

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
        }
//        fcmTokenManagerViewModel.retrieveAndSaveFCMToken()
        /* viewModel.user.observe(this) { firebaseUser ->
             if (firebaseUser != null) {
                 val uid = firebaseUser.uid
                 Log.d("ㅋㅋㅋㅋㅋ", "사용자 UID: $uid")
             }git pull origin release/1.0.0
         }*/
    }
    private fun setStartApp() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.my_nav_host) as NavHostFragment
        val navController = navHostFragment.navController
        _binding?.let { NavigationUI.setupWithNavController(it.myBottomNav, navController) }
    }

    private fun checkAppPushNotification() {
        // Android 13 이상 && 푸시 권한 없음
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            && PackageManager.PERMISSION_DENIED == ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)) {
            // 푸시 권한 없음
            permissionPostNotification.launch(Manifest.permission.POST_NOTIFICATIONS)
            return
        }
        // TODO: 권한이 허용된 경우의 동작을 구현
    }
    private val permissionPostNotification = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            // 권한 허용
            // TODO: 권한이 허용된 경우의 동작을 구현
        } else {
            // 권한 비허용
            // TODO: 권한이 거부된 경우의 동작을 구현
        }
    }

}