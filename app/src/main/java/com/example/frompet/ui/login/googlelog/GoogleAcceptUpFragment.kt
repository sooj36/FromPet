package com.example.frompet.ui.login.googlelog

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.frompet.MainActivity
import com.example.frompet.R
import com.example.frompet.databinding.FragmentGoogleAcceptUpBinding
import com.example.frompet.ui.intro.IntroActivity.Companion.TAG
import com.example.frompet.ui.login.MemberInfoActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GoogleAcceptUpFragment : DialogFragment() {
    private var _binding: FragmentGoogleAcceptUpBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GoogleViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //false로 설정해 주면 화면밖 혹은 뒤로가기 버튼시 다이얼로그라 dismiss 되지 않는다.

        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentGoogleAcceptUpBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val text = "구글 로그인으로 회원가입을 완료했습니다!"

        binding.tvSample.text = text

        binding.btYes.setOnClickListener {
            Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
            startGoogleSignIn()
        }
        binding.btNo.setOnClickListener {

        }

        viewModel.currentUser.observe(viewLifecycleOwner) { firebaseUser ->
            val uid = firebaseUser?.uid
            if (uid != null) {
                Log.e(TAG, "$uid")
                if (userHas()) {
                    startMainActivity()
                } else {
                    startMemberInfoActivity()
                }
            } else {
                Log.e(TAG, "null")
            }
        }
    }

    private fun userHas(): Boolean {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser != null
    }

    companion object {
        private const val RC_SIGN_IN = 9001
        fun newInstance(): GoogleAcceptUpFragment {
            return GoogleAcceptUpFragment()
        }
    }

    private fun startGoogleSignIn() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // 이미 로그인한 사용자가 있으면 MainActivity로 이동
            startMainActivity()
        } else {
            // Google 로그인 진행
            val webClientId = getString(R.string.web_client_id)
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientId)
                .requestEmail()
                .build()
            val mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: requestCode = $requestCode, resultCode = $resultCode")
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    val idToken = account.idToken
                    Log.e(TAG, "$idToken")
                    if (idToken != null) {
                        viewModel.signInGoogle(idToken)
                        viewModel.currentUser.observe(viewLifecycleOwner) { firebaseUser ->
                            val uid = firebaseUser?.uid
                            if (uid != null) {
                                Log.e(TAG, "$uid")
                            }

                        }
                    } else {
                        Log.e(TAG, "idToken is null")
                    }
                } catch (e: ApiException) {
                    Log.e(TAG, "Google sign-in failed", e)
                }
            } else {
                Log.e(TAG, "Google sign-in result is not OK")
            }
        }
    }


    private fun startMainActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
    private fun startMemberInfoActivity() {
        val intent = Intent(requireContext(), MemberInfoActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

}