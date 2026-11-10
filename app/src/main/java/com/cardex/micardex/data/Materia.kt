package com.cardex.micardex.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * ENTITY: representa la ÚNICA tabla de la base de datos ("materias").
 * Cada propiedad se convierte en una columna; cada objeto Materia es un renglón.
 *
 * Las calificaciones son opcionales (Int?) porque no todas las materias
 * se cursan en Repetición o en Especial.
 */
@Entity(tableName = "materias")
data class Materia(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "nombre")
    val nombre: String,

    @ColumnInfo(name = "semestre")
    val semestre: Int,

    @ColumnInfo(name = "cal_normal")
    val calNormal: Int?,

    @ColumnInfo(name = "cal_repeticion")
    val calRepeticion: Int?,

    @ColumnInfo(name = "cal_especial")
    val calEspecial: Int?
) {
    /**
     * Calificación que cuenta para el promedio: la del último intento.
     * Especial > Repetición > Normal. (No se guarda en la tabla, se calcula.)
     */
    val calificacionFinal: Int?
        get() = calEspecial ?: calRepeticion ?: calNormal

    /** Origen de la calificación final, para mostrarlo en pantalla. */
    val tipoFinal: String
        get() = when {
            calEspecial != null -> "Especial"
            calRepeticion != null -> "Repetición"
            calNormal != null -> "Normal"
            else -> "Sin calificación"
        }
}

/**
 * Resultado de la consulta "promedio por semestre".
 * No es una tabla: es solo la forma que tiene el resultado del SELECT ... GROUP BY.
 */
data class PromedioSemestre(
    val semestre: Int,
    val promedio: Double?,
    val materias: Int
)
