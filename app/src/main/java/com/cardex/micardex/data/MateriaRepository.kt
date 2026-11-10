package com.cardex.micardex.data

import kotlinx.coroutines.flow.Flow

/**
 * REPOSITORY: intermediario entre el ViewModel y la fuente de datos (el DAO de Room).
 * El ViewModel no sabe de dónde vienen los datos; solo le pide al repositorio.
 * Si en el futuro los datos vinieran también de internet, solo cambiaría esta clase.
 */
class MateriaRepository(private val dao: MateriaDao) {

    // Lecturas: se exponen como Flow para que la interfaz se actualice sola
    val materias: Flow<List<Materia>> = dao.obtenerTodas()
    val promedioGeneral: Flow<Double?> = dao.promedioGeneral()
    val promediosSemestre: Flow<List<PromedioSemestre>> = dao.promedioPorSemestre()

    /** Decide si la materia es nueva (id = 0 → INSERT) o ya existe (UPDATE). */
    suspend fun guardar(materia: Materia) {
        if (materia.id == 0) dao.insertar(materia) else dao.actualizar(materia)
    }

    suspend fun eliminar(materia: Materia) = dao.eliminar(materia)
}
