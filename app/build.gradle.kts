plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.ecopulse"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.ecopulse"
        minSdk = 31
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true // Включаем R8/ProGuard оптимизацию и сжатие
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug") // Используем debug-ключ для простоты сборки
        }
        getByName("debug") {
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
    }

    // Обязательно объявляем измерения для флаворов
    flavorDimensions.add("version")

    productFlavors {
        create("free") {
            dimension = "version"
            applicationIdSuffix = ".free"
            resValue("string", "app_name", "EcoPulse Free")
            buildConfigField("Boolean", "IS_PREMIUM", "false")
        }
        create("premium") {
            dimension = "version"
            applicationIdSuffix = ".premium"
            resValue("string", "app_name", "EcoPulse Pro")
            buildConfigField("Boolean", "IS_PREMIUM", "true")
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
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation("androidx.core:core:1.15.0")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    val composeBom = platform("androidx.compose:compose-bom:2024.02.02")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Основные компоненты Compose UI, Runtime и Материальный дизайн 3
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // Интеграция Compose с Activity и жизненным циклом (ViewModel)
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // Навигация (Jetpack Navigation for Compose)
    implementation("androidx.navigation:navigation-compose:2.7.7")
    // Библиотека сериализации (нужна для Type-Safe навигации, которую мы заложили в Screen.kt)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // Инструменты для отладки UI (отрисовка Preview в студии)
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-compiler:2.51.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // Firebase BOM — управляет версиями всех Firebase библиотек
    val firebaseBom = platform("com.google.firebase:firebase-bom:33.1.0")
    implementation(firebaseBom)

// Firestore
    implementation("com.google.firebase:firebase-firestore-ktx")

// FCM (Push-уведомления)
    implementation("com.google.firebase:firebase-messaging-ktx")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}