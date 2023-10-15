package com.example.frompet.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.frompet.R
import com.example.frompet.databinding.ActivityLoginBinding
import com.example.frompet.databinding.ActivityMainBinding
import com.example.frompet.home.HomeFragment
import com.example.frompet.login.viewmodel.LoginViewModel
import com.example.frompet.login.viewmodel.LoginViewModelFactory
import com.example.frompet.main.MainActivity
import com.google.firebase.firestore.FirebaseFirestore


class LoginActivity : AppCompatActivity() {
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            LoginViewModelFactory()
        )[LoginViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btSingup.setOnClickListener {
            val intent = Intent(this, SingUpActivity::class.java)
            startActivity(intent)
        }

        binding.btPasswordRe.setOnClickListener {
            val intent = Intent(this,PasswordResetActivity::class.java)
            startActivity(intent)
        }
        binding.btLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            viewModel.singIn(email, password)



            viewModel.user.observe(this) { firebaseUser ->
                if (firebaseUser != null) {
                    val uid = firebaseUser.uid
                    Log.d("LoginActivity", "사용자 UID: $uid")

                    // Firebase Firestore에서 사용자 정보 가져오기
                    FirebaseFirestore.getInstance().collection("User")
                        .document(uid)
                        .get()
                        .addOnSuccessListener { documentSnapshot ->
                            if (documentSnapshot.exists()) {
                                // 사용자 정보가 Firestore에 있는 경우, MainActivity로 이동
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            } else {
                                // 사용자 정보가 Firestore에 없는 경우, MemberInfoActivity로 이동
                                val intent = Intent(this, MemberInfoActivity::class.java)
                                startActivity(intent)
                            }
                        }
                        .addOnFailureListener {
                        }
                }
            }
        }

        viewModel.loginResult.observe(this) { loginSuccess ->
            if (loginSuccess) {
                showLoginResultToast(true)
            } else {
                showLoginResultToast(false)
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoginResultToast(isSuccess: Boolean) {
        val message = if (isSuccess) "로그인 성공" else "로그인 실패"
        showToast(message)
    }

}

