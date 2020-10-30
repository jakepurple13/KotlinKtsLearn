import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    //id(BuildPlugins.androidApplication)
    //id(BuildPlugins.kotlinAndroid)
    //id(BuildPlugins.kotlinAndroidExtensions)
    id("com.android.application")
    //id(BuildPlugins.kotlinKapt)
    //kotlin("")
    kotlin("android")
    kotlin("android.extensions")
}

android {
    compileSdkVersion(AndroidSdk.compile)
    defaultConfig {
        applicationId = "com.programmersbox.kotlinktslearn"
        minSdkVersion(AndroidSdk.min)
        targetSdkVersion(AndroidSdk.target)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {

    implementation(Libraries.kotlinStdLib)
    implementation(Libraries.appCompat)
    implementation(Libraries.ktxCore)
    implementation(Libraries.constraintLayout)
    implementation(Dependencies.material)
    implementation(Coroutines.core)
    implementation(Coroutines.android)

    implement(HelpfulToolsLibs.allLibs)
    //kapt(HelpfulToolsLibs.dslprocessor)

    testImplementation (TestLibraries.junit4)
    androidTestImplementation (TestLibraries.testRunner)
    androidTestImplementation (TestLibraries.espresso)

}

apply(from = "../publish.gradle")