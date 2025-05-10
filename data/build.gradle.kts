import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    id(libs.plugins.kotlin.kapt.get().pluginId)
    alias(libs.plugins.kotlin.serialization)
}
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

val serverHost = localProperties["server.host"] as String
val serverPort = (localProperties["server.port"] as String).toInt()
val isHttps = (localProperties["server.isHttps"] as String).toBoolean()

android {
    namespace = "com.reditus.knuhelperdemo.data"
    compileSdk = 35

    defaultConfig {
        minSdk = 27
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "SERVER_HOST", "\"$serverHost\"")
        buildConfigField("int", "SERVER_PORT", "$serverPort")
        buildConfigField("boolean","SERVER_IS_HTTPS", "$isHttps")
    }

    buildTypes {
        release {
            consumerProguardFiles("consumer-rules.pro")
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
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)

    //hlit
    implementation(libs.hilt.android)
    kapt(libs.dagger.hilt.compiler)
    implementation(libs.bundles.ktor)
    implementation(libs.androidx.datastore.preferences)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

kapt {
    correctErrorTypes = true
}