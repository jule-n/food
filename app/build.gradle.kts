plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.hilt.android)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.jule.food"
    compileSdk = 37

    androidResources {
        generateLocaleConfig = true
    }

    defaultConfig {
        applicationId = "com.jule.food"
        minSdk = 28
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
    buildFeatures {
        compose = true
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
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation("androidx.compose.material3:material3:1.5.0-alpha26")
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.navigation.compose)
    implementation("sh.calvin.reorderable:reorderable:2.5.1")
    implementation(libs.coil.compose)
    implementation(libs.picasso)
    implementation(libs.androidx.appcompat)
    implementation(libs.play.services.base)
    implementation(libs.androidx.animation.graphics.android)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)
    testImplementation(libs.junit)
    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.kotlinx.serialization.json)
    implementation("me.saket.telephoto:zoomable:0.14.0")
    implementation("me.saket.telephoto:zoomable-image-coil3:0.16.0")
    implementation("io.github.aghajari:LazyFlowLayout:1.1.0")

    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    implementation(libs.google.hilt.android)
    ksp(libs.google.hilt.compiler)
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Preferences DataStore (SharedPreferences like APIs)
    implementation("androidx.datastore:datastore-preferences:1.2.1")
}

//configurations.all {
//    resolutionStrategy {
//        force("androidx.compose:compose-bom:2024.04.01")
//    }
//}
//
