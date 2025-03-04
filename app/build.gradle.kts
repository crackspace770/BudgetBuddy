plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-parcelize")
    id("kotlin-kapt")
}

android {
    namespace = "com.android.budgetbuddy"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.android.budgetbuddy"
        minSdk = 24
        targetSdk = 35
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
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))

    implementation("com.google.firebase:firebase-auth:22.3.0")

    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("com.google.firebase:firebase-firestore-ktx:24.10.0")
    implementation("com.google.firebase:firebase-firestore:24.10.0")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation ("com.google.android.gms:play-services-auth:20.1.0")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")

    implementation ("androidx.datastore:datastore-preferences:1.0.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation ("androidx.activity:activity-ktx:1.3.1")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")

    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("com.github.bumptech.glide:glide:4.11.0")

    implementation ("androidx.datastore:datastore-core:1.1.1")

    implementation ("androidx.camera:camera-camera2:1.1.0-beta02")
    implementation ("androidx.camera:camera-lifecycle:1.1.0-beta02")
    implementation ("androidx.camera:camera-view:1.1.0-beta02")

    implementation ("androidx.camera:camera-core:1.1.0-alpha04")
    implementation ("androidx.camera:camera-camera2:1.1.0-alpha04")

    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.github.ismaeldivita:chip-navigation-bar:1.4.0")

    implementation ("androidx.room:room-runtime:2.6.1")
    kapt ("androidx.room:room-compiler:2.6.1")
    implementation ("androidx.room:room-ktx:2.6.1")


    implementation("com.facebook.shimmer:shimmer:0.1.0@aar")
    implementation("com.google.android.material:material:1.12.0")

    implementation("androidx.fragment:fragment-ktx:1.8.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.2")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.2")

    implementation("io.coil-kt:coil:2.1.0")
    implementation("io.coil-kt:coil-svg:2.1.0")

    implementation("androidx.work:work-runtime:2.8.1")
    implementation("com.loopj.android:android-async-http:1.4.10")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0-RC")

    implementation ("com.squareup.moshi:moshi:1.13.0")
    implementation ("com.squareup.moshi:moshi-kotlin:1.13.0")


}