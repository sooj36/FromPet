package com.example.frompet.di

import com.example.frompet.login.repository.AuthRepository
import com.example.frompet.login.repository.BaseAuthRepository
import com.example.frompet.login.repository.firebase.BaseAuthenticator
import com.example.frompet.login.repository.firebase.FirebaseAuthenticator
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
    fun provideAuthenticator():BaseAuthenticator{
        return FirebaseAuthenticator()
    }
    @Singleton
    @Provides
    fun provideRepository(
        authenticator: BaseAuthenticator
    ):BaseAuthRepository{
        return AuthRepository(authenticator)
    }

}