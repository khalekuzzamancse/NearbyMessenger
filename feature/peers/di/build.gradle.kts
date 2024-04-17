plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinMultiplatform)
}
kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }

    }
    jvm("desktop") {
        jvmToolchain(17)
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(project(":feature:peers:domain"))
                api(project(":feature:peers:data"))
                implementation(project(":core:wifi_direct"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.test.kotlinTest)
                implementation(libs.test.kotlinTestJunit)

            }

        }

        val desktopMain by getting {
            dependencies {
                //dependency to support android coil on desktop
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing")
            }
        }
    }

}
android {
    namespace = "peers.di"
    compileSdk = 34
    defaultConfig {
        minSdk = 27
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

}