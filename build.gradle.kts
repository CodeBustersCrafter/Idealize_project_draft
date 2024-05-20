
// Top-level build file where you can add configuration options common to all sub-projects/modules.
dependencies {
    // Import the BoM for the Firebase platform
    implementation(libs.firebase.bom)

    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation(libs.google.firebase.auth)
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.google.gms.google.services) apply false
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
}

fun implementation(bom: Provider<MinimalExternalModuleDependency>) {

}
