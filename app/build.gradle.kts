plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.budgetbuddy"
    compileSdk = 34

    packagingOptions {
        exclude("javamoney.properties")
    }

    defaultConfig {
        applicationId = "com.example.budgetbuddy"
        minSdk = 29
        targetSdk = 33
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
    buildToolsVersion = "30.0.3"
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment:2.3.0")
    implementation("androidx.navigation:navigation-ui:2.3.0")
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.google.firebase:firebase-messaging")
    implementation("androidx.annotation:annotation:1.7.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("com.google.firebase:firebase-messaging:23.4.1")
    implementation("com.google.android.gms:play-services-auth:19.2.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.android.volley:volley:1.2.1")
    testImplementation("junit:junit:4.13.2")
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    implementation("com.github.jitpack:gradle-simple:v1.1")
    implementation("com.squareup.okhttp3:okhttp:4.9.2")
    // implementation("javax.money:money-api:1.1")
    // implementation("org.javamoney:moneta:1.4")
    runtimeOnly("org.joda:joda-money:1.0.4")
    implementation("org.joda:joda-money:1.0.4")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
