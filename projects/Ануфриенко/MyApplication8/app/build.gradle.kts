plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.myapplication8"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myapplication8"
        minSdk = 26
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation("androidx.core:core:1.13.1")

    // OSMDroid — OpenStreetMap, без API-ключа
    implementation("org.osmdroid:osmdroid-android:6.1.18")

    // Fused Location Provider — для GPS в фоновом сервисе
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // CardView
    implementation("androidx.cardview:cardview:1.0.0")

    // ViewPager2
    implementation("androidx.viewpager2:viewpager2:1.1.0")

    // Fragment
    implementation("androidx.fragment:fragment:1.8.3")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}