# Mi Cardex (Actividad A2.2)

App Android en Kotlin + Jetpack Compose para llevar el control del cardex.

## Cómo abrirla
1. Descomprime el .zip.
2. En Android Studio: **File → Open** y elige la carpeta `MiCardex`.
3. Espera a que termine el *Gradle Sync* (descarga dependencias; necesita internet).
   Si Android Studio ofrece crear/actualizar el Gradle Wrapper, acepta.
4. Ejecuta en un emulador o teléfono (Android 7.0 / API 24 o superior).

## Antes de la demostración
El nombre ya incluye los últimos tres dígitos del número de control (116), en
`app/src/main/res/values/strings.xml`:

```xml
<string name="app_name">Mi Cardex 116</string>
```

## Estructura
| Archivo | Rol |
|---|---|
| `data/Materia.kt` | **Entity**: la única tabla `materias` |
| `data/MateriaDao.kt` | **DAO**: insertar, actualizar, eliminar, consultas de promedio |
| `data/CardexDatabase.kt` | **Database**: crea `cardex.db` (Singleton) |
| `data/MateriaRepository.kt` | **Repository**: intermediario entre el ViewModel y el DAO |
| `prefs/AlumnoPrefs.kt` | **SharedPreferences**: número de control y nombre |
| `ui/CardexViewModel.kt` | Conecta la interfaz con Room y SharedPreferences |
| `ui/Pantallas.kt` | Pantallas: Alumno, Cardex, Materia, Promedios |
| `MainActivity.kt` | Punto de entrada y navegación |

## Regla de promedios
Cada materia cuenta con la calificación de su último intento:
Especial → si no hay, Repetición → si no hay, Normal. Las materias sin calificación no cuentan.

## Ver la base de datos (opcional, útil para capturas)
Android Studio → **View → Tool Windows → App Inspection → Database Inspector**:
ahí se ve la tabla `materias` con sus renglones en vivo.
