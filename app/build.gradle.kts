plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    id("kotlin-kapt")
}

android {
    namespace = "com.example.wanandroid"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.wanandroid"
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

    kotlinOptions {
        jvmTarget = "11"
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

    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

// OkHttp（日志拦截器）
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

// Gson
    implementation("com.google.code.gson:gson:2.10.1")
    // ViewModel 核心库 (管理生命周期)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    // Fragment KTX (提供 by viewModels() 这个极其方便的委托语法)
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    // 协程生命周期 (配合 StateFlow 使用)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    // 图片加载库 Coil
    implementation("io.coil-kt:coil:2.5.0")
    
    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Room
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    // 使用 KSP 而不是 KAPT (如果是新版 Kotlin) - 等等，可能需要添加 KSP 插件
    // 或者直接用 KAPT
    // 还是先用 kapt 吧，因为项目可能没配 ksp
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")

    // LeakCanary
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.13")
}