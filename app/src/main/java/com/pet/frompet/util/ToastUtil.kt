package com.pet.frompet.util

import android.content.Context
import android.widget.Toast

internal fun Context.showToast(message: String, time: Int ){
    Toast.makeText(this,message,time).show()
}//아이원트 호출
