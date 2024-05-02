plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.realm)

}
kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }
    jvm("desktop"){
        jvmToolchain(17)
    }
    sourceSets{
        val commonMain by getting{
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.database.realm.base)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlin.test.junit)
            }
        }

        val androidMain by getting{
            dependencies {


            }
        }
        val desktopMain by getting{
            dependencies {

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing")
            }
        }
    }
    //to use expect and actual keywords
    kotlin {
        compilerOptions {
            // Common compiler options applied to all Kotlin source sets
            freeCompilerArgs.add("-Xmulti-platform")
        }
    }


}
android {
    namespace = "core.database"
    compileSdk = 34
    defaultConfig {
        minSdk = 27
    }

}