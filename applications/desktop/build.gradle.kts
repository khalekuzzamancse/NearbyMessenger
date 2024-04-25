import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    //kotlin("jvm")
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    jvm {
        jvmToolchain(17)
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.common)
                implementation(compose.desktop.currentOs)
                implementation(project(":feature:chat:ui"))
                implementation(project(":core:socket:role_factory"))

            }
        }
    }
}

compose.desktop {
    application {
        javaHome  = System.getenv("JAVA_17")
        mainClass = "DesktopMainKt"


        nativeDistributions {
            targetFormats(TargetFormat.Exe)
            packageName = "Nearby Messenger"
            version = "1.0.0"
            windows {
                packageVersion = "1.0.0"
                msiPackageVersion = "1.0.0"
                exePackageVersion = "1.0.0"
              //  iconFile.set(project.file("icon.ico"))
            }
        }
    }
}