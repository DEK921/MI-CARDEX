package com.cardex.micardex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cardex.micardex.data.Materia
import com.cardex.micardex.ui.CardexViewModel
import com.cardex.micardex.ui.PantallaAlumno
import com.cardex.micardex.ui.PantallaCardex
import com.cardex.micardex.ui.PantallaConsultas
import com.cardex.micardex.ui.PantallaMateria

/** Pantallas de la app (navegación sencilla sin librerías extra). */
sealed interface Pantalla {
    data object Alumno : Pantalla
    data object Cardex : Pantalla
    data class EditarMateria(val materia: Materia?) : Pantalla
    data object Consultas : Pantalla
}

class MainActivity : ComponentActivity() {

    private val viewModel: CardexViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                App(viewModel)
            }
        }
    }
}

@Composable
fun App(vm: CardexViewModel) {
    // Si no hay datos del alumno guardados, la app inicia pidiéndolos.
    var pantalla by remember {
        mutableStateOf<Pantalla>(if (vm.tieneDatosAlumno()) Pantalla.Cardex else Pantalla.Alumno)
    }

    val control by vm.numeroControl.collectAsStateWithLifecycle()
    val nombre by vm.nombre.collectAsStateWithLifecycle()
    val materias by vm.materias.collectAsStateWithLifecycle()
    val promedio by vm.promedioGeneral.collectAsStateWithLifecycle()
    val porSemestre by vm.promediosSemestre.collectAsStateWithLifecycle()

    // El botón "atrás" del teléfono regresa a la lista en lugar de cerrar la app
    BackHandler(enabled = pantalla != Pantalla.Cardex && vm.tieneDatosAlumno()) {
        pantalla = Pantalla.Cardex
    }

    when (val p = pantalla) {
        Pantalla.Alumno -> PantallaAlumno(
            controlInicial = control,
            nombreInicial = nombre,
            puedeRegresar = vm.tieneDatosAlumno(),
            onGuardar = { c, n -> vm.guardarAlumno(c, n); pantalla = Pantalla.Cardex },
            onRegresar = { pantalla = Pantalla.Cardex }
        )

        Pantalla.Cardex -> PantallaCardex(
            titulo = stringResource(R.string.app_name),
            numeroControl = control,
            nombre = nombre,
            materias = materias,
            promedioGeneral = promedio,
            onAgregar = { pantalla = Pantalla.EditarMateria(null) },
            onEditar = { pantalla = Pantalla.EditarMateria(it) },
            onConsultas = { pantalla = Pantalla.Consultas },
            onEditarAlumno = { pantalla = Pantalla.Alumno }
        )

        is Pantalla.EditarMateria -> PantallaMateria(
            materia = p.materia,
            onGuardar = { vm.guardarMateria(it); pantalla = Pantalla.Cardex },
            onEliminar = { vm.eliminarMateria(it); pantalla = Pantalla.Cardex },
            onRegresar = { pantalla = Pantalla.Cardex }
        )

        Pantalla.Consultas -> PantallaConsultas(
            promedioGeneral = promedio,
            porSemestre = porSemestre,
            onRegresar = { pantalla = Pantalla.Cardex }
        )
    }
}
