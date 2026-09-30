pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

// Версии те же, что в template-flutter (AGP 8.9.1 / Kotlin 2.1.0 / Gradle 8.14):
// они уже в локальном gradle-кеше и проверены на Codemagic.
// Jetpack Compose НЕ используется: UI — XML + AppCompat + Material Components.
// White-version (без google-services) по умолчанию.
plugins {
    id("com.android.application") version "8.9.1" apply false
    id("org.jetbrains.kotlin.android") version "2.1.0" apply false
    // id("com.google.gms.google-services") version "4.4.2" apply false
}

rootProject.name = "CrownFruitsDrop"
include(":app")
