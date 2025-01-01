plugins {

    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.bk"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.bk"
        minSdk = 34
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
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-auth:21.1.0")
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.android.material:material:1.9.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("com.google.firebase:firebase-auth:21.1.0' // Firebase Auth")
    implementation ("com.google.firebase:firebase-firestore:24.3.0") // Firestore（如果需要）
    implementation ("com.google.firebase:firebase-analytics:21.1.0") // Firebase Analytics
    implementation ("com.squareup.picasso:picasso:2.71828") // 图片加载库（如果需要）
    // Firebase BoM
    implementation (platform("com.google.firebase:firebase-bom:31.2.3"))

    // Firebase Authentication
    implementation ("com.google.firebase:firebase-auth")

    // Firebase Firestore
    implementation ("com.google.firebase:firebase-firestore")

    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")

    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")
    // Jsoup 用于解析HTML
    implementation ("org.jsoup:jsoup:1.15.4")
    // Google Play Services Tasks
    implementation ("com.google.android.gms:play-services-tasks:18.0.2")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}