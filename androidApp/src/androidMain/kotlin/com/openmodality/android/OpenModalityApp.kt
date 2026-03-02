package com.openmodality.android

import android.app.Application
import com.openmodality.di.sharedModule
import com.openmodality.sensor.PlatformSensors
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class OpenModalityApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@OpenModalityApp)
            modules(sharedModule(PlatformSensors()))
        }
    }
}
