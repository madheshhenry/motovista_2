plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.motovista_deep"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.motovista_deep"
        minSdk = 26
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
    // AndroidX Core libraries
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // UI Components
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.gridlayout:gridlayout:1.0.0")

    // Material Design (SINGLE VERSION)
    implementation("com.google.android.material:material:1.11.0")

    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // JSON parsing
    implementation("com.google.code.gson:gson:2.10.1")

    // Image loading
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // Image views
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.github.chrisbanes:PhotoView:2.3.0")

    // PDF Generation
    implementation("com.itextpdf:itextpdf:5.5.13.3")
    implementation("commons-io:commons-io:2.11.0")
    implementation("androidx.documentfile:documentfile:1.0.1")
    implementation("de.hdodenhof:circleimageview:3.1.0")


// For CardView
    implementation("androidx.cardview:cardview:1.0.0")
// For NestedScrollView
    implementation("androidx.core:core:1.7.0")
    // PDF Viewer - CHOOSE ONE OPTION:

    // Option 1: Try a WORKING alternative (add this line)
    // implementation("com.github.Praseetha-K:AndroidPdfViewer:1.0.0")

    // Option 2: Use Android's native viewer (NO dependency needed)
    // (Use the PdfViewerHelper class I provided earlier)

    // REMOVE ALL THESE PROBLEMATIC LINES:
    // implementation("com.github.barteksc:android-pdf-viewer:2.8.2")
    // implementation("com.github.barteksc:android-pdf-viewer:3.2.0-beta.1")
    // implementation("com.github.barteksc:android-pdf-viewer:3.2.0")
    // implementation("com.github.TalbotGooday:AndroidPdfViewer:4.3.0")
}