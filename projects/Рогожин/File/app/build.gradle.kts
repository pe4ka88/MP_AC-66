plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.file"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.file"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation("androidx.activity:activity:1.8.2")
    implementation("androidx.fragment:fragment:1.6.2")
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.palette:palette:1.0.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.google.android.exoplayer:exoplayer-core:2.16.1")
    implementation("com.google.android.exoplayer:exoplayer-ui:2.16.1")
    implementation("com.google.android.exoplayer:exoplayer-hls:2.16.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("net.java.dev.jna:jna:5.18.1@aar")
    implementation("com.alphacephei:vosk-android:0.3.75@aar")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}