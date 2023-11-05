package com.example.frompet.ui.commnunity.communityhome

import android.util.Log
import com.example.frompet.data.model.CommunityData

sealed interface CategoryClick {
    data class PetCategory(
        val item: CommunityData
    ) : CategoryClick

}
