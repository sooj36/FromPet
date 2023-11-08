package com.pet.frompet.util

import android.graphics.Color
import java.util.Random

class RandomColor {
    companion object {
        fun getRandomColor(alpha: Int, saturation: Float, brightness: Float): Int {
            val random = Random()
            val hue = random.nextInt(361).toFloat() // 0 to 360
            val hsv = floatArrayOf(hue, saturation, brightness)
            return Color.HSVToColor(alpha, hsv)
        }
    }
}
