plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.thirdlab8"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.thirdlab8"
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
    
    // КРИТИЧЕСКИ ВАЖНО: ViewBinding (НЕ DataBinding)
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // AndroidX Core
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
    
    // Navigation Component - ИСПРАВЛЕНИЕ ОШИБКИ "Cannot resolve symbol navigation"
    implementation("androidx.navigation:navigation-fragment:2.8.7")
    implementation("androidx.navigation:navigation-ui:2.8.7")
    
    // ViewModel и LiveData - для MVVM
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.8.7")
    implementation("androidx.lifecycle:lifecycle-livedata:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime:2.8.7")
    
    // Retrofit для HTTP запросов
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // Gson для парсинга JSON
    implementation("com.google.code.gson:gson:2.11.0")
    
    // Glide для загрузки изображений - ИСПРАВЛЕНИЕ загрузки аватаров
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    
    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}