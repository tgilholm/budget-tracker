plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.budgettracker"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.budgettracker"
        minSdk = 35 // Minimum SDK updated to support java.time and getLast()
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
    implementation(libs.constraintlayout)

    implementation("androidx.viewpager2:viewpager2:1.1.0") // ViewPager2
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0") // MPAndroidChart

    // Add dependency for Room DB
    implementation(libs.room.runtime)
    implementation(libs.recyclerview)

    // Preference Fragment dependency
    implementation("androidx.preference:preference:1.2.1")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    annotationProcessor(libs.room.compiler)
}