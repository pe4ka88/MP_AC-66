plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.taxiezepchukac66"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.taxiezepchukac66"
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
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation("com.yandex.android:maps.mobile:4.30.0-full")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation(libs.constraintlayout)
    implementation(libs.glance)
    implementation("androidx.work:work-runtime:2.8.1")
    implementation("androidx.work:work-runtime-ktx:2.8.1")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}