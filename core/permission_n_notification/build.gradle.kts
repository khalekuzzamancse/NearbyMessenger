plugins {
    id("org.jetbrains.compose")
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
                implementation(compose.ui)
                implementation(compose.material3)
                implementation(compose.animation)
                implementation(compose.animationGraphics)
                implementation(compose.materialIconsExtended)
                implementation(compose.foundation)
                implementation(compose.runtime)
                implementation(libs.windowSize)
                //
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        val androidMain by getting {
            dependencies {

                //to access Context such as for permissions
                implementation(libs.androidx.activity.compose)

            }
        }
        val desktopMain by getting {
            dependencies {
                //dependency to support android coil on desktop
                api("org.jetbrains.kotlinx:kotlinx-coroutines-swing")
            }
        }
    }


}
android {
    namespace = "core.permission"
    compileSdk = 34
    defaultConfig {
        minSdk = 27
    }

}