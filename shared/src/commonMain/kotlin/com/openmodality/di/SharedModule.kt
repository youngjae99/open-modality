package com.openmodality.di

import com.openmodality.server.OpenModalityServer
import com.openmodality.server.SessionManager
import com.openmodality.sensor.PlatformSensors
import com.openmodality.tools.SensorToolRegistry
import org.koin.dsl.module

/**
 * Shared Koin module.
 * PlatformSensors must be provided by platform-specific modules.
 */
fun sharedModule(platformSensors: PlatformSensors) = module {
    single { platformSensors }
    single { SessionManager() }
    single { SensorToolRegistry(get()) }
    single {
        val registry: SensorToolRegistry = get()
        OpenModalityServer(
            tools = registry.registerAll(),
            sensors = get(),
            sessionManager = get()
        )
    }
}
