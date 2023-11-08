package com.pet.frompet.ui.commnunity.communityhome

import android.util.Log
import com.pet.frompet.data.model.CommunityData

sealed interface CategoryClick {
    data class PetCategory(
        val item: CommunityData
    ) : CategoryClick

}
