plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.ba.housedrawba"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ba.modernhouse.design3d.houseplan.drawhouse.floorplan"
        minSdk = 24
        targetSdk = 36
        versionCode = 10
        versionName = "10.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    viewBinding {
        enable = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            resValue("string", "PURCHASE_ID", "premium")
            resValue("string", "ADMOB_APP_ID", "ca-app-pub-7106940377104365~3034687751")

            resValue("string", "ADMOB_BANNER", "ca-app-pub-7106940377104365/3743931165")
            resValue("string", "ADMOB_BANNER_MF", "ca-app-pub-7106940377104365/5436286781")
            resValue("string", "ADMOB_BANNER_HF", "ca-app-pub-7106940377104365/2253620054")
            resValue("string", "ADMOB_BANNER_SPLASH", "ca-app-pub-7106940377104365/7142662228")

            resValue("string", "ADMOB_INTERSTITIAL", "ca-app-pub-7106940377104365/6694645443")
            resValue("string", "ADMOB_INTERSTITIAL_MF", "ca-app-pub-7106940377104365/6370094507")
            resValue("string", "ADMOB_INTERSTITIAL_HF", "ca-app-pub-7106940377104365/6749368459")

            resValue("string", "ADMOB_OPEN_APP", "ca-app-pub-7106940377104365/8502636856")
            resValue("string", "ADMOB_OPEN_APP_MF", "ca-app-pub-7106940377104365/4879783397")
            resValue("string", "ADMOB_OPEN_APP_HF", "ca-app-pub-7106940377104365/1309339519")

            resValue("string", "ADMOB_REWARDED", "ca-app-pub-7106940377104365/3176315295")
            resValue("string", "ADMOB_REWARDED_HF", "ca-app-pub-7106940377104365/9194110496")
            resValue("string", "ADMOB_REWARDED_MF", "ca-app-pub-7106940377104365/3175497050")

            resValue("string", "ADMOB_NATIVE", "ca-app-pub-3940256099942544/2247696110")

            resValue ("string", "max_sdk", "kzt7bMUziQHwexUW3gzLP3Fed31MNvkmbWT5gVbIFqeyXR1as2X8c2hJx_kdPGFPkO0i1_frN6xOPkxl1LiXC3")
            resValue ("string", "max_interstitial", "b1aadede5bec44c7")
            resValue ("string", "max_MREC", "28522b4e27298b0e")
            resValue ("string", "max_banner", "f1bfa4aea3ccfb34")
            resValue ("string", "max_appOpen", "d8e34457aa159c7b")

        }
        debug {
            resValue("string", "PURCHASE_ID", "android.test.purchased")
            resValue("string", "ADMOB_APP_ID", "ca-app-pub-3940256099942544~3347511713")

            resValue("string", "ADMOB_BANNER", "ca-app-pub-3940256099942544/6300978111")
            resValue("string", "ADMOB_BANNER_MF", "ca-app-pub-3940256099942544/6300978111")
            resValue("string", "ADMOB_BANNER_HF", "ca-app-pub-3940256099942544/6300978111")
            resValue("string", "ADMOB_BANNER_SPLASH", "ca-app-pub-3940256099942544/6300978111")

            resValue("string", "ADMOB_INTERSTITIAL", "ca-app-pub-3940256099942544/1033173712")
            resValue("string", "ADMOB_INTERSTITIAL_MF", "ca-app-pub-3940256099942544/1033173712")
            resValue("string", "ADMOB_INTERSTITIAL_HF", "ca-app-pub-3940256099942544/1033173712")

            resValue("string", "ADMOB_OPEN_APP", "ca-app-pub-3940256099942544/9257395921")
            resValue("string", "ADMOB_OPEN_APP_MF", "ca-app-pub-3940256099942544/9257395921")
            resValue("string", "ADMOB_OPEN_APP_HF", "ca-app-pub-3940256099942544/9257395921")

            resValue("string", "ADMOB_REWARDED", "ca-app-pub-7106940377104365/3176315295")
            resValue("string", "ADMOB_REWARDED_HF", "ca-app-pub-7106940377104365/9194110496")
            resValue("string", "ADMOB_REWARDED_MF", "ca-app-pub-7106940377104365/3175497050")

            resValue("string", "ADMOB_NATIVE", "ca-app-pub-3940256099942544/2247696110")

            resValue ("string", "max_sdk", "kzt7bMUziQHwexUW3gzLP3Fed31MNvkmbWT5gVbIFqeyXR1as2X8c2hJx_kdPGFPkO0i1_frN6xOPkxl1LiXC3")
            resValue ("string", "max_interstitial", "b1aadede5bec44c7")
            resValue ("string", "max_MREC", "28522b4e27298b0e")
            resValue ("string", "max_banner", "f1bfa4aea3ccfb34")
            resValue ("string", "max_appOpen", "d8e34457aa159c7b")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":nativetemplates"))
    implementation(libs.glide)
    implementation(libs.sdp.android)
    implementation(libs.autofittextview)
    implementation(libs.dexter)
    implementation(libs.lottie)
    implementation(libs.ultimatebarx)
    implementation("com.github.yalantis:ucrop:2.2.10")
    implementation("io.github.bitvale:switcher:1.1.2")

    implementation("com.amazonaws:aws-android-sdk-s3:2.81.0")
    implementation("com.itextpdf:itext7-core:9.3.0")
    implementation("com.google.zxing:core:3.5.3")

    implementation("com.google.code.gson:gson:2.13.1")
    implementation("com.android.volley:volley:1.2.1")

    implementation("io.github.sceneview:sceneview:2.3.0")
//    implementation("io.github.sceneview:arsceneview:2.3.0")

    implementation(libs.play.services.ads)
    implementation(libs.lifecycle.extensions)
    implementation(libs.lifecycle.runtime)
    implementation(libs.app.update)
    implementation(libs.review)
    implementation(libs.billing)

    implementation(libs.applovin)
    implementation(libs.mintegral)
    implementation(libs.facebook)
    implementation(libs.infer.annotation)

    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.config)

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation ("com.applovin:applovin-sdk:13.4.0")
    implementation ("com.applovin.mediation:google-adapter:24.6.0.0")
    implementation ("com.applovin.mediation:mintegral-adapter:16.9.91.0")
    implementation ("com.applovin.mediation:facebook-adapter:6.20.0.0")

    //For AR Drawing Features
    implementation ("androidx.camera:camera-camera2:1.4.2")
    implementation ("androidx.camera:camera-core:1.4.2")
    implementation ("androidx.camera:camera-lifecycle:1.4.2")
    implementation ("androidx.camera:camera-view:1.4.2")
}