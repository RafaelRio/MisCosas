# MisCosas — Estado de desarrollo

Última actualización: 26 de agosto de 2026.

Este documento es el punto de reanudación del proyecto. Debe actualizarse al cerrar cada bloque de trabajo. El código y el historial de Git siguen siendo la fuente de verdad; antes de actuar hay que contrastar este documento con `git status`, los últimos commits y los archivos mencionados.

## Forma de trabajo

- Trabajar un único micro-paso cada vez.
- Explicar primero qué se construirá y por qué.
- Rafael escribe normalmente el código y los tests para aprender.
- Si Rafael pide ayuda porque no entiende un paso, se puede proporcionar el código concreto y explicarlo.
- Los cambios mecánicos de formato, imports, comas y finales de archivo puede hacerlos directamente el asistente.
- No crear repositorios, abstracciones o dependencias sin un consumidor real.
- Al terminar cada bloque: ejecutar validaciones proporcionales, revisar el staging, proponer el commit y esperar antes de avanzar.

## Producto y plataformas

MisCosas es un pasaporte digital personal y familiar para registrar objetos, compras, garantías, devoluciones, documentos y mantenimiento.

| Plataforma | Interfaz | Versión mínima | Identificador |
| --- | --- | --- | --- |
| Android | Jetpack Compose nativo | API 29 | `com.rafario.miscosas` |
| iOS | SwiftUI nativo, inicialmente solo iPhone | iOS 17 | `com.rafario.miscosas` |

No existe UI compartida. `sharedLogic` contiene el dominio y la infraestructura que aporta valor compartir.

## Toolchain actual

- Kotlin 2.4.10.
- Android Gradle Plugin 9.1.1.
- Gradle 9.3.1.
- Android `compileSdk` y `targetSdk` 37.
- KSP 2.3.11.
- Coroutines 1.11.0.
- Room 3.0.1.
- SQLite bundled 2.7.0.
- JDK 21.

## Decisiones arquitectónicas vigentes

- La aplicación es local-first y Room es la fuente de verdad de la UI.
- Una mutación local sincronizable guarda el dato y su operación de outbox dentro de la misma transacción Room.
- Firebase Auth, Firestore y Storage se incorporarán después mediante adaptadores; sus tipos no entrarán en el dominio.
- Los cambios remotos se aplicarán a Room y nunca alimentarán directamente la UI.
- El aplicador remoto no debe utilizar repositorios de escritura local porque generaría un bucle de outbox.
- Los modelos de dominio no conocen Room, Firebase, Android ni iOS.
- Se utiliza inyección por constructor y composición manual por ahora.
- Koin tiene soporte KMP, pero no se añadirá hasta que el número real de casos de uso y ViewModels justifique un contenedor.
- Room, sus entidades, DAOs e implementaciones concretas permanecen `internal` y no se exportan a Swift.
- No se crea un CRUD o repositorio por cada tabla de forma automática; los contratos nacen de casos de uso reales.

## Trabajo completado

### Baseline y dominio

- Separación de UI nativa: Compose en Android y SwiftUI en iOS.
- Modelos e invariantes de usuarios, hogares y miembros.
- Objetos, categorías, compras y dinero.
- Garantías y periodos de devolución.
- Documentos y metadatos.
- Tareas y registros de mantenimiento.
- Historial de objetos.
- IDs nominales y códigos estables para los enums persistidos.

### Persistencia local

Room contiene entidades, mappers, DAOs y tests para:

- `users`;
- `households`;
- `household_members`;
- `items`;
- `documents`;
- `warranties`;
- `return_periods`;
- `maintenance_tasks`;
- `maintenance_records`;
- `item_history_events`;
- `sync_outbox`.

También están terminados los builders Android/iOS y la configuración común con `BundledSQLiteDriver`.

### Preparación de sincronización

- `SyncOperation` y `SyncRecordType` usan códigos estables.
- `sync_outbox` no tiene claves foráneas para que una eliminación pueda sincronizarse aunque la fila de origen ya no exista.
- El outbox agrupa la última operación por objetivo, conserva un `mutationId`, ordena pendientes y elimina una operación solo si coincide el `mutationId` confirmado.
- Firebase todavía no está configurado.

### Repositorio local-first de objetos

El commit `f7a0f3b` (`Add local-first Room item repository`) contiene:

- `ItemRepository`;
- observación reactiva de Items por `HouseholdId`;
- `RoomItemRepository.findById`;
- `RoomItemRepository.observeByHouseholdId`;
- `RoomItemRepository.save`;
- guardado atómico de `ItemEntity` y `SyncOutboxEntity` con operación `UPSERT`;
- `Clock` y generador de `mutationId` inyectables;
- tests iOS de lectura, filtrado/mapeo, escritura con outbox y rollback real mediante trigger SQLite.

### Creación local-first de hogares

