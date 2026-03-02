package com.openmodality.di

import com.openmodality.mcp.McpServer
import com.openmodality.mcp.McpSessionManager
import com.openmodality.sensor.PlatformSensors
import com.openmodality.tools.SensorToolRegistry
import org.koin.dsl.module

/**
 * Shared Koin module.
 * PlatformSensors must be provided by platform-specific modules.
 */
fun sharedModule(platformSensors: PlatformSensors) = module {
    single { platformSensors }
    single { McpSessionManager() }
    single { SensorToolRegistry(get()) }
    single {
        val registry: SensorToolRegistry = get()
        McpServer(
            tools = registry.registerAll(),
            resources = registry.registerResources(),
            sessionManager = get()
        )
    }
}
