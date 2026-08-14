# MisCosas

MisCosas será un pasaporte digital personal y familiar para registrar objetos, conservar su documentación y controlar compras, garantías, devoluciones y mantenimiento.

La **Fase 1** está completada: el baseline Kotlin Multiplatform está configurado y las interfaces de Android e iOS viven en sus respectivas aplicaciones. Todavía no se han implementado modelos de negocio, persistencia, autenticación ni sincronización.

## Decisiones de plataforma

| Plataforma | Interfaz | Versión mínima | Identificador |
| --- | --- | --- | --- |
| Android | Jetpack Compose | API 24 | `com.rafario.miscosas` |
| iOS | SwiftUI, solo iPhone inicialmente | iOS 17.0 | `com.rafario.miscosas` |

La lógica que aporte valor compartir se escribirá en Kotlin Multiplatform. Las interfaces y su ciclo de vida serán nativos de cada plataforma.

## Estructura actual

- `androidApp`: aplicación Android y presentación nativa con Jetpack Compose.
- `iosApp`: aplicación nativa SwiftUI e integración con el framework Kotlin `SharedLogic`.
- `sharedLogic`: código Kotlin compartido entre Android e iOS.

`androidApp` depende directamente de `sharedLogic`, mientras que `iosApp` consume su framework. `sharedLogic` no depende de Jetpack Compose ni de SwiftUI.

## Convenciones de paquetes

- `presentation` pertenece a cada aplicación nativa: Jetpack Compose en Android y SwiftUI en iOS.
- `domain` contendrá modelos, reglas, validaciones y contratos de repositorio independientes de la plataforma.
- `data` contendrá persistencia, adaptadores e implementaciones de los repositorios definidos por dominio.
- `domain` no dependerá de `data`; `data` sí podrá depender de `domain`.
- Los source sets `androidMain` e `iosMain` se usarán solo cuando una implementación específica de plataforma aporte valor real.

Los paquetes y abstracciones se crearán cuando aparezca su primer tipo real. No se mantendrán carpetas vacías ni interfaces que solo reenvíen llamadas.

Hasta que la Fase 2 introduzca el primer tipo de dominio, `sharedLogic` conserva un archivo fuente sin declaraciones. Kotlin/Native necesita al menos una fuente para generar el framework que integra Xcode; ese anclaje temporal no expone ninguna API.

## Toolchain del baseline

- Kotlin 2.4.10.
- Gradle 9.1.0.
- Android Gradle Plugin 9.0.1.
- Daemon de Gradle con Azul JDK 21.
- Android `compileSdk` y `targetSdk` 36.
- Jetpack Compose Android alineado mediante BOM 2026.05.01.
- Baseline iOS verificado localmente con Xcode 26.6 en un Mac con Apple Silicon.

## Ejecutar Android

Desde la raíz del repositorio:

```bash
./gradlew :androidApp:assembleDebug
```

También se puede seleccionar la configuración `androidApp` y un dispositivo Android desde Android Studio.

## Ejecutar iOS

Se puede seleccionar la configuración `iosApp` y un simulador compatible desde Android Studio, o abrir el proyecto en Xcode:

```bash
open iosApp/iosApp.xcodeproj
```

El esquema compartido se llama `iosApp`. Para comprobar el build sin firma desde terminal:

```bash
xcodebuild \
  -project iosApp/iosApp.xcodeproj \
  -scheme iosApp \
  -configuration Debug \
  -destination 'generic/platform=iOS Simulator' \
  -derivedDataPath build/xcode-derived \
  CODE_SIGNING_ALLOWED=NO \
  build
```

Si Android Studio muestra `destination not found` después de actualizar Xcode, sus runtimes o el proyecto, hay que volver a seleccionar en la barra del IDE un simulador reconocido por la versión activa de Xcode. La versión del simulador no modifica el mínimo iOS 17.0 de la aplicación.

Para ejecutar en un iPhone físico será necesario seleccionar un equipo de desarrollo en `Signing & Capabilities`; no hace falta para el simulador.

## Tests

Los tests aritméticos generados por la plantilla se han eliminado porque no comprobaban comportamiento del producto. La suite compartida comenzará con los primeros modelos y reglas de dominio.

El siguiente paso es la **Fase 2: fundamentos y modelos de dominio**. Se introducirán los tipos de uno en uno, junto con sus invariantes y tests reales.
