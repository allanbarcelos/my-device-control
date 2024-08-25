plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "dev.barcelos.mydevicecontrolclientapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "dev.barcelos.mydevicecontrolclientapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // Adicionar configuração personalizada BuildConfig
        buildConfigField("String", "ANDROID_TOKEN", "\"___ACCESS_TOKEN___\"")
        buildConfigField("String", "SERVER_API", "\"___SERVER_API___\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "ANDROID_TOKEN", "\"___ACCESS_TOKEN___\"")
            buildConfigField("String", "SERVER_API", "\"___SERVER_API___\"")
        }
        debug {
            buildConfigField("String", "ANDROID_TOKEN", "\"___ACCESS_TOKEN___\"")
            buildConfigField("String", "SERVER_API", "\"___SERVER_API___\"")
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
        buildConfig = true

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

    // Dependências para WebSocket e suporte a atividades Kotlin
    implementation("com.squareup.okhttp3:okhttp:4.10.0") // Biblioteca OkHttp para WebSocket
    implementation("androidx.appcompat:appcompat:1.6.1") // Para compatibilidade com versões anteriores
    implementation("androidx.activity:activity-ktx:1.6.0") // Para suporte a atividades Kotlin

    // Dependências de Teste
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Dependências de Debug
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


}