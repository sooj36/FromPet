package com.example.frompet.ui.setting

import FCMTokenManagerViewModel
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import coil.load
import com.example.frompet.databinding.FragmentSettingBinding
import com.example.frompet.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingViewModel by viewModels()
    private val fcmTokenManagerViewModel: FCMTokenManagerViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadUserPetProfile()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSettingBinding.inflate(inflater, container, false)


        viewModel.petProfile.observe(viewLifecycleOwner) { petProfile ->
            petProfile?.let {
               binding.ivPet.load(it)//혹시모르니 코일로 바꿔놨습니다 승현님.
                Log.d("sooj", "${it}")
            }
        }

        viewModel.petName.observe(viewLifecycleOwner) { petName ->
            binding.tvPetName.text = petName
        }

        viewModel.petType.observe(viewLifecycleOwner) { petType ->
            binding.tvPetType.text = petType
        }


        binding.ibLogOut.setOnClickListener {
            val currentUser = FirebaseAuth.getInstance().currentUser

            if (currentUser != null) {
                // 현재 로그인된 사용자가 있는 경우에만 실행
                val userId = currentUser.uid

                // FCM 토큰을 삭제하는 코드 추가
                fcmTokenManagerViewModel.removeFCMToken(userId)

                // 사용자 로그아웃
                FirebaseAuth.getInstance().signOut()

                // LoginActivity로 이동
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
            }
        }


        binding.btModify.setOnClickListener {
            val intent = Intent(requireActivity(), SettingProfileActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }
    override fun onResume() {
        super.onResume()
        viewModel.loadUserPetProfile()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}