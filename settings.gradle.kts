pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
val applicationsModules= listOf(":applications","applications:android",":applications:desktop")
val coreModules= listOf(":core",
    ":core:connectivity", ":core:connectivity:wifi_direct", ":core:connectivity:wifi_hotspot", ":core:connectivity:nearby_connection_api",
    ":core:socket",":core:socket:server",":core:socket:client",":core:socket:protocol",":core:socket:role_factory",
    ":core:database",
    ":core:permission_n_notification",
)
val featureModules= listOf(":feature",
    ":feature:scanned_device",":feature:scanned_device:data",":feature:scanned_device:domain",":feature:scanned_device:ui",":feature:scanned_device:di",
    ":feature:chat",":feature:chat:data",":feature:chat:domain",":feature:chat:ui",":feature:chat:di",
    ":feature:nearby_api_chat_service",
    ":feature:navigation",
    )
include(applicationsModules+coreModules+featureModules)
