package com.cardex.micardex.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cardex.micardex.data.CardexDatabase
import com.cardex.micardex.data.Materia
import com.cardex.micardex.data.MateriaRepository
import com.cardex.micardex.data.PromedioSemestre
import com.cardex.micardex.prefs.AlumnoPrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * El ViewModel conecta la interfaz con los datos (Room y SharedPreferences).
 * Sobrevive a rotaciones de pantalla y ejecuta las operaciones de BD en corrutinas.
 */
class CardexViewModel(app: Application) : AndroidViewModel(app) {

    // El ViewModel ya no usa el DAO directamente: pasa por el Repository
    private val repo = MateriaRepository(CardexDatabase.obtener(app).materiaDao())
    private val prefs = AlumnoPrefs(app)

    // ---------- Datos del alumno (SharedPreferences) ----------
    private val _numeroControl = MutableStateFlow(prefs.numeroControl)
    val numeroControl: StateFlow<String> = _numeroControl.asStateFlow()

    private val _nombre = MutableStateFlow(prefs.nombre)
    val nombre: StateFlow<String> = _nombre.asStateFlow()

    fun tieneDatosAlumno() = prefs.tieneDatos()

    fun guardarAlumno(control: String, nombre: String) {
        prefs.numeroControl = control.trim()
        prefs.nombre = nombre.trim()
        _numeroControl.value = prefs.numeroControl
        _nombre.value = prefs.nombre
    }

    // ---------- Datos del cardex (Room) ----------
    val materias: StateFlow<List<Materia>> =
        repo.materias.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val promedioGeneral: StateFlow<Double?> =
        repo.promedioGeneral.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val promediosSemestre: StateFlow<List<PromedioSemestre>> =
        repo.promediosSemestre.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun guardarMateria(materia: Materia) = viewModelScope.launch {
        repo.guardar(materia)
    }

    fun eliminarMateria(materia: Materia) = viewModelScope.launch {
        repo.eliminar(materia)
    }
}
