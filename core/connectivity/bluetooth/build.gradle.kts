import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

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

            }
        }
        val commonTest by getting{
            dependencies{
                implementation(libs.test.kotlinTest)
                implementation(libs.test.kotlinTestJunit)


            }

        }
        val androidMain by getting {
            dependencies {

            }
        }

        val desktopMain by getting {
            dependencies {

            }
        }

    }


}
android {
    namespace = "blueetooth"
    compileSdk = 34
    defaultConfig {
        minSdk = 27
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

}