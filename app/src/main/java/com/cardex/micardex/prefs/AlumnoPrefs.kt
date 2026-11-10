package com.cardex.micardex.prefs

import android.content.Context

/**
 * Guarda el número de control y el nombre del alumno con SharedPreferences.
 * Son datos permanentes (sobreviven al cerrar la app) y NO están en la base de datos:
 * se guardan en un archivo XML de pares clave-valor llamado "datos_alumno.xml".
 */
class AlumnoPrefs(context: Context) {

    private val prefs = context.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE)

    var numeroControl: String
        get() = prefs.getString(KEY_CONTROL, "") ?: ""
        set(valor) = prefs.edit().putString(KEY_CONTROL, valor).apply()

    var nombre: String
        get() = prefs.getString(KEY_NOMBRE, "") ?: ""
        set(valor) = prefs.edit().putString(KEY_NOMBRE, valor).apply()

    fun tieneDatos(): Boolean = numeroControl.isNotBlank() && nombre.isNotBlank()

    companion object {
        private const val ARCHIVO = "datos_alumno"
        private const val KEY_CONTROL = "numero_control"
        private const val KEY_NOMBRE = "nombre_alumno"
    }
}
