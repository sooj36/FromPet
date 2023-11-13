package com.pet.frompet.ui.more.signout

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.pet.frompet.MainActivity
import com.pet.frompet.R
import com.pet.frompet.databinding.ActivityMorePasswordResetBinding
import com.pet.frompet.databinding.ActivitySignOutBinding
import com.pet.frompet.ui.intro.IntroActivity
import com.pet.frompet.ui.login.LoginActivity
import com.pet.frompet.ui.login.LoginViewModel
import com.pet.frompet.ui.more.MorePasswordResetViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignOutActivity : AppCompatActivity() {
    private var _binding: ActivitySignOutBinding? = null
    private val binding get() = _binding!!

    private val viewModel by lazy {
        ViewModelProvider(this).get(MorePasswordResetViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySignOutBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btOut.setOnClickListener {
            viewModel.deleteAccount()
            checkLoginState()
        }






    }
    private fun checkLoginState() {
        val intent = if (isLoggedIn()) {
            Intent(this, IntroActivity::class.java)
        } else {
            Intent(this, LoginActivity::class.java) // 원하는 액티비티로 변경 가능
        }

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        // 앱을 재시작하기 위한 PendingIntent 생성
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        // AlarmManager 서비스를 가져와서 재시작 예약
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(
            AlarmManager.RTC,
            System.currentTimeMillis() + 100, // 필요한 경우 지연 시간을 조절할 수 있습니다
            pendingIntent
        )

        // 현재 액티비티를 종료하여 앱 재시작을 시뮬레이션
        finish()
    }
    private fun isLoggedIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

}