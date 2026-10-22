# MisCosas

MisCosas será un pasaporte digital personal y familiar para registrar objetos, conservar su documentación y controlar compras, garantías, devoluciones y mantenimiento.

El proyecto ya contiene el baseline Kotlin Multiplatform, interfaces nativas, modelos de dominio, persistencia Room, repositorios locales con outbox y parte de la lógica de registro, perfil y sesión. Firebase y la sincronización remota todavía no están integrados. Estos componentes no equivalen a los flujos completos de la V1.

Actualmente se está definiendo el producto mediante SDD/SDMD. El alcance aprobado, las propuestas y las cuestiones pendientes están en [PRODUCT_REQUIREMENTS_V1.md](docs/PRODUCT_REQUIREMENTS_V1.md). El [estado de desarrollo](docs/DEVELOPMENT_STATUS.md) distingue lo implementado de lo previsto y fija el siguiente paso. Antes de continuar, leer [AGENTS.md](AGENTS.md); para una sesión nueva, usar [CONTINUATION_PROMPT.md](docs/CONTINUATION_PROMPT.md). El [brief original](docs/PRODUCT_BRIEF_ORIGINAL.md) se conserva como referencia histórica.

## Decisiones de plataforma

| Plataforma | Interfaz | Versión mínima | Identificador |
| --- | --- | --- | --- |
| Android | Jetpack Compose | API 29 | `com.rafario.miscosas` |
| iOS | SwiftUI, solo iPhone inicialmente | iOS 17.0 | `com.rafario.miscosas` |

La lógica que aporte valor compartir se escribirá en Kotlin Multiplatform. Las interfaces y su ciclo de vida serán nativos de cada plataforma.

## Estructura actual

- `androidApp`: aplicación Android y presentación nativa con Jetpack Compose.
- `iosApp`: aplicación nativa SwiftUI e integración con el framework Kotlin `SharedLogic`.
- `sharedLogic`: código Kotlin compartido entre Android e iOS.

`androidApp` depende directamente de `sharedLogic`, mientras que `iosApp` consume su framework. `sharedLogic` no depende de Jetpack Compose ni de SwiftUI.

## Convenciones de paquetes

- `presentation` pertenece a cada aplicación nativa: Jetpack Compose en Android y SwiftUI en iOS.
- `domain` contiene modelos, reglas, validaciones, contratos de repositorio y casos de uso independientes de la plataforma.
- `data` contiene persistencia, adaptadores e implementaciones de los repositorios definidos por dominio.
- `domain` no dependerá de `data`; `data` sí podrá depender de `domain`.
- Los source sets `androidMain` e `iosMain` se usarán solo cuando una implementación específica de plataforma aporte valor real.

Los paquetes y abstracciones se crearán cuando aparezca su primer tipo real. No se mantendrán carpetas vacías ni interfaces que solo reenvíen llamadas.

## Dominio compartido

`sharedLogic` contiene modelos independientes de Android, iOS, Firebase y cualquier base de datos:

- identidad, hogares y miembros;
- objetos, categorías y compras;
- garantías y periodos de devolución;
- documentos y sus metadatos;
- tareas y registros de mantenimiento;
- historial relevante de los objetos.

Las fechas civiles se representan mediante `LocalDate`, mientras que las marcas temporales de auditoría utilizan `Instant` UTC. El dinero se almacena en unidades mínimas enteras junto con su moneda.

Los estados temporales de garantías, devoluciones y mantenimiento se calculan a partir de sus fechas. No se persisten alertas, estadísticas ni información derivada.

## Toolchain actual

- Kotlin 2.4.10.
- Gradle 9.3.1.
- Android Gradle Plugin 9.1.1.
- JDK 21; el baseline utilizó la distribución Azul.
- Android `compileSdk` y `targetSdk` 37.
- Jetpack Compose Android alineado mediante BOM 2026.05.01.
- Baseline iOS verificado históricamente con Xcode 26.6 en un Mac con Apple Silicon; no se ha vuelto a ejecutar durante esta consolidación documental.

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

La suite compartida cubre las invariantes y cálculos del dominio y se ejecuta en Android e iOS:

```bash
./gradlew :sharedLogic:allTests
```

Para comprobar también la exportación del framework utilizado por Swift:

```bash
./gradlew :sharedLogic:linkDebugFrameworkIosSimulatorArm64
```

La consolidación funcional está versionada; el siguiente bloque pendiente de definición es cierre de sesión y cambio de cuenta, cuya propuesta aún debe aprobarse. Consulta [DEVELOPMENT_STATUS.md](docs/DEVELOPMENT_STATUS.md) para retomar paso a paso. Las futuras SPEC, PLAN y TASKS desarrollarán el alcance aprobado por pasos; esta documentación no autoriza comenzar su implementación.
