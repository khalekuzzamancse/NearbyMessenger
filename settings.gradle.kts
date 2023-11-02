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

rootProject.name = "ConnectivitySamples(Networking)"
include(":app")
include(":Level_1")
include(":Level_1:clinet_server_concept")
include(":Level_1:clinet_server_concept:clinetapp")
include(":Level_1:clinet_server_concept:serverapp")
include(":Level_1:wifi")
include(":Level_1:wifi:peertopeer")
