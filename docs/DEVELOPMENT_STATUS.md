# MisCosas — Estado de desarrollo

Última actualización: 28 de septiembre de 2026.

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

### Persistencia local-first del perfil de usuario

El bloque de persistencia local del usuario está implementado y validado:

- `UserRepository` define la operación de escritura que necesita el flujo de alta;
- `RoomUserRepository` convierte el modelo de dominio y construye una operación `USER/UPSERT`;
- el `UserId` se utiliza como `scopeId` y `recordId`, porque el usuario es una entidad raíz y no pertenece a un hogar;
- el reloj y el generador de `mutationId` se reciben por constructor para permitir pruebas deterministas;
- `UserEntity` y `SyncOutboxEntity` se guardan dentro de una única `withWriteTransaction`;
- el camino feliz comprueba exactamente tanto el perfil como la operación pendiente;
- un trigger SQLite fuerza el fallo del outbox y demuestra que la transacción también revierte la inserción del usuario.

`RoomUserRepository.save()` representa una mutación local que debe sincronizarse. No debe utilizarse para aplicar descargas de Firebase ni ejecutarse indiscriminadamente en cada inicio de sesión, porque generaría nuevas operaciones de outbox.

La validación global completó correctamente:

```text
:sharedLogic:allTests
:sharedLogic:linkDebugFrameworkIosSimulatorArm64
:androidApp:assembleDebug
```

### Creación del perfil después del registro

`CreateUserUseCase` está implementado y validado para el momento inmediatamente posterior a un registro exitoso:

- recibe el `UserId` opaco devuelto por autenticación y el nombre introducido en el formulario;
- consulta el reloj exactamente una vez;
- construye un `User` con `createdAt` y `updatedAt` iguales;
- delega la escritura local-first en `UserRepository`;
- el test utiliza un repositorio capturador y verifica el usuario completo;
- el reloj falso cuenta sus invocaciones para evitar que dos lecturas pasen inadvertidas.

Este caso de uso crea un perfil nuevo. No debe ejecutarse en cada login o restauración de sesión de un usuario existente, porque reemplazaría el significado de `createdAt` y generaría una mutación local nueva.

### Registro por email independiente del proveedor

El camino feliz del registro por email está implementado y validado sin depender todavía del SDK de Firebase:

- `AuthenticationRepository` define el puerto neutral `registerWithEmail(email, password): UserId`;
- el contrato no expone `FirebaseUser`, tokens ni otros tipos del proveedor;
- `RegisterWithEmailUseCase` envía las credenciales al puerto de autenticación;
- utiliza el UID devuelto junto al nombre del formulario para invocar `CreateUserUseCase`;
- devuelve el mismo `UserId` al consumidor;
- la contraseña no entra en el modelo `User`, Room ni el outbox;
- el nombre se valida antes de cualquier llamada remota, evitando cuentas sin perfil por una entrada local inválida;
- si autenticación falla, el error se propaga y no se intenta crear el perfil local;
- los tests combinan autenticación falsa, `CreateUserUseCase` real y un repositorio de usuarios capturador para comprobar el flujo y el orden de sus efectos.

Firebase Auth y Room no comparten una transacción. Si la cuenta remota se crea pero falla el perfil local, no se debe intentar ocultar el problema borrando automáticamente la cuenta: habrá que conservar la sesión autenticada y ofrecer una recuperación o reintento del perfil local.

El fallo parcial ya queda identificado explícitamente:

- `RegistrationPartiallyCompletedException` indica que autenticación terminó correctamente pero no pudo guardarse el perfil local;
- conserva el `UserId` autenticado, el nombre necesario para el reintento inmediato y la causa técnica original;
- solo envuelve errores posteriores a la autenticación, por lo que un fallo del proveedor conserva su tipo original;
- una `CancellationException` se propaga sin envolver para respetar la cancelación estructurada de coroutines;
- los tests verifican las credenciales enviadas, el usuario que se intentó guardar, el número de intentos, la causa original y la cancelación.

Esta excepción es interna. La futura fachada pública no debe exportarla directamente a Swift: la transformará en un resultado interoperable como `COMPLETED` o `LOCAL_PROFILE_PENDING`.

## Punto de reanudación

La persistencia local-first del usuario, la creación del perfil y el registro por email están implementados hasta la identificación del fallo parcial. `CompleteUserProfileUseCase` ya completa el perfil ausente y conserva el existente; su reintento secuencial está cubierto por tests. `GetSessionStateUseCase` distingue entre ausencia de sesión, perfil pendiente y sesión con perfil local, sin escribir datos. Firebase todavía no está configurado: siguen pendientes la restauración real de la sesión tras cerrar la app, la conexión del flujo de recuperación con las pantallas, una fachada pública y un `AppContainer` para las aplicaciones nativas.

## Próximo bloque

La lectura del perfil local por ID está implementada:

- `UserRepository` incorpora `findById(userId: UserId): User?`.
- `RoomUserRepository` consulta el DAO existente y transforma la entidad al modelo de dominio, devolviendo `null` si no existe.
- La lectura no escribe datos ni genera operaciones de outbox.
- Los repositorios falsos de creación y registro se han adaptado al nuevo contrato.
- Los tests verifican que se devuelve el usuario solicitado cuando hay varios perfiles y que se devuelve `null` para un ID inexistente aunque haya otro perfil guardado.
- La clase `RoomUserRepositoryTest` está en verde.

