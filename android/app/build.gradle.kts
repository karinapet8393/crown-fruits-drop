import java.util.Properties

// Native Android: Kotlin + XML layouts (AppCompat + Material Components + Fragments + ViewModel).
// Jetpack Compose НЕ используется. White-version по умолчанию (без google-services).
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val keystoreProperties = Properties()
val keystorePropertiesFile = rootProject.file("key.properties")
if (keystorePropertiesFile.exists()) {
    keystorePropertiesFile.inputStream().use(keystoreProperties::load)
}

// Подпись принимается в трёх формах, чтобы подходили ВСЕ сборщики фабрики:
//   1. env ANDROID_KEYSTORE_*   — cm-build-kotlin.yaml, gh-build-kotlin.yml
//   2. -PMYAPP_RELEASE_*        — форма RN-шаблона (cm-build.yaml / gh-build.yml)
//   3. android/key.properties   — пишет generate-kotlin.sh (per-app keystore), читает build-apk-kotlin.sh
fun signingValue(envKey: String, gradleProp: String, propertyKey: String): String? {
    return System.getenv(envKey)?.takeIf { it.isNotBlank() }
        ?: (project.findProperty(gradleProp) as String?)?.takeIf { it.isNotBlank() }
        ?: keystoreProperties.getProperty(propertyKey)?.takeIf { it.isNotBlank() }
}

val storeFilePath = signingValue("ANDROID_KEYSTORE_PATH", "MYAPP_RELEASE_STORE_FILE", "storeFile")
val releaseStorePassword = signingValue("ANDROID_KEYSTORE_PASSWORD", "MYAPP_RELEASE_STORE_PASSWORD", "storePassword")
val releaseKeyAlias = signingValue("ANDROID_KEY_ALIAS", "MYAPP_RELEASE_KEY_ALIAS", "keyAlias")
val releaseKeyPassword = signingValue("ANDROID_KEY_PASSWORD", "MYAPP_RELEASE_KEY_PASSWORD", "keyPassword")
val hasReleaseSigning = listOf(
    storeFilePath,
    releaseStorePassword,
    releaseKeyAlias,
    releaseKeyPassword,
).all { !it.isNullOrBlank() }

android {
    // namespace == applicationId == package в .kt: test-ui.sh / verify-app-runs.sh
    // запускают `am start -n <applicationId>/.MainActivity`.
    namespace = "com.WqNzVmK.rJpLtF"
    compileSdk = 36

    val buildNumber = System.getenv("BUILD_NUMBER")?.toIntOrNull() ?: 1
    val versionNameValue = System.getenv("APP_VERSION")?.takeIf { it.isNotBlank() } ?: "1.0"

    defaultConfig {
        applicationId = "com.WqNzVmK.rJpLtF"
        minSdk = 26
        targetSdk = 36
        versionCode = buildNumber
        versionName = versionNameValue
    }

    // AI-ассеты gen-assets.sh кладёт в <project>/assets/*.png. Эта папка подключена
    // как Android assets напрямую — без копирования в res/. Код грузит их через
    // core/assets/AssetImages.kt: imageView.loadAsset(AppAssets.BG_MENU).
    sourceSets {
        getByName("main") {
            assets.srcDirs("../../assets")
        }
    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = file(storeFilePath!!)
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }
}

// Разрешённый набор зависимостей (см. CLAUDE-kotlin.md). Новые библиотеки не добавлять:
// приложение полностью офлайн, без монетизации, хранение — только локальное (SharedPreferences).
dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.activity:activity-ktx:1.9.3")
    implementation("androidx.fragment:fragment-ktx:1.8.5")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
}
