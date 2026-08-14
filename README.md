# MisCosas

MisCosas será un pasaporte digital personal y familiar para registrar objetos, conservar su documentación y controlar compras, garantías, devoluciones y mantenimiento.

El proyecto se encuentra en la **Fase 1**: el baseline Kotlin Multiplatform está configurado y las interfaces de Android e iOS ya viven en sus respectivas aplicaciones. Todavía no se han implementado modelos de negocio, persistencia, autenticación ni sincronización.

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

## Tests del baseline

```bash
./gradlew \
  :sharedLogic:testAndroidHostTest \
  :sharedLogic:iosSimulatorArm64Test
```

El siguiente paso de la Fase 1 será retirar el código de demostración de la plantilla y fijar las convenciones de paquetes para `presentation`, `domain` y `data`. Los paquetes se crearán cuando aparezcan sus primeros tipos reales, sin añadir carpetas o abstracciones vacías.
