package com.example.frompet.ui.commnunity

import android.content.Context
import android.view.LayoutInflater
import com.example.frompet.databinding.CommunityAddBottomSheetExitBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class AddExitDialog(private val context : Context) {

    fun showExitDialog(onConfirm : () -> Unit) {
        val bottomSheetDialog = BottomSheetDialog(context)
        val binding = CommunityAddBottomSheetExitBinding.inflate(LayoutInflater.from(context), null, false)

        binding.btnExit.setOnClickListener {
            onConfirm.invoke()
            bottomSheetDialog.dismiss()
        }

        binding.btnCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(binding.root)
        bottomSheetDialog.show()
    }
}