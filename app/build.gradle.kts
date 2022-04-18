plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    compileSdk = ConfigData.compileSdk

    defaultConfig {
        applicationId = ConfigData.applicationId
        minSdk = ConfigData.minSdk
        targetSdk = ConfigData.targetSdk
        versionCode = ConfigData.versionCode
        versionName = ConfigData.versionName

        testInstrumentationRunner = ConfigData.testInstrumentationRunner
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        getByName("debug") {
            isDebuggable = true
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        applicationVariants.all {
            val variant = this
            variant.outputs
                .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
                .forEach { output ->
                    val outputFileName =
                        "${rootProject.name} ${variant.versionName} - ${variant.baseName}.apk"
                    println("OutputFileName: $outputFileName")
                    output.outputFileName = outputFileName
                }
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.kotlinCompilerExt
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(Deps.coreKtx)
    implementation(Deps.playSevMaps)
    implementation(Deps.mapsCompose)
    implementation(Deps.libPlaces)
    //implementation ()
    // compose
    implementation(Deps.composeMaterial)
    implementation(Deps.composeUi)
    implementation(Deps.composeUiTooling)
    implementation(Deps.composeFoundation)
    implementation(Deps.composeRuntime)
    implementation(Deps.composeCompiler)
    implementation(Deps.composeNavigation)
    implementation(Deps.composeActivity)
    // system UI controller 0.24.5-alpha
    implementation(Deps.accompanistSysUiController)
    implementation(Deps.accompanistPermissions)
    implementation(Deps.accompanistPager)
    // lifecycle feathers
    implementation(Deps.lifecycleRuntime)
    implementation(Deps.lifecycleVieModelKtx)
    implementation(Deps.lifecycleViewModelCompose)
    //coroutines flow
    implementation(Deps.coroutinesFlowKtx)
    // Retrofit
    implementation(Deps.gson)
    implementation(Deps.retrofit)
    implementation(Deps.retrofitConverter)
    //okhttp
    implementation(Deps.okhttp)
    implementation(Deps.okhttpInterseptor)
    // Glide
    implementation(Deps.glide)
    implementation(Deps.glideCronet)
    implementation(Deps.glideRV) {
        // Excludes the support library because it's already included by Glide.
        isTransitive = false
    }
    //lottie
    implementation(Deps.lottie)
    // hilt di
    implementation(Deps.hiltDeps)
    implementation(Deps.hiltNavFragment)
    kapt(Deps.hiltCompiler)
    implementation(Deps.hiltNavCompose)

    testImplementation(Deps.jUnit)
    androidTestImplementation(Deps.jUnitExtTest)
    androidTestImplementation(Deps.espressoCore)
    androidTestImplementation(Deps.jUnitUiTest)
    debugImplementation(Deps.composeUiToolingPreview)
}