`CompleteUserProfileUseCase` y sus tests ya están implementados en el árbol de trabajo:

- si el usuario local existe, no vuelve a guardarlo;
- si no existe, delega en `CreateUserUseCase` con el UID y nombre recibidos, sin repetir el registro remoto;
- el test `preservesCreatedProfileWhenCompletionIsRetried` usa un repositorio falso con estado: la primera llamada crea el perfil y la segunda, con otro nombre y un reloj adelantado, conserva el usuario completo (`id`, `displayName`, `createdAt` y `updatedAt`) y una sola escritura total.

Validación de este incremento el 28 de septiembre de 2026:

```text
./gradlew :sharedLogic:testAndroidHostTest --tests com.rafario.miscosas.domain.usecase.CompleteUserProfileUseCaseTest --console=plain
BUILD SUCCESSFUL — 3 tests, 0 fallos, 0 errores, 0 omitidos.
```

No se ejecutaron la suite completa, los tests iOS ni los builds de las aplicaciones para este cambio de test. Gradle mostró un aviso de versiones de XML del SDK Android; no impidió ejecutar las pruebas.

Esta cobertura verifica reintentos secuenciales con un repositorio en memoria, no recuperación tras reinicio ni atomicidad entre llamadas concurrentes. La consulta y la escritura siguen siendo operaciones separadas.

Decisión de recuperación del perfil:

- Si existe una sesión autenticada y un perfil local, continuar normalmente.
- Si existe una sesión autenticada pero falta el perfil local, pedir de nuevo el nombre en la pantalla «Completa tu perfil».
- Al confirmar, ejecutar `CompleteUserProfileUseCase` con el UID de la sesión y el nombre introducido, sin repetir el registro remoto.
- No se guardará provisionalmente el nombre para recuperarlo tras cerrar la aplicación.

La consulta del estado de sesión ya está implementada:

- `AuthenticationRepository` incorpora `suspend fun getCurrentUserId(): UserId?`; `null` representa ausencia de sesión.
- `GetSessionStateUseCase` consulta el UID y, si existe, busca su perfil mediante `UserRepository.findById`.
- `SessionState` representa los resultados `SignedOut`, `ProfilePending(userId)` y `Ready(user)`.
- `Ready` indica que hay sesión y perfil local; no implica que el onboarding del hogar esté completado.
- La consulta no registra cuentas, no crea perfiles ni escribe datos locales.
- Los tres escenarios tienen tests con repositorios falsos; los falsos de los tests de registro también implementan el nuevo contrato.

Rafael confirmó que pasan los tres tests de sesión y, después, los tests de todos los casos de uso en Android Host, ejecutando:

```text
./gradlew :sharedLogic:testAndroidHostTest --tests "com.rafario.miscosas.domain.usecase.GetSessionStateUseCaseTest" --console=plain
./gradlew :sharedLogic:testAndroidHostTest --tests "com.rafario.miscosas.domain.usecase.*" --console=plain
```

Esta validación no incluye la suite completa, los tests iOS ni los builds de las aplicaciones. Los tests de sesión usan falsos: todavía no validan la restauración de sesión de un proveedor real ni la recuperación tras reiniciar el proceso.

El siguiente micro-paso es decidir dónde se implementará el adaptador de autenticación y cómo expondrá una sesión restaurada al dominio. A partir de esa decisión se diseñará la fachada pública para las aplicaciones nativas, conservando internos los tipos de infraestructura.

Solo después se añadirán las dependencias Firebase y la composición de producción.

## Dirección posterior

Cada bloque debe volver a evaluarse antes de empezar. La dirección prevista es:

1. Recuperación idempotente del perfil local tras un registro parcial.
2. Sesión autenticada y composición manual mediante `AppContainer`.
3. Firebase Auth mediante adaptadores de plataforma.
4. Conectar onboarding y creación/unión a hogares en Compose y SwiftUI.
5. Diseñar esquema, reglas y adaptadores de Firestore; después implementar subida del outbox y aplicación remota a Room.
6. Implementar verticales nativas de objetos, documentos/Storage, garantías, devoluciones, mantenimiento e historial.
7. Añadir invitaciones, permisos, alertas, fotos, búsqueda, estadísticas y exportación solo cuando sus flujos estén definidos.
8. Cerrar con migraciones Room, emuladores Firebase, CI, accesibilidad, localización, seguridad y documentación para portfolio.

## Documentación pendiente

El `README.md` está desactualizado: todavía muestra parte de la toolchain anterior y afirma que la persistencia no existe. Debe actualizarse después de registrar este bloque, preferiblemente en un commit documental separado.

## Reanudación desde otro ordenador o conversación

El archivo `docs/CONTINUATION_PROMPT.md` contiene un prompt reutilizable. Después de clonar o actualizar el repositorio en otro ordenador, se debe abrir el proyecto y pegar ese prompt en una conversación nueva. El asistente contrastará este documento con el código y Git antes de continuar.
