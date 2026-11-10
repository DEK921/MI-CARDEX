package com.cardex.micardex.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object): define CÓMO se accede a la tabla "materias".
 * Room lee estas anotaciones y genera automáticamente el código SQL/Java.
 *
 * - Las funciones "suspend" se ejecutan en segundo plano (corrutinas).
 * - Las que regresan Flow se actualizan solas cuando cambia la tabla.
 */
@Dao
interface MateriaDao {

    // ---------- CRUD ----------

    @Insert
    suspend fun insertar(materia: Materia)

    @Update
    suspend fun actualizar(materia: Materia)

    @Delete
    suspend fun eliminar(materia: Materia)

    @Query("SELECT * FROM materias ORDER BY semestre, nombre")
    fun obtenerTodas(): Flow<List<Materia>>

    @Query("SELECT * FROM materias WHERE id = :id")
    suspend fun obtenerPorId(id: Int): Materia?

    // ---------- CONSULTAS ----------
    // COALESCE toma el primer valor no nulo: Especial, luego Repetición, luego Normal.
    // AVG ignora los renglones donde todo es NULL (materias aún sin calificación).

    @Query(
        """
        SELECT AVG(COALESCE(cal_especial, cal_repeticion, cal_normal))
        FROM materias
        """
    )
    fun promedioGeneral(): Flow<Double?>

    @Query(
        """
        SELECT semestre,
               AVG(COALESCE(cal_especial, cal_repeticion, cal_normal)) AS promedio,
               COUNT(*) AS materias
        FROM materias
        GROUP BY semestre
        ORDER BY semestre
        """
    )
    fun promedioPorSemestre(): Flow<List<PromedioSemestre>>
}
