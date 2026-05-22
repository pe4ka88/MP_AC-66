plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.a2lab"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.a2lab"
        minSdk = 24
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Базовые зависимости AndroidX с актуальными версиями
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("androidx.navigation:navigation-fragment:2.8.0")
    implementation("androidx.navigation:navigation-ui:2.8.0")
    implementation("androidx.activity:activity:1.9.3")

    // Тестирование
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    // OpenStreetMap (OSMDroid)
    implementation("org.osmdroid:osmdroid-android:6.1.18") {
        exclude(group = "com.j256.ormlite", module = "ormlite-android")
        exclude(group = "com.j256.ormlite", module = "ormlite-core")
    }

    // Google Play Services для геолокации
    implementation("com.google.android.gms:play-services-location:21.3.0")
}