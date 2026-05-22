plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.taxi"
    compileSdk = 36  // ИЗМЕНИТЬ С 34 НА 36

    defaultConfig {
        applicationId = "com.example.taxi"
        minSdk = 24
        targetSdk = 36  // ИЗМЕНИТЬ С 34 НА 36
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
    dependencies {
        implementation("androidx.appcompat:appcompat:1.6.1")
        implementation("com.google.android.material:material:1.9.0")
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")

        // Для геолокации (уже есть)
        implementation("com.google.android.gms:play-services-location:21.3.0")

        //  osmdroid для OpenStreetMap
        implementation("org.osmdroid:osmdroid-android:6.1.20")

        testImplementation("junit:junit:4.13.2")
        androidTestImplementation("androidx.test.ext:junit:1.1.5")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
        //  ДЛЯ ПОГОДЫ
        // Для сетевых запросов
        implementation("com.squareup.okhttp3:okhttp:4.12.0")
        // Для парсинга JSON
        implementation("com.google.code.gson:gson:2.10.1")

        testImplementation("junit:junit:4.13.2")
        androidTestImplementation("androidx.test.ext:junit:1.1.5")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    }
}