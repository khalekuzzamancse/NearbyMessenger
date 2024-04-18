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
val coreModules= listOf(":core",":core:wifi_direct",":core:socket")
val featureModules= listOf(":feature",
    ":feature:peers",":feature:peers:data",":feature:peers:domain",":feature:peers:ui",":feature:peers:di",
    ":feature:chat",":feature:chat:data",":feature:chat:domain",":feature:chat:ui",
    )
include(applicationsModules+coreModules+featureModules)
