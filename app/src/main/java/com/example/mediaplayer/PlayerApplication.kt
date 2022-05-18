package com.example.mediaplayer

import android.app.Application
import com.example.data.di.repositoryModule
import com.example.domain.di.interactorModule
import com.example.presentation.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PlayerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
    androidContext(applicationContext)
            modules(modules)
        }

    }
    private val modules = listOf(
        viewModelModule,
        interactorModule,
        repositoryModule
    )
}