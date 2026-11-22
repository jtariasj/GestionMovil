plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kover)
}

android {
    namespace = "cl.duoc.gestionmovil"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "cl.duoc.gestionmovil"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.room.common)
    testImplementation(libs.junit)
    testImplementation(libs.junit)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.1")
    testImplementation("io.mockk:mockk:1.13.16")
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.2")
}
kover {
    reports {
        filters {
            includes {
                classes(
                    // Validaciones y reglas de negocio puras (paquete util)
                    "cl.duoc.gestionmovil.util.*",

                    // Repositories (capa de datos híbrida Retrofit + Room)
                    "cl.duoc.gestionmovil.data.OportunidadRepository*",
                    "cl.duoc.gestionmovil.data.UsuarioRepository*",
                    "cl.duoc.gestionmovil.data.OrganizacionRepository*",
                    "cl.duoc.gestionmovil.data.ContactoRepository*",
                    "cl.duoc.gestionmovil.data.SeguimientoRepository*",

                    // ViewModels (lógica de presentación, estados y permisos)
                    "cl.duoc.gestionmovil.ui.oportunidad.OportunidadFormViewModel*",
                    "cl.duoc.gestionmovil.ui.oportunidad.OportunidadListViewModel*",
                    "cl.duoc.gestionmovil.ui.oportunidad.OportunidadDetailViewModel*",
                    "cl.duoc.gestionmovil.ui.login.LoginViewModel*",
                    "cl.duoc.gestionmovil.ui.organizacion.OrganizacionViewModel*",
                    "cl.duoc.gestionmovil.ui.contacto.ContactoViewModel*",
                    "cl.duoc.gestionmovil.ui.seguimiento.SeguimientoViewModel*"
                )
            }
        }
    }
}