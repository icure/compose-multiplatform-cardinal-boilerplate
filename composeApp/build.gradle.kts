import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    jvm()

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.datastore.preferences)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.cardinal.sdk)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}

val EXTERNAL_SERVICES_SPEC_ID: String by project
val APPLICATION_ID: String by project
val PRODUCT_BUNDLE_IDENTIFIER: String by project
val PROCESS_ID: String by project

// Task to generate xcconfig file for iOS with build configuration values
abstract class GenerateIosConfigTask : DefaultTask() {
    @get:Input
    abstract val externalServicesSpecId: Property<String>

    @get:Input
    abstract val applicationId: Property<String>

    @get:Input
    abstract val productBundleId: Property<String>

    @get:Input
    abstract val processId: Property<String>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun generate() {
        val configFile = outputFile.get().asFile
        configFile.parentFile.mkdirs()
        configFile.writeText("""
            // Auto-generated from gradle.properties - DO NOT EDIT MANUALLY
            EXTERNAL_SERVICES_SPEC_ID=${externalServicesSpecId.get()}
            APPLICATION_ID=${applicationId.get()}
            PRODUCT_BUNDLE_IDENTIFIER=${productBundleId.get()}
            PROCESS_ID=${processId.get()}
        """.trimIndent())
        println("Generated iOS config at: ${configFile.absolutePath}")
    }
}

tasks.register<GenerateIosConfigTask>("generateIosConfig") {
    externalServicesSpecId.set(EXTERNAL_SERVICES_SPEC_ID)
    applicationId.set(APPLICATION_ID)
    productBundleId.set(PRODUCT_BUNDLE_IDENTIFIER)
    processId.set(PROCESS_ID)
    outputFile.set(project.rootProject.file("iosApp/Configuration/BuildConfig.xcconfig"))
}

// Run before iOS builds
tasks.matching { it.name.startsWith("compile") && it.name.contains("Kotlin") }.configureEach {
    dependsOn("generateIosConfig")
}

android {
    namespace = "com.icure.cardinal.compose.multiplatform"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.icure.cardinal.compose.multiplatform"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        buildConfig = true
    }

    flavorDimensions += "version"

    productFlavors {
        create("patient-dev") {
            isDefault = true
            dimension = "version"
            applicationId = PRODUCT_BUNDLE_IDENTIFIER

            buildConfigField("String", "externalServicesSpecId", """"$EXTERNAL_SERVICES_SPEC_ID"""")
            buildConfigField("String", "applicationId", """"$APPLICATION_ID"""")
            buildConfigField("String", "processId", """"$PROCESS_ID"""")
        }
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

// Configure JVM arguments for all JavaExec tasks (including jvmRun used by IntelliJ)
tasks.withType<JavaExec>().configureEach {
    systemProperty("EXTERNAL_SERVICES_SPEC_ID", EXTERNAL_SERVICES_SPEC_ID)
    systemProperty("APPLICATION_ID", APPLICATION_ID)
    systemProperty("PROCESS_ID", PROCESS_ID)
    systemProperty("KEY_STORAGE_PATH", "${project.rootDir.absolutePath}/keyStorage")
}

compose.desktop {
    application {
        mainClass = "com.icure.cardinal.compose.multiplatform.MainKt"

        jvmArgs(
            "-DEXTERNAL_SERVICES_SPEC_ID=${EXTERNAL_SERVICES_SPEC_ID}",
            "-DAPPLICATION_ID=${APPLICATION_ID}",
            "-DPROCESS_ID=${PROCESS_ID}",
            "-DKEY_STORAGE_PATH=${project.rootDir.absolutePath}/keyStorage"
        )

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.icure.cardinal.compose.multiplatform"
            packageVersion = "1.0.0"
        }
    }
}
