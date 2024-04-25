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
val coreModules= listOf(":core",":core:wifi_direct",
    ":core:socket",":core:socket:server",":core:socket:client",":core:socket:role_factory",
)
val featureModules= listOf(":feature",
    ":feature:scanned_device",":feature:scanned_device:data",":feature:scanned_device:domain",":feature:scanned_device:ui",":feature:scanned_device:di",
    ":feature:chat",":feature:chat:data",":feature:chat:domain",":feature:chat:ui",
    ":feature:navigation",
    )
include(applicationsModules+coreModules+featureModules)