El bloque de creación de un hogar está implementado y validado:

- `HouseholdRepository` define una única operación de agregado para crear el hogar junto a su membresía propietaria;
- `CreateHouseholdUseCase` genera un único `HouseholdId`, consulta el reloj una vez, construye el hogar y crea al usuario actual como `OWNER` con el mismo instante;
- el caso de uso recibe `Clock` y generador de ID por constructor para permitir pruebas deterministas;
- `RoomHouseholdRepository` transforma ambos modelos a entidades Room;
- guarda `HouseholdEntity`, `HouseholdMemberEntity` y dos operaciones `UPSERT` del outbox dentro de una única `withWriteTransaction`;
- el outbox del hogar usa el propio `HouseholdId` como scope y record ID;
- el outbox de la membresía usa `HouseholdId` como scope y `UserId` como record ID, representando su identidad compuesta;
- ambas operaciones comparten el mismo instante de encolado y tienen `mutationId` independientes.

La cobertura incluye:

- test común del caso de uso mediante un repositorio capturador;
- integración iOS del camino feliz contra Room real;
- comprobación exacta de las dos entidades y los dos registros del outbox;
- un trigger SQLite que fuerza el fallo de la segunda inserción del outbox;
- comprobación de rollback completo del hogar, membresía y ambas operaciones pendientes.

La validación global posterior completó correctamente:

```text
:sharedLogic:allTests
:sharedLogic:linkDebugFrameworkIosSimulatorArm64
:sharedLogic:linkDebugFrameworkIosArm64
:androidApp:assembleDebug
```

Los únicos avisos son los conocidos sobre las declaraciones `expect`/`actual` Beta generadas y utilizadas por Room.

## Punto de reanudación

El bloque de creación local-first de hogares está terminado y validado. Antes de continuar con otro bloque hay que comprobar `git status` y `git log` para confirmar que quedó registrado correctamente.

Si estos archivos todavía aparecen como `AM`, hay que volver a añadir explícitamente sus versiones finales porque el índice contenía sus esqueletos iniciales:

```text
sharedLogic/src/commonMain/kotlin/com/rafario/miscosas/data/repository/RoomHouseholdRepository.kt
sharedLogic/src/commonMain/kotlin/com/rafario/miscosas/domain/repository/HouseholdRepository.kt
sharedLogic/src/commonMain/kotlin/com/rafario/miscosas/domain/usecase/CreateHouseholdUseCase.kt
sharedLogic/src/commonTest/kotlin/com/rafario/miscosas/domain/usecase/CreateHouseholdUseCaseTest.kt
sharedLogic/src/iosTest/kotlin/com/rafario/miscosas/data/repository/RoomHouseholdRepositoryTest.kt
docs/DEVELOPMENT_STATUS.md
```

Antes de cualquier commit hay que revisar `git diff --cached --check` y `git diff --cached`.

## Próximo bloque

Después del commit, el siguiente paso no es añadir Firebase de golpe. Primero hay que auditar los puntos de entrada Android/iOS y diseñar la composición manual mínima que permita:

1. Mantener una única instancia de `MisCosasDatabase` durante la vida de la aplicación.
2. Construir `RoomHouseholdRepository` y `CreateHouseholdUseCase` mediante inyección por constructor.
3. Introducir un contrato de sesión autenticada sin tipos Firebase en dominio.
4. Persistir el `User` autenticado antes de crear el hogar, cumpliendo las claves foráneas.
5. Exponer a cada UI una fachada o ViewModel apropiado, sin exportar Room, DAO ni `Flow` crudo a Swift.

Firebase Auth se incorporará mediante adaptadores cuando esos límites estén definidos. Firestore y el consumidor del outbox llegarán después; no deben llamarse directamente desde el caso de uso de creación.

## Dirección posterior

Cada bloque debe volver a evaluarse antes de empezar. La dirección prevista es:

1. Composición manual mediante `AppContainer` y sesión autenticada.
2. Firebase Auth mediante adaptadores de plataforma y persistencia del usuario local.
3. Conectar onboarding y creación/unión a hogares en Compose y SwiftUI.
4. Diseñar esquema, reglas y adaptadores de Firestore; después implementar subida del outbox y aplicación remota a Room.
5. Implementar verticales nativas de objetos, documentos/Storage, garantías, devoluciones, mantenimiento e historial.
6. Añadir invitaciones, permisos, alertas, fotos, búsqueda, estadísticas y exportación solo cuando sus flujos estén definidos.
7. Cerrar con migraciones Room, emuladores Firebase, CI, accesibilidad, localización, seguridad y documentación para portfolio.

## Documentación pendiente

El `README.md` está desactualizado: todavía muestra parte de la toolchain anterior y afirma que la persistencia no existe. Debe actualizarse después de registrar este bloque, preferiblemente en un commit documental separado.
