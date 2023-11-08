package com.example.frompet.ui.intro

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.compose.runtime.currentCompositionErrors
import androidx.viewpager2.widget.ViewPager2
import com.example.frompet.MainActivity
import com.example.frompet.R
import com.example.frompet.data.model.PageItem
import com.example.frompet.databinding.ActivityIntroBinding
import com.example.frompet.ui.login.MemberInfoActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class IntroActivity : AppCompatActivity() {
    companion object {
        const val TAG: String = "로그"
    }
    private lateinit var binding: ActivityIntroBinding
    private var pageItemList = ArrayList<PageItem>()
    private lateinit var myIntroPagerRecyclerAdapter: IntroAdapter
    private val autoScrollHandler = Handler()
    private val autoScrollRunnable :Runnable = object  : Runnable{
        override fun run() {
            val currentItem = binding.myIntroViewPager.currentItem
            val nextItem = currentItem + 1
            if(nextItem < pageItemList.size){
                binding.myIntroViewPager.setCurrentItem(nextItem, true)
            }else{
                autoScrollHandler.removeCallbacks(this)
            }
            autoScrollHandler.postDelayed(this, 4000)
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val currentUser = FirebaseAuth.getInstance().currentUser

        if(currentUser != null){
            val uid = currentUser.uid
            val userDocRef = FirebaseFirestore.getInstance().collection("User").document(uid)

            userDocRef.get()
                .addOnSuccessListener { doucmentSnapshot ->
                    if (doucmentSnapshot.exists()){
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        val intent = Intent(this,MemberInfoActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
                .addOnFailureListener{

                }
        }

        binding.previousBtn.setOnClickListener {
            Log.d(TAG, "MainActivity - 이전 버튼 클릭")
            binding.myIntroViewPager.currentItem = binding.myIntroViewPager.currentItem - 1
        }

        binding.nextBtn.setOnClickListener {
            Log.d(TAG, "MainActivity - 다음 버튼 클릭")
           val currentItem = binding.myIntroViewPager.currentItem
            if(currentItem < pageItemList.size -1){
                binding.myIntroViewPager.currentItem = currentItem +1
            }else{
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        pageItemList.add(PageItem(R.color.light_orange, R.drawable.splash, "From Pet~!!"))
        pageItemList.add(PageItem(R.color.warm_blue, R.drawable.type, "반려동물을 위한 "))
        pageItemList.add(PageItem(R.color.light_gray, R.drawable.loginprofile, "소개팅 시작~"))

        myIntroPagerRecyclerAdapter = IntroAdapter(pageItemList)

        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        // 뷰페이저에 설정
        binding.apply {
            myIntroViewPager.adapter = myIntroPagerRecyclerAdapter
            myIntroViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            dotsIndicator.setViewPager2(binding.myIntroViewPager)
        }
        autoScrollHandler.postDelayed(autoScrollRunnable, 3000)
    }

}