package com.example.frompet.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.frompet.databinding.ActivityLoginBinding
import com.example.frompet.MainActivity
import com.example.frompet.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "랄라라"
        private const val RC_SIGN_IN = 9001
    }

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel by lazy {
        ViewModelProvider(this)[LoginViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listenToChannels()
        registerObservers()

        binding?.apply {
            signInButton.setOnClickListener {
                progressBarSignin.isVisible = true
                val email = userEmailEtv.text.toString()
                val password = userPasswordEtv.text.toString()
                viewModel.signInUser(email, password)

            }
            binding?.apply{
            loginGoogle.setOnClickListener {
                startGoogleSignIn()
            }
            }

            signUpTxt.setOnClickListener {
                val intent = Intent(this@LoginActivity, SingUpActivity::class.java)
                startActivity(intent)
            }

            forgotPassTxt.setOnClickListener {
                val intent = Intent(this@LoginActivity, PasswordResetActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun registerObservers() {
        viewModel.currentUser.observe(this@LoginActivity) { user ->
            user?.let {
                val uid = user.uid // 사용자의 고유 식별자 (UID)
                val userDocRef = FirebaseFirestore.getInstance().collection("User").document(uid)

                userDocRef.get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            // 사용자 정보가 Firestore에 있는 경우, MainActivity로 이동
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            // 사용자 정보가 Firestore에 없는 경우, MemberInfoActivity로 이동
                            val intent = Intent(this@LoginActivity, MemberInfoActivity::class.java)
                            startActivity(intent)
                        }
                    }
                    .addOnFailureListener {
                        // Firestore에서 데이터를 가져오는 동안 오류가 발생한 경우

                    }
            }
        }
    }

    private fun listenToChannels() {
        lifecycleScope.launch {
            viewModel.allEventsFlow.collect { event ->
                when (event) {
                    is LoginViewModel.AllEvents.Error -> {
                        val errorMessage = event.getKoreanMessage()
                        showSnackbar(errorMessage)
                        binding.progressBarSignin.isInvisible = true
                    }

                    is LoginViewModel.AllEvents.Message -> {
                        showSnackbar(event.message)
                    }

                    is LoginViewModel.AllEvents.ErrorCode -> {
                        when (event.code) {
                            1 -> {
                                binding.userEmailEtvl.error = "메일이 비어있어요"
                            }

                            2 -> {
                                binding.userPasswordEtvl.error = "비밀번호가 비어있어요"
                            }
                        }
                        binding.progressBarSignin.isInvisible = true
                    }

                    else -> {}
                }
            }
        }
    }
    private fun startGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    val idToken = account.idToken
                    if (idToken != null) {
                        // Google 로그인 성공 후, 해당 idToken을 사용하여 Firebase에 인증합니다.
                        viewModel.signInGoogle(idToken)

                        // Firebase에 로그인 성공한 후, 사용자 정보를 Firestore에 저장하고 MainActivity로 이동합니다.
                        viewModel.currentUser.observe(this@LoginActivity) { user ->
                            user?.let {
                                val uid = user.uid // 사용자의 고유 식별자 (UID)
                                val userDocRef = FirebaseFirestore.getInstance().collection("User").document(uid)

                                userDocRef.get()
                                    .addOnSuccessListener { documentSnapshot ->
                                        if (documentSnapshot.exists()) {
                                            // 사용자 정보가 Firestore에 있는 경우, MainActivity로 이동
                                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                            startActivity(intent)
                                        } else {
                                            // 사용자 정보가 Firestore에 없는 경우, MemberInfoActivity로 이동
                                            val intent = Intent(this@LoginActivity, MemberInfoActivity::class.java)
                                            startActivity(intent)
                                        }
                                    }
                                    .addOnFailureListener {
                                        // Firestore에서 데이터를 가져오는 동안 오류가 발생한 경우
                                    }
                            }
                        }
                    } else {
                        showSnackbar("Google 로그인 중 오류가 발생했습니다.")
                    }
                } catch (e: ApiException) {
                    showSnackbar("Google 로그인 중 오류가 발생했습니다.")
                }
            } else {
                showSnackbar("Google 로그인을 취소했습니다.")
            }
        }
    }
    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}

