package com.example.frompet.ui.login

import android.content.ContentValues
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
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel by lazy {
        ViewModelProvider(this)[LoginupViewModel::class.java]

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

            binding.signUpTxt.setOnClickListener {
                val intent = Intent(this@LoginActivity, SingUpActivity::class.java)
                startActivity(intent)
            }

            binding.forgotPassTxt.setOnClickListener {
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
                    is LoginupViewModel.AllEvents.Error -> {
                        binding.apply {
                            errorTxt.text = event.error
                            progressBarSignin.isInvisible = true
                        }
                    }

                    is LoginupViewModel.AllEvents.Message -> {
                        Toast.makeText(this@LoginActivity, event.message, Toast.LENGTH_SHORT).show()
                    }

                    is LoginupViewModel.AllEvents.ErrorCode -> {
                        if (event.code == 1)
                            binding.apply {
                                userEmailEtvl.error = "메일이 비어있어요"
                                progressBarSignin.isInvisible = true
                            }


                        if (event.code == 2)
                            binding.apply {
                                userPasswordEtvl.error = "비밀번호가 비어있어요"
                                progressBarSignin.isInvisible = true
                            }
                    }

                    else -> {
                        Log.d(ContentValues.TAG, "listenToChannels: No event received so far")
                    }
                }

            }
        }

    }

}

