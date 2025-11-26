plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.crashlytics)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.huerto_hogar_aplicacion"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.huerto_hogar_aplicacion"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        compose = true
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
    implementation(libs.androidx.compose.ui.text)
    val room_version = "2.8.1"
    implementation("androidx.room:room-runtime:${room_version}")

    ksp("androidx.room:room-compiler:$room_version")
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:${room_version}")
    // optional - RxJava2 support for Room
    implementation("androidx.room:room-rxjava2:${room_version}")
    // optional - RxJava3 support for Room
    implementation("androidx.room:room-rxjava3:${room_version}")
    // optional - Guava support for Room, including Optional and ListenableFuture
    implementation("androidx.room:room-guava:${room_version}")
    // optional - Test helpers
    testImplementation("androidx.room:room-testing:${room_version}")
    // optional - Paging 3 Integration

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.auth)
    implementation("com.google.firebase:firebase-analytics")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("io.coil-kt:coil-compose:2.4.0")

    implementation("com.google.android.gms:play-services-location:21.0.1")

    implementation("androidx.datastore:datastore-preferences:1.0.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.8")

    implementation("androidx.room:room-paging:${room_version}")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.compose.runtime:runtime-livedata")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}