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
val featureModules= listOf(":feature",":feature:peers",":feature:chatui")
include(applicationsModules+featureModules)
