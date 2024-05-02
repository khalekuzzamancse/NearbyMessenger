import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
}
kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        instrumentedTestVariant {
            sourceSetTree.set(KotlinSourceSetTree.test)
            dependencies {
                implementation(libs.test.androidxUiJunit)
                debugImplementation(libs.test.androidxUiManifest)
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
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.windowSize)
                //
                implementation(project(":feature:scanned_device"))//For Scanned device UI
                implementation(project(":feature:chat:ui")) //For chat UI and Add chat to database
                implementation(project(":core:connectivity:wifi_direct")) //For Device list and connect with  wifi direct
                implementation(project(":core:socket:role_factory")) //For data communication after connect via wifi direct

            }
        }
        val commonTest by getting{
            dependencies{
                implementation(libs.test.kotlinTest)
                implementation(libs.test.kotlinTestJunit)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.uiTest)

            }

        }
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.core)
                implementation(libs.androidx.navigation.compose)
                //for view model
                val lifecycleVersion = "2.7.0"
                // ViewModel
                implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
                // ViewModel utilities for Compose
                implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
                implementation(libs.androidx.navigation.compose)
            }
        }

        val desktopMain by getting {
            dependencies {
                //dependency to support android coil on desktop
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing")
            }
        }
        val desktopTest by getting {
            dependencies {
                implementation(compose.desktop.uiTestJUnit4)
                implementation(compose.desktop.currentOs)
            }
        }
    }


}
android {
    namespace = "chatbywifidirect"
    compileSdk = 34
    defaultConfig {
        minSdk = 27
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

}