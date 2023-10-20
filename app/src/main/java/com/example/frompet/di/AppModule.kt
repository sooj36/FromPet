package com.example.frompet.di

import com.example.frompet.data.repository.AuthRepository
import com.example.frompet.data.repository.BaseAuthRepository
import com.example.frompet.data.repository.firebase.BaseAuthenticator
import com.example.frompet.data.repository.firebase.FirebaseAuthenticator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideAuthenticator(): BaseAuthenticator {
        return FirebaseAuthenticator()
    }
    @Singleton
    @Provides
    fun provideRepository(
        authenticator: BaseAuthenticator,
        firestore: FirebaseFirestore
    ): BaseAuthRepository {
        return AuthRepository(authenticator, firestore)
    }

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Singleton
    @Provides
    fun provideFirestore(): FirebaseFirestore {
        val firestore = FirebaseFirestore.getInstance()
        val settings = firestore.firestoreSettings
        firestore.firestoreSettings = settings
        return firestore
    }

}