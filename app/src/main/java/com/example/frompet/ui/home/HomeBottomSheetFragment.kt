import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.load
import com.example.frompet.R
import com.example.frompet.databinding.FragmentHomeBottomSheetDialogBinding
import com.example.frompet.data.model.User
import com.example.frompet.ui.chat.activity.ChatClickUserDetailActivity
import com.example.frompet.ui.chat.activity.ChatPullScreenActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class HomeBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentHomeBottomSheetDialogBinding? = null
    private val binding get() = _binding!!
    private var user: User? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): android.view.View? {
        _binding = FragmentHomeBottomSheetDialogBinding.inflate(inflater, container, false)

        user = arguments?.getParcelable(ChatClickUserDetailActivity.USER)
        user?.let {
            displayUserInfo(it)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)= with(binding) {
        super.onViewCreated(view, savedInstanceState)
        backBtn.setOnClickListener {
            dismiss()
        }

        ivPetProfile.setOnClickListener {
            val intent  = Intent(requireContext(), ChatPullScreenActivity::class.java)
            intent.putExtra(ChatPullScreenActivity.IMAGE_URL,user?.petProfile)
            startActivity(intent)

        }
    }
    private fun displayUserInfo(user: User)= with(binding) {
            tvPetName.text = user.petName
            tvPetAge.text = "${user.petAge.toString()}세"
            tvPetGender.text = user.petGender
            tvPetType.text = user.petType
            tvPetDes.text = user.petDescription
            tvPetIntro.text = user.petIntroduction
            user.petProfile.let {
                ivPetProfile.load(user.petProfile){
                    error(R.drawable.kakaotalk_20230825_222509794_01)
                }
            }
        }




    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance(user: User): HomeBottomSheetFragment {
            val args = Bundle()
            args.putParcelable(ChatClickUserDetailActivity.USER, user)
            val fragment = HomeBottomSheetFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
