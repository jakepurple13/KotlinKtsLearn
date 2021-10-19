object HelpfulToolsLibs {
    const val jakepurple13 = "10.5.2"
    const val flowutils = "com.github.jakepurple13.HelpfulTools:flowutils:$jakepurple13"
    const val gsonutils = "com.github.jakepurple13.HelpfulTools:gsonutils:$jakepurple13"
    const val helpfulutils = "com.github.jakepurple13.HelpfulTools:helpfulutils:$jakepurple13"
    const val loggingutils = "com.github.jakepurple13.HelpfulTools:loggingutils:$jakepurple13"
    const val dragswipe = "com.github.jakepurple13.HelpfulTools:dragswipe:$jakepurple13"
    const val funutils = "com.github.jakepurple13.HelpfulTools:funutils:$jakepurple13"
    const val dslannotations = "com.github.jakepurple13.HelpfulTools:dslannotations:$jakepurple13"
    const val dslprocessor = "com.github.jakepurple13.HelpfulTools:dslprocessor:$jakepurple13"
    //const val rxutils = "com.github.jakepurple13.HelpfulTools:rxutils:$jakepurple13"

    val allLibs = arrayOf(
        flowutils,
        gsonutils,
        helpfulutils,
        loggingutils,
        //rxutils,
        dragswipe,
        funutils
        //dslannotations
    )
}

object Dependencies {
    const val material = "com.google.android.material:material:1.1.0"
}

object Coroutines {
    private const val version = "1.3.3"
    const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
    const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
}

fun org.gradle.kotlin.dsl.DependencyHandlerScope.implement(item: Array<String>) = item.forEach { add("implementation", it) }

const val kotlinVersion = "1.4.10"

object BuildPlugins {

    object Versions {
        const val buildToolsVersion = "4.1.0"
    }

    const val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.buildToolsVersion}"
    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
    const val kotlinGradlePlugin2 = "org.jetbrains.kotlin:kotlin-gradle-plugin:"
    const val androidApplication = "com.android.application"
    const val kotlinAndroid = "kotlin-android"
    const val kotlinAndroidExtensions = "kotlin-android-extensions"
    const val kotlinKapt = "kotlin-kapt"
}

object AndroidSdk {
    const val min = 27
    const val compile = 29
    const val target = compile
}

object Libraries {
    private object Versions {
        const val jetpack = "1.1.0"
        const val constraintLayout = "1.1.3"
        const val ktx = "1.2.0"
    }

    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"
    const val appCompat = "androidx.appcompat:appcompat:${Versions.jetpack}"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    const val ktxCore = "androidx.core:core-ktx:${Versions.ktx}"
}

object TestLibraries {
    private object Versions {
        const val junit4 = "4.13"
        const val testRunner = "1.1.0-alpha4"
        const val espresso = "3.2.0"
    }

    const val junit4 = "junit:junit:${Versions.junit4}"
    const val testRunner = "androidx.test:runner:${Versions.testRunner}"
    const val espresso = "androidx.test.espresso:espresso-core:${Versions.espresso}"
}
