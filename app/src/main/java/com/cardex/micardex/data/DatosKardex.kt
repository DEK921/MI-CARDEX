package com.cardex.micardex.data

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Datos iniciales del kardex (Seguimiento del Alumno, TecNM / I.T. Tuxtla Gutiérrez).
 * Se insertan automáticamente cuando se crea la base de datos.
 *
 * Periodos → semestre:
 *   1 = Ene-Jun 2023   2 = Ago-Dic 2023   3 = Ene-Jun 2024 (+ Verano 2024)
 *   4 = Ago-Dic 2024   5 = Ene-Jun 2025   6 = Ago-Dic 2025   7 = Ene-Jun 2026
 *
 * La "Evaluación Complementaria" es parte del curso normal, por eso va en cal_normal.
 * CALC.VECTORIAL: N/A en curso normal (no aprobada) y 79 en Repetición (Verano 2024).
 */
object DatosKardex {

    // nombre, semestre, normal, repetición, especial
    val materias: List<Materia> = listOf(
        // Semestre 1 — Enero-Junio 2023
        m("CALC.DIFER.", 1, 70),
        m("FUND. DE PROG.", 1, 88),
        m("TALL.DE ETICA", 1, 82),
        m("MAT.DISCRETAS", 1, 71),
        m("TALL.ADMON.", 1, 84),
        m("FUND.INVEST.", 1, 100),
        // Semestre 2 — Agosto-Diciembre 2023
        m("QUIMICA", 2, 81),
        m("CONTAB.FINANC.", 2, 86),
        m("PROG. ORIEN. A OBJ.", 2, 70),
        m("ALG.LINEAL", 2, 90),
        m("DESARROLLO SUSTENTABLE", 2, 100),
        m("FISICA GRAL.", 2, 75),
        m("CALC.INTEGRAL", 2, 70),
        // Semestre 3 — Enero-Junio 2024 (Cálc. Vectorial se repitió en Verano 2024)
        m("CALC.VECTORIAL", 3, null, repeticion = 79),
        m("TALLER DE SIST.OPERA", 3, 76),
        m("FUND.BASES.DATOS", 3, 70),
        m("PROB.Y ESTAD.", 3, 82),
        m("INV.DE OPERAC.", 3, 74),
        m("PRINC.ELEC.APLIC.DIG", 3, 85),
        m("EST.DATOS", 3, 82),
        // Semestre 4 — Agosto-Diciembre 2024
        m("TALLER.BASES.DATOS", 4, 75),
        m("TOP.AVANZ.DE PROG.", 4, 86),
        m("FUND. DE TELEC.", 4, 70),
        m("SIMULACION", 4, 85),
        m("ARQ.DE COMPUTADORAS", 4, 93),
        m("ECUAC.DIFER.", 4, 73),
        m("METOD.NUMERICOS", 4, 70),
        // Semestre 5 — Enero-Junio 2025
        m("PROG. WEB", 5, 98),
        m("SIST.OPERATIVOS", 5, 91),
        m("ADMON.BASE D.", 5, 79),
        m("LENG.Y AUTOM.I", 5, 70),          // Evaluación Complementaria
        m("REDES DE COMP.", 5, 90),
        m("FUND. DE. ING. SOFT.", 5, 92),
        // Semestre 6 — Agosto-Diciembre 2025
        m("LENGUAJES Y AUT, II", 6, 80),
        m("CULT.EMPRES.", 6, 97),
        m("CONM.ENRUT.RED.DATOS", 6, 70),
        m("ING.DE SOFTWARE", 6, 73),
        m("LENGUAJES DE INTERFAZ", 6, 77),
        m("GRAFICACION", 6, 96),
        // Semestre 7 — Enero-Junio 2026
        m("GEST. PROY.SOFT.", 7, 71),        // Evaluación Complementaria
        m("PROG. LOG. Y FUNCIONAL", 7, 79),
        m("PRO. PROY. Y PLNS. TECNOLOGICOS", 7, 85),
        m("E-BUSINESS", 7, 85),
        m("TALL.INVEST.I", 7, 85),
        m("ADMON.DE REDES", 7, 96),
        m("SISTEMAS PROGR.", 7, 70),
    )

    private fun m(nombre: String, semestre: Int, normal: Int?, repeticion: Int? = null, especial: Int? = null) =
        Materia(nombre = nombre, semestre = semestre, calNormal = normal,
            calRepeticion = repeticion, calEspecial = especial)

    /** Inserta todas las materias con SQL directo (se ejecuta dentro de la creación de la BD). */
    fun insertar(db: SupportSQLiteDatabase) {
        materias.forEach {
            db.execSQL(
                "INSERT INTO materias (nombre, semestre, cal_normal, cal_repeticion, cal_especial) " +
                    "VALUES (?, ?, ?, ?, ?)",
                arrayOf<Any?>(it.nombre, it.semestre, it.calNormal, it.calRepeticion, it.calEspecial)
            )
        }
    }

    /**
     * Callback de Room:
     * - onCreate: la BD se crea por primera vez (instalación nueva) → se cargan los datos.
     * - onDestructiveMigration: la BD se va a recrear al subir de versión. Room lo llama
     *   ANTES de volver a crear la tabla, así que solo se marca como pendiente...
     * - onOpen: ...y aquí, con la tabla ya creada, se cargan los datos.
     */
    val callback = object : RoomDatabase.Callback() {
        private var pendiente = false

        override fun onCreate(db: SupportSQLiteDatabase) = insertar(db)

        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
            pendiente = true
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            if (pendiente) {
                insertar(db)
                pendiente = false
            }
        }
    }
}
