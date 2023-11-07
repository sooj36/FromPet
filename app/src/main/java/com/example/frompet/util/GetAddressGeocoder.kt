package com.example.frompet.util

import android.content.Context
import android.location.Geocoder
import java.io.IOException
import java.util.Locale

fun getAddressGeocoder(context: Context, latitude: Double, longitude: Double): String {
    val geocoder = Geocoder(context, Locale.KOREA)
    try {
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        if (addresses?.isNotEmpty() == true) {
            val address = addresses[0]
            //박세준:2023-11-07
            // adminArea는 서울특별시, 경기도 등을, locality는 강남구, 수원시등을 나타냅니다

            return with(address) {
                val adminAreaName = adminArea?.replace("특별시", "시")?.replace("광역시", "시")
                listOfNotNull(adminAreaName, locality, subLocality).joinToString(" ").trim()
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return "주소를 찾을 수 없음"
}
