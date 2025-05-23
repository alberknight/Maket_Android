plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") // Plugin de Firebase
}

android {
    namespace = "com.example.apiconecta1"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.apiconecta1"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-auth:22.1.1")
    implementation ("com.google.firebase:firebase-firestore:24.7.0")
    implementation ("com.google.firebase:firebase-storage:20.2.1")
    implementation ("com.squareup.picasso:picasso:2.71828") // Para mostrar imágenes
    implementation ("com.google.firebase:firebase-firestore:24.7.1")
    implementation ("com.google.android.gms:play-services-safetynet:18.0.1")
}