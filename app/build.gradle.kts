@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.tomastu.iceweather"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.tomastu.iceweather"
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
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson.converter)
    implementation(libs.data.store)
    implementation(platform("com.google.firebase:firebase-bom:32.2.2"))
    implementation(libs.play.services.ads)
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation(libs.viewmodel.compose)
    implementation(libs.accompanist.swiperefresh)
    implementation(libs.fragment.ktx)
//    implementation(libs.coroutine.core)
//    implementation(libs.coroutine.android)
    implementation(files("libs/AMap2DMap_6.0.0_AMapSearch_9.5.0_AMapLocation_6.3.0_20230410.aar"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}

gradle.beforeProject {
    this.ext.set("hasTests", false)
    println("invoke beforeProject callback! project: ${this.name}")
}

gradle.afterProject {
    if (this.ext.has("hasTests") && this.ext.get("hasTests") as Boolean) {
        val projectString = this.toString()
        println("adding test tasks to $projectString")
        tasks.register("test") {
//            doLast {
                println("running tests for $projectString")
//            }
        }
    }
    println("invoke afterProject callback! project: ${this.name}")
}