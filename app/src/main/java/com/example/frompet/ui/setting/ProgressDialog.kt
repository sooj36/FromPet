package com.example.frompet.ui.setting


import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.view.Window
import com.example.frompet.R


class ProgressDialog(context: Context?) : Dialog(context!!) {
    init {

        requestWindowFeature(Window.FEATURE_NO_TITLE)


        setContentView(R.layout.activity_progress_dialog)


        window?.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT)

        setCancelable(false);
    }
}
