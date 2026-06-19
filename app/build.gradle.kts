import java.util.Properties
import java.io.FileInputStream

// Читаем keystore.properties (не коммитится). Если файла нет — release подпишется debug-ключом.
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
val hasReleaseKeystore = keystorePropertiesFile.exists()
if (hasReleaseKeystore) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
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
        buildConfigField("String", "GEMINI_API_KEY", "\"${project.findProperty("GEMINI_API_KEY") ?: ""}\"")
        // Ключ Google Maps — добавить в local.properties: MAPS_API_KEY=ВАШ_КЛЮЧ
        manifestPlaceholders["MAPS_API_KEY"] = project.findProperty("MAPS_API_KEY") ?: ""
    }

    signingConfigs {
        if (hasReleaseKeystore) {
            create("release") {
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true // R8/ProGuard: оптимизация, обфускация и сжатие кода
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Подписываем release-ключом, если он настроен; иначе откат на debug (чтобы сборка не падала)
            signingConfig = if (hasReleaseKeystore)
                signingConfigs.getByName("release")
            else
                signingConfigs.getByName("debug")
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
    implementation("androidx.compose.material:material-icons-extended")

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

// Crashlytics
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")

    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

// Google Maps SDK
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.maps.android:maps-ktx:5.1.1")
    implementation("com.google.maps.android:maps-compose:4.4.1")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}