package com.example.frompet.ui.chat.dialog

import android.content.Context
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.example.frompet.databinding.ChatBottomSheetExitBinding

class ChatExitDialog(private val context: Context) {


    fun showExitDialog(onConfirm: () -> Unit) {
        val bottomSheetDialog = BottomSheetDialog(context)
        val binding = ChatBottomSheetExitBinding.inflate(LayoutInflater.from(context), null, false)

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