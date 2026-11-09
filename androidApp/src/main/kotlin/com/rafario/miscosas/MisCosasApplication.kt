package com.rafario.miscosas

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rafario.miscosas.api.session.createAndroidSessionRestorationFacade
import com.rafario.miscosas.data.auth.FirebaseNativeAuthenticationProvider
import com.rafario.miscosas.data.profile.FirestoreRemoteUserProfileRepository

class MisCosasApplication : Application() {

    val sessionRestorationFacade by lazy {
        val authRepo = FirebaseAuth.getInstance()
        val firestoreRepo = FirebaseFirestore.getInstance()

        createAndroidSessionRestorationFacade(
            applicationContext,
            FirebaseNativeAuthenticationProvider(authRepo),
            FirestoreRemoteUserProfileRepository(firestoreRepo)
        )
    }
}