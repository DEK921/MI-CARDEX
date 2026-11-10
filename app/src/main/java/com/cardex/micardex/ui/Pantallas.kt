@file:OptIn(ExperimentalMaterial3Api::class)

package com.cardex.micardex.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.cardex.micardex.data.Materia
import com.cardex.micardex.data.PromedioSemestre
import java.util.Locale

private fun formato(valor: Double?): String =
    if (valor == null) "—" else String.format(Locale.US, "%.2f", valor)

// =====================================================================
// 1) PANTALLA DE DATOS DEL ALUMNO (se guardan en SharedPreferences)
// =====================================================================
@Composable
fun PantallaAlumno(
    controlInicial: String,
    nombreInicial: String,
    puedeRegresar: Boolean,
    onGuardar: (String, String) -> Unit,
    onRegresar: () -> Unit
) {
    var control by rememberSaveable { mutableStateOf(controlInicial) }
    var nombre by rememberSaveable { mutableStateOf(nombreInicial) }
    val valido = control.isNotBlank() && nombre.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Datos del alumno") },
                navigationIcon = {
                    if (puedeRegresar) IconButton(onClick = onRegresar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Regresar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Estos datos se guardan de forma permanente en el teléfono " +
                    "(SharedPreferences), fuera de la base de datos.",
                style = MaterialTheme.typography.bodyMedium
            )
            OutlinedTextField(
                value = control, onValueChange = { control = it },
                label = { Text("Número de control") }, singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = nombre, onValueChange = { nombre = it },
                label = { Text("Nombre completo") }, singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = { onGuardar(control, nombre) },
                enabled = valido,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Guardar") }
        }
    }
}

// =====================================================================
// 2) PANTALLA PRINCIPAL: LISTA DEL CARDEX
// =====================================================================
@Composable
fun PantallaCardex(
    titulo: String,
    numeroControl: String,
    nombre: String,
    materias: List<Materia>,
    promedioGeneral: Double?,
    onAgregar: () -> Unit,
    onEditar: (Materia) -> Unit,
    onConsultas: () -> Unit,
    onEditarAlumno: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titulo) },
                actions = {
                    TextButton(onClick = onConsultas) { Text("Promedios") }
                    IconButton(onClick = onEditarAlumno) { Icon(Icons.Filled.Person, "Datos del alumno") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAgregar) { Icon(Icons.Filled.Add, "Agregar materia") }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 88.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("No. de control: $numeroControl", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Promedio general: ${formato(promedioGeneral)}",
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
            }

            if (materias.isEmpty()) {
                item {
                    Text(
                        "Aún no hay materias registradas. Toca + para agregar la primera.",
                        modifier = Modifier.padding(top = 24.dp)
                    )
                }
            }

            // Agrupa visualmente las materias por semestre
            materias.groupBy { it.semestre }.toSortedMap().forEach { (semestre, lista) ->
                item(key = "sem$semestre") {
                    Text(
                        "Semestre $semestre",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
                items(lista, key = { it.id }) { materia ->
                    TarjetaMateria(materia, onClick = { onEditar(materia) })
                }
            }
        }
    }
}

@Composable
private fun TarjetaMateria(materia: Materia, onClick: () -> Unit) {
    val calFinal = materia.calificacionFinal
    val aprobada = calFinal != null && calFinal >= 70
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(materia.nombre, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(
                    "N: ${materia.calNormal ?: "—"}   R: ${materia.calRepeticion ?: "—"}   E: ${materia.calEspecial ?: "—"}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    calFinal?.toString() ?: "—",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (calFinal == null) MaterialTheme.colorScheme.onSurfaceVariant
                    else if (aprobada) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error
                )
                Text(materia.tipoFinal, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

// =====================================================================
// 3) FORMULARIO PARA AGREGAR / EDITAR UNA MATERIA
// =====================================================================
@Composable
fun PantallaMateria(
    materia: Materia?,                 // null = materia nueva
    onGuardar: (Materia) -> Unit,
    onEliminar: (Materia) -> Unit,
    onRegresar: () -> Unit
) {
    var nombre by rememberSaveable { mutableStateOf(materia?.nombre ?: "") }
    var semestre by rememberSaveable { mutableStateOf(materia?.semestre?.toString() ?: "") }
    var normal by rememberSaveable { mutableStateOf(materia?.calNormal?.toString() ?: "") }
    var repeticion by rememberSaveable { mutableStateOf(materia?.calRepeticion?.toString() ?: "") }
    var especial by rememberSaveable { mutableStateOf(materia?.calEspecial?.toString() ?: "") }
    var confirmarBorrado by remember { mutableStateOf(false) }

    // Validaciones
    fun calValida(t: String) = t.isBlank() || (t.toIntOrNull()?.let { it in 0..100 } ?: false)
    val semestreNum = semestre.toIntOrNull()
    val semestreOk = semestreNum != null && semestreNum in 1..15
    val todoOk = nombre.isNotBlank() && semestreOk &&
        calValida(normal) && calValida(repeticion) && calValida(especial)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (materia == null) "Nueva materia" else "Editar materia") },
                navigationIcon = {
                    IconButton(onClick = onRegresar) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Regresar") }
                },
                actions = {
                    if (materia != null) IconButton(onClick = { confirmarBorrado = true }) {
                        Icon(Icons.Filled.Delete, "Eliminar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = nombre, onValueChange = { nombre = it },
                label = { Text("Nombre de la materia") }, singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            CampoNumero("Semestre (1–15)", semestre, { semestre = it }, error = semestre.isNotBlank() && !semestreOk)
            HorizontalDivider()
            Text("Calificaciones (0–100). Deja vacío lo que no aplique.", style = MaterialTheme.typography.bodySmall)
            CampoNumero("Curso Normal", normal, { normal = it }, error = !calValida(normal))
            CampoNumero("Repetición", repeticion, { repeticion = it }, error = !calValida(repeticion))
            CampoNumero("Especial", especial, { especial = it }, error = !calValida(especial))

            Button(
                onClick = {
                    onGuardar(
                        Materia(
                            id = materia?.id ?: 0,
                            nombre = nombre.trim(),
                            semestre = semestreNum!!,
                            calNormal = normal.toIntOrNull(),
                            calRepeticion = repeticion.toIntOrNull(),
                            calEspecial = especial.toIntOrNull()
                        )
                    )
                },
                enabled = todoOk,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Guardar") }
        }
    }

    if (confirmarBorrado && materia != null) {
        AlertDialog(
            onDismissRequest = { confirmarBorrado = false },
            title = { Text("Eliminar materia") },
            text = { Text("¿Eliminar \"${materia.nombre}\" del cardex?") },
            confirmButton = {
                TextButton(onClick = { confirmarBorrado = false; onEliminar(materia) }) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { confirmarBorrado = false }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun CampoNumero(etiqueta: String, valor: String, onCambio: (String) -> Unit, error: Boolean) {
    OutlinedTextField(
        value = valor,
        onValueChange = { nuevo -> if (nuevo.all { it.isDigit() } && nuevo.length <= 3) onCambio(nuevo) },
        label = { Text(etiqueta) },
        isError = error,
        supportingText = { if (error) Text("Valor fuera de rango") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )
}

// =====================================================================
// 4) CONSULTAS: PROMEDIO GENERAL Y POR SEMESTRE
// =====================================================================
@Composable
fun PantallaConsultas(
    promedioGeneral: Double?,
    porSemestre: List<PromedioSemestre>,
    onRegresar: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Promedios") },
                navigationIcon = {
                    IconButton(onClick = onRegresar) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Regresar") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Promedio general", style = MaterialTheme.typography.titleMedium)
                        Text(formato(promedioGeneral), style = MaterialTheme.typography.displaySmall)
                    }
                }
            }
            item {
                Text(
                    "Promedio por semestre",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
            if (porSemestre.isEmpty()) item { Text("Sin datos todavía.") }
            items(porSemestre, key = { it.semestre }) { fila ->
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Semestre ${fila.semestre}", style = MaterialTheme.typography.bodyLarge)
                            Text("${fila.materias} materia(s)", style = MaterialTheme.typography.bodySmall)
                        }
                        Text(formato(fila.promedio), style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
            item {
                Text(
                    "Se toma la calificación del último intento: Especial, si no Repetición, si no Normal.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
    }
}
