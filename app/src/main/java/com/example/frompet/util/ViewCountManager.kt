package com.example.frompet.util

import com.google.firebase.firestore.FirebaseFirestore

class ViewCountManager {

    fun checkedViewCount(firestore: FirebaseFirestore, docsId: String?, onSuccess: () -> Unit) {
        docsId ?: return

        val postRef = firestore.collection("Community").document(docsId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(postRef)
            val currentCount = snapshot.getLong("viewCount") ?: 0
            transaction.update(postRef, "viewCount", currentCount + 1)
        }.addOnSuccessListener {
            onSuccess()
        }
    }
}
