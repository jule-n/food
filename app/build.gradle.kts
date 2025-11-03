plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)

    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
//    kotlin("jvm") version "2.1.10"
    kotlin("plugin.serialization") version "1.9.0"
}

android {
    namespace = "com.jule.food"
    compileSdk = 34

    androidResources {
        generateLocaleConfig = true
    }

    defaultConfig {
        applicationId = "com.jule.food"
        minSdk = 28
        compileSdk = 35
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.material3:material3-android:1.4.0-alpha15")
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.navigation.compose)
//    implementation("androidx.navigation:navigation-compose:2.9.0")
    implementation("sh.calvin.reorderable:reorderable:2.5.1")
//    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation(libs.coil.compose)
//    implementation(libs.stfalconimageviewer)
    implementation (libs.picasso)
    implementation(libs.androidx.appcompat)
    implementation(libs.play.services.base)
    implementation(libs.androidx.animation.graphics.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
//    implementation(libs.kotlinx.serialization.json)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    implementation("me.saket.telephoto:zoomable:0.14.0")
    implementation("me.saket.telephoto:zoomable-image-coil3:0.16.0")
    implementation("io.github.aghajari:LazyFlowLayout:1.1.0")
}