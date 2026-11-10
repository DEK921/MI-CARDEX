package com.cardex.micardex.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * DATABASE: punto de acceso principal a la base de datos.
 * - entities: lista de tablas (aquí solo una: Materia).
 * - version: se incrementa si cambia la estructura de la tabla.
 *
 * Se usa el patrón Singleton para que exista una sola instancia de la BD en toda la app.
 */
// version 2: al actualizar la app, la BD se recrea y se cargan los datos del kardex
@Database(entities = [Materia::class], version = 2, exportSchema = false)
abstract class CardexDatabase : RoomDatabase() {

    abstract fun materiaDao(): MateriaDao

    companion object {
        @Volatile
        private var INSTANCIA: CardexDatabase? = null

        fun obtener(context: Context): CardexDatabase =
            INSTANCIA ?: synchronized(this) {
                INSTANCIA ?: Room.databaseBuilder(
                    context.applicationContext,
                    CardexDatabase::class.java,
                    "cardex.db"          // nombre del archivo SQLite en el teléfono
                )
                    .fallbackToDestructiveMigration()   // si cambia la versión, recrea la tabla
                    .addCallback(DatosKardex.callback)  // y la llena con los datos del kardex
                    .build().also { INSTANCIA = it }
            }
    }
}
