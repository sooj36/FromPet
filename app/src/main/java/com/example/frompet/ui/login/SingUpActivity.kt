package com.example.frompet.ui.login

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.frompet.databinding.ActivitySingUpBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SingUpActivity : AppCompatActivity() {
    private var _binding: ActivitySingUpBinding? = null
    private val binding get() = _binding!!
    private val viewModel by lazy {
        ViewModelProvider(this)[LoginViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySingUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerObservers()
        listenToChannels()

        binding.apply {
            signUpButton.setOnClickListener {
                progressBarSignup.isVisible = true
                val email = userEmailEtv.text.toString()
                val password = userPasswordEtv.text.toString()
                val confirmPass = confirmPasswordEtv.text.toString()
                if(email.isEmpty()){
                    Toast.makeText(this@SingUpActivity,"이메일을 입력해주세요",Toast.LENGTH_SHORT).show()
                }else if (password.length < 6){
                    Toast.makeText(this@SingUpActivity,"비밀번호는 6자리 이상입니다.",Toast.LENGTH_SHORT).show()
                }else{
                    viewModel.signUpUser(email, password, confirmPass)
                }

            }
            signInTxt.setOnClickListener {
                val intent = Intent(this@SingUpActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }
    private fun registerObservers() {
        viewModel.currentUser.observe(this@SingUpActivity) { user ->
            user?.let {
                val intent = Intent(this@SingUpActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun listenToChannels() {
        lifecycleScope.launch {
            viewModel.allEventsFlow.collect { event ->
                when(event){
                    is LoginViewModel.AllEvents.Error -> {
                        binding.apply {
                            errorTxt.text = event.error
                            progressBarSignup.isInvisible = true
                        }
                    }
                    is LoginViewModel.AllEvents.Message -> {
                        Toast.makeText(this@SingUpActivity, event.message, Toast.LENGTH_SHORT).show()
                    }
                    is LoginViewModel.AllEvents.ErrorCode -> {
                        if (event.code == 1)
                            binding.apply {
                                userEmailEtvl.error = "메일이 비어있어요"
                                progressBarSignup.isInvisible = true
                            }


                        if(event.code == 2)
                            binding.apply {
                                userPasswordEtvl.error = "비밀번호가 비어있어요"
                                progressBarSignup.isInvisible = true
                            }

                        if(event.code == 3)
                            binding.apply {
                                confirmPasswordEtvl.error = "비밀번호가 맞지 않네요"
                                progressBarSignup.isInvisible = true
                            }
                        if(event.code == 4){
                            Toast.makeText(this@SingUpActivity,event.code,Toast.LENGTH_SHORT).show()
                        }
                    }

                    else ->{

                    }
                }

            }
        }
    }
    private fun showToast(message: String) {
        Toast.makeText(this@SingUpActivity, message, Toast.LENGTH_SHORT).show()
    }
}
