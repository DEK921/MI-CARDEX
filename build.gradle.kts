// Archivo de compilación del proyecto (nivel raíz)
plugins {
    id("com.android.application") version "8.5.2" apply false
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
    // KSP: procesador de anotaciones que usa Room para generar el código de la BD
    id("com.google.devtools.ksp") version "2.0.21-1.0.28" apply false
}
