package com.yuchen.composeapp.data

import com.google.firebase.firestore.FirebaseFirestore

class FirebaseFacade {
    private var firestore: FirebaseFirestore? = null

    fun getFirestore(): FirebaseFirestore =
        firestore ?: kotlin.run {
            firestore = FirebaseFirestore.getInstance()
            firestore!!
        }
}