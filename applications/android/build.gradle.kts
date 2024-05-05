plugins {
    alias(libs.plugins.androidApplication)
    kotlin("android")
    alias(libs.plugins.jetbrainsCompose)
}

android {
    namespace = "kzcse.bluefimessenger"
    //do not change this later,already sign in certificate generated,and in plays tore this already uploaded,if changed then update will not work

    compileSdk = 34

    defaultConfig {
        applicationId = "kzcse.bluefimessenger"
        //do not change this later,already sign in certificate generated,and in plays tore this already uploaded,if changed then update will not work
        minSdk = 27
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.accompanist.permissions)
    implementation(compose.ui)
    implementation(compose.material3)
    implementation(compose.animation)
    implementation(compose.animationGraphics)
    implementation(compose.materialIconsExtended)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.windowSize)

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    //
    implementation(project(":feature:navigation"))
    implementation(project(":core:connectivity:wifi_direct"))//For starting broadcast on application class and read wifi  status
    implementation(project(":core:connectivity:wifi_hotspot"))//For starting broadcast on application class and read wifi  hotspot and scan devices
    implementation(project(":core:connectivity:bluetooth"))//For starting broadcast on application class and read bluetooth status
    implementation(project(":core:permission_n_notification"))
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation(project(":core:connectivity:wifi_direct"))
    implementation(project(":feature:bluetooth_chat_service"))

}