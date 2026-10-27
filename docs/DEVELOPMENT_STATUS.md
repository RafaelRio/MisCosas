# MisCosas — Estado de desarrollo

Última actualización: 26 de octubre de 2026.

Este documento es el punto de reanudación del proyecto. Debe actualizarse al cerrar cada bloque de trabajo. El código, Git y las evidencias describen el estado realizado; las especificaciones aprobadas definen el comportamiento esperado. Antes de actuar hay que contrastar este documento con `git status`, los últimos commits y los archivos mencionados.

## Forma de trabajo

Rafael desarrolla MisCosas personalmente. El asistente actúa como tutor
técnico, arquitecto y guía: consulta el estado real, recomienda el siguiente
paso pequeño, explica por qué corresponde y qué rutas se verían afectadas,
y revisa el resultado que Rafael comparta.

Rafael crea y modifica personalmente el código y las pruebas, y ejecuta los
comandos que alteran el proyecto. Rafael ha autorizado de forma permanente al
asistente a crear y modificar `AGENTS.md` y todos los archivos de `docs/` para
registrar decisiones funcionales ya aprobadas y mantener reglas, SPEC, PLAN,
TASKS, requisitos y estado. No hace falta pedir autorización por cada edición
documental dentro de ese ámbito. Este permiso no autoriza inventar o aprobar
decisiones funcionales, avanzar de fase sin acuerdo, modificar código o
pruebas, ejecutar comandos que alteren el proyecto ni realizar operaciones
Git. El asistente comunica los cambios documentales para su revisión; Rafael
gestiona stage, commits y push.

Expresiones como «continuamos» o «siguiente paso» solicitan una recomendación,
no autorizan cambios ni el avance a otra fase. Definición funcional,
documentación de requisitos, SPEC, PLAN, TASKS, implementación, pruebas y
revisión se acuerdan por separado. No se considera una fase completada sin
revisar la evidencia con Rafael.

El desarrollo avanza por incrementos funcionales: no es necesario resolver
todas las cuestiones P- de V1 antes de empezar. Para cada incremento se
identifican y resuelven solo las decisiones que afecten a su alcance,
comportamiento, aceptación o dependencias; las demás permanecen pendientes y
trazadas. No se continúa automáticamente por el siguiente ID P-.

## Producto y plataformas

MisCosas es un pasaporte digital personal y familiar para registrar objetos, compras, garantías, devoluciones, documentos y mantenimiento.

**MisCosas es el nombre final**, confirmado por Rafael el 20 de octubre de 2026. La visión completa está conservada en [PRODUCT_BRIEF_ORIGINAL.md](PRODUCT_BRIEF_ORIGINAL.md), que transcribe el prompt inicial. Su nombre provisional «Mío» queda como referencia histórica.

El producto previsto incluye hogares compartidos y administración de miembros, fotografías, categorías, favoritos, historial, alertas, búsqueda y filtros, estadísticas sobre el precio histórico de compra y sincronización entre miembros y dispositivos. Los objetos pertenecen al hogar. El funcionamiento local-first y la recuperación de información al cambiar de móvil forman parte de esa visión. Se comienza en España y en español, con preparación para internacionalización.

El alcance funcional aprobado está consolidado en [PRODUCT_REQUIREMENTS_V1.md](PRODUCT_REQUIREMENTS_V1.md), que debe leerse completo antes de continuar. Incluye desde V1 hogares compartidos, pertenencia a varios hogares, permisos, invitaciones y sincronización; su implementación gradual no los excluye de esa versión. El bloque de cierre de sesión y cambio de cuenta (`PROP-SES-001..008`) también está aprobado funcionalmente y documentado allí. El brief se conserva como fuente histórica. Exportación de inventario PDF/CSV y categorías personalizadas siguen pospuestas o como posibilidades futuras según sus entradas X-. El alcance aprobado no demuestra que las funcionalidades estén implementadas ni resuelve sus cuestiones pendientes.

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
- Para Auth se acordó usar los SDK nativos desde cada aplicación, un puente público neutral y un adaptador Kotlin interno hacia `AuthenticationRepository`. La integración real Android/iOS y la restauración de sesión aún deben validarse.
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

La SPEC `auth-email-profile-session`, el PLAN técnico y las TASKS están aprobados. `TASKS.md` contiene 26 tareas: AUTH-T01–AUTH-T10 están completadas, AUTH-T11 está en progreso y AUTH-T12–AUTH-T25 siguen pendientes. Para AUTH-T07, Rafael comunicó tests comunes en verde para estados y resultados, el traductor `NativeAuthenticationRepository` y `GetSessionStateUseCase`, incluidos errores y cancelación. Xcode compiló referencias Swift a `SessionState` y `NativeSessionLookupResult`; Rafael comprobó en ejecución las variantes `Authenticated`, `NoSession` y `RecoverableError`. Tras retirar el control temporal de `ContentView.swift`, comunicó un Build de Xcode exitoso. La llamada suspendida genérica y el manejo de éxito/error se habían validado en AUTH-T01. Estas validaciones fueron comunicadas por Rafael; no se ejecutaron de nuevo durante esta actualización documental.

AUTH-T04 queda completada por la persistencia Room Android por UID: nombre de archivo SHA-256 de bytes UTF-8, factoría con `applicationContext`, persistencia del perfil tras reabrir e aislamiento A→B→A mediante tests instrumentados en Pixel 10. La factoría todavía no está conectada al ciclo real de autenticación, restauración o cierre/cambio de sesión. AUTH-T04 probó `UserEntity`; AUTH-T06 verificó además perfil, hogar y conservación del outbox en Android e iOS. AUTH-T16 conectará Room al UID real durante restauración y uso, cerrará/inutilizará el contexto anterior al salir o cambiar UID y comprobará que no se reutiliza ni expone. AUTH-T25 conserva la validación final del aislamiento real de sesión.

AUTH-T02 registró que no se encontró `miscosas.db` ni sus sidecars en las instalaciones seleccionadas; el archivo `files/profileInstalled` de Android se preservó sin inspección ni atribución. AUTH-T05 está completada: el builder iOS usa un nombre SHA-256 derivado del UID bajo `Application Support`, y la factoría lo conecta con `UserDatabaseProvider`. Rafael comunicó compilación iOS exitosa y tests Room reales en verde para persistencia del perfil tras cierre/reapertura y aislamiento A→B→A. La conservación del outbox se verificó en AUTH-T06; la integración con autenticación real queda en AUTH-T16/AUTH-T25. Firebase y la conexión con el ciclo real de sesión siguen pendientes en sus tareas correspondientes.

AUTH-T10 queda completada según la evidencia comunicada por Rafael: Firebase CLI 15.34.1 quedó asociado al proyecto de desarrollo; la consulta de bases Firestore no encontró ninguna instancia remota; los emuladores Auth y Firestore arrancaron en los puertos 9099 y 8080; y `npm test` en `firebase-rules-tests` pasó con un caso que permite el perfil propio y rechaza el perfil de otro UID, la escritura anónima y una ruta de hogar. La prueba usa `@firebase/rules-unit-testing` contra el emulador local. `.gitignore` excluye las configuraciones Firebase, `node_modules/` y `*-debug.log`; la búsqueda local no encontró los archivos cliente. No se desplegaron reglas y la CLI confirmó que no hay base Firestore remota. El procedimiento consiste en arrancar con `npx -y firebase-tools@latest emulators:start --only auth,firestore` usando JDK 21, y ejecutar `npm test` desde `firebase-rules-tests`. Limitaciones: las reglas locales permiten al propietario guardar cualquier estructura en su documento y requieren validación de esquema antes de una integración remota; `npm install` informó cinco vulnerabilidades altas en el árbol de dependencias de prueba, cuyo desglose está pendiente. Esto no acredita integración de Firebase SDK en Android/iOS ni uso de una base Firestore de producción.

AUTH-T11 está en progreso. El catálogo y módulo Android incluyen Google Services, Firebase BoM 35.1.0, Firebase Auth y `kotlinx-coroutines-play-services`; `processDebugGoogleServices` procesó la configuración local y `:androidApp:assembleDebug` terminó en BUILD SUCCESSFUL. El puente común expone registro y `NativeAuthenticationRepository` reenvía credenciales y transforma el UID a `UserId`; `NativeAuthenticationRepositoryTest` pasó con un falso. `FirebaseNativeAuthenticationProvider` implementa consulta/refresco de sesión, registro y acceso, clasificación de errores Firebase y propagación de cancelación. Las cinco pruebas instrumentadas (`registersWithAuthEmulatorAndReturnsAuthenticatedUid`, `signsInWithAuthEmulatorAndReturnsExistingUid`, `rejectsUnknownAccountAsCredentialsRejected`, `returnsNoSessionWhenThereIsNoAuthenticatedUser` y `returnsAuthenticatedSessionAfterRefreshingCurrentUser`) terminaron en BUILD SUCCESSFUL en Pixel 10 AVD. Cubren registro, acceso con el mismo UID, rechazo de credenciales, ausencia de sesión y refresco de una sesión autenticada no verificada, según la ejecución observada. Para este entorno fue necesario ejecutar `adb reverse tcp:9099 tcp:9099` y configurar los tests con `127.0.0.1:9099`; desde el proceso de la app, `10.0.2.2:9099` expiraba aunque el navegador del AVD alcanzaba el emulador. Siguen pendientes errores de red/límites y la conexión del adaptador a la composición de la app.

## Estado por fase y siguiente paso

| Fase | Estado actual |
| --- | --- |
| Definición funcional | 217 entradas V1 aprobadas y 8 decisiones `PROP-SES-001..008` aprobadas funcionalmente. Quedan 80 cuestiones abiertas por especificar; ver IDs en `PRODUCT_REQUIREMENTS_V1.md`. `P-AUT-001` está resuelto por S21–S24; `P-AUT-002` por S25 y S27; `P-AUT-005` y `P-CRU-001` por S26. S28 resuelve la restauración offline de una verificación previamente confirmada; S29, la conservación temporal y descarte de formularios; y S30 delimita el cierre seguro del primer incremento, aplazando el intento de sincronización y las notificaciones a sus incrementos respectivos sin alterar el alcance global de V1. La política de cierre de sesión de P-ALE-003 está resuelta; su deduplicación multidispositivo sigue abierta. |
| Propuestas y decisiones | `PROP-SES-001..008` aprobadas funcionalmente; otras propuestas y 15 exclusiones, aplazamientos o límites X conservan el estado indicado en el documento de requisitos. |
| SPEC | `docs/features/auth-email-profile-session/SPEC.md` aprobada formalmente por Rafael con S30. Define el alcance del primer incremento y marca explícitamente como diferidos el intento de sincronización al cerrar sesión y la limpieza de notificaciones. Mobile App Builder realizó una revisión independiente de solo lectura y sus omisiones se corrigieron antes de la aprobación. |
| PLAN | `docs/features/auth-email-profile-session/PLAN.md` **Aprobado formalmente por Rafael** el 23 de octubre de 2026 tras revisar la coherencia final con la SPEC. Incorpora cuatro precisiones técnicas sobre legacy, interoperabilidad Swift, protección de snapshots frente a outbox pendiente y Firebase. |
| TASKS | `docs/features/auth-email-profile-session/TASKS.md` **Aprobado formalmente por Rafael** con 26 tareas. AUTH-T01–AUTH-T10 están **Completadas**, AUTH-T11 está **En progreso** y AUTH-T12–AUTH-T25 están **Pendientes**. En T04, Rafael comunicó pruebas Android Host del nombre SHA-256 y tests instrumentados de apertura, persistencia y aislamiento A→B→A en Pixel 10; la revisión del test confirma que A conserva su perfil y B no lo ve. La factoría no está conectada al ciclo real de sesión. El test cubre perfil (`UserEntity`), no outbox; T06 verifica la conservación de una operación pendiente. En T05, Rafael comunicó compilación iOS y tests Room reales en verde para persistencia al cerrar/reabrir y aislamiento A→B→A del perfil. En T06, Rafael comunicó tests Android instrumentados e iOS en verde para aislamiento de perfil, hogar y outbox. En T07, comunicó tests comunes en verde para estados, resultados, conversión del repositorio y caso de uso, así como compilación y comprobación en ejecución desde Swift de las tres variantes neutrales. Tras retirar el control temporal, comunicó un Build de Xcode exitoso. En T08, comunicó tests Android Host verdes para validación de registro e inicio de sesión, reintento de perfil, traducción del UID, ausencia de llamadas para datos de acceso inválidos y propagación del error de lectura del perfil local. También comunicó las suites Android Host e iOS Simulator completas en verde, enlace del framework y Build de Xcode tras ampliar el puente neutral. El test integrado de reintento después del fallo parcial de registro pasó en Android Host e iOS Simulator, conservando UID/nombre y sin repetir el alta. La inspección de `User`, `UserEntity` y `SyncOutboxEntity` confirma que no contienen campos de contraseña. En T09, Rafael comunicó `iosSimulatorArm64Test` en verde para el rechazo de creación de hogares y de las operaciones directas de `RoomItemRepository` con sesión no verificada: lectura por ID, observación por hogar y guardado. El test de hogares comprueba que hogar, membresía y outbox no se escriben; los tests de objetos comprueban que las lecturas son rechazadas y que `save` no escribe ni encola operaciones. Rafael también comunicó la suite iOS en verde tras añadir acceso limitado al perfil propio: el perfil propio se puede guardar con correo no verificado y se rechaza guardar otro UID sin crear perfil ni outbox. No se comunicó un caso separado de lectura del perfil propio no verificado. Firebase y el perfil remoto no están integrados; AUTH-T11–AUTH-T16 conservan esas integraciones y AUTH-T25 la validación final. Véase la evidencia y sus límites en TASKS. |
| Implementación | Hay modelos de dominio, Room y avances local-first para objetos, hogares, perfil y sesión, además de lógica compartida de registro e inicio de sesión por email. Firebase y la integración nativa completa siguen pendientes. Los detalles están en “Trabajo completado” y “Perfil y sesión”. |
| Pruebas y validación | Hay resultados históricos y resultados de tests comunicados por Rafael. No se afirma que se hayan vuelto a ejecutar en esta sesión. La integración Firebase no está validada. |
| Bloqueos | No hay una tarea formalmente bloqueada en este momento. AUTH-T01 está completada con Build Xcode y llamada asíncrona real desde Swift, ambos resultados observados por Rafael, y aplicación restaurada funcionando según su confirmación. La SPEC aprobada no conserva decisiones funcionales pendientes. Antes de cerrar las tareas de UI, Rafael debe decidir el tratamiento visual de acciones presentes en Figma que quedan fuera de esta SPEC (Google/Apple y confirmación de contraseña); no se conectarán acciones falsas ni se ampliará el alcance por su presencia en diseño. |

**AUTH-T06:** Rafael comunicó que las pruebas Room reales de Android (instrumentada en el móvil) e iOS terminaron en verde. Ambas verifican que perfil, hogar y fila pendiente del outbox de A no son visibles desde B y siguen presentes al volver a A. En la primera ejecución iOS hubo una aserción fallida por discrepancia en el nombre del perfil; tras corregirla, Rafael comunicó el pase de la suite.

**Punto de reanudación:** AUTH-T01–AUTH-T10 están completadas y AUTH-T11 está en progreso según la evidencia comunicada por Rafael. AUTH-T06 verificó en Android e iOS la separación por UID y la conservación del perfil, hogar y outbox pendiente al cambiar A→B→A. AUTH-T07 definió los estados comunes y resultados neutrales; los tests Android Host reportados en verde cubren estado, traducción del proveedor, caso de uso, errores y cancelación. Para AUTH-T08, Rafael comunicó tests verdes para validación de registro e inicio de sesión, reintento tras fallo local, traducción del UID por el repositorio, validaciones locales, propagación del fallo de lectura del perfil y suite Android Host completa. También comunicó la suite iOS Simulator completa en verde, enlace iOS y Build de Xcode tras añadir el método neutral de inicio de sesión. El test integrado de reintento tras fallo parcial pasó en Android Host e iOS Simulator sin repetir el registro. `User`, `UserEntity` y `SyncOutboxEntity` no contienen campos de contraseña. Para AUTH-T09, Rafael comunicó `iosSimulatorArm64Test` en verde para el rechazo directo de creación de hogares y de lectura/escritura de objetos con sesión no verificada: `create`, `findById`, recogida de `observeByHouseholdId` y `save`. El test de hogares comprueba que no se escriben hogar, membresía ni outbox; los tests de objetos verifican que no se exponen lecturas ni se escribe/encola el objeto. Rafael también comunicó la suite iOS en verde tras añadir acceso limitado al perfil propio: el perfil propio se puede guardar con correo no verificado y se rechaza guardar otro UID sin crear perfil ni outbox. No se comunicó un caso separado de lectura del perfil propio no verificado. Firebase, perfil remoto y ciclo real de sesión/perfil continúan pendientes en las tareas asignadas; AUTH-T16 conectará la factoría al ciclo autenticado de restauración, logout y cambio de UID, y AUTH-T25 mantendrá la validación final del aislamiento real de sesión. La SPEC, PLAN y TASKS siguen aprobados.

**Primer incremento:** Rafael aprobó formalmente la SPEC de acceso por correo/contraseña, verificación por enlace, perfil, recuperación remota por UID, restauración de sesión, restricciones para cuentas no verificadas y cierre local seguro en Android e iOS. S28 define la restauración offline de verificación; S29, el ciclo de vida de los formularios; S30 mantiene la conservación y aislamiento de pendientes, mientras aplaza el intento de sincronización al incremento que incorpore esa capacidad y la limpieza de notificaciones al incremento de alertas. El motor general de sincronización, conflictos y alertas sigue fuera de esta SPEC, sin eliminarse del alcance V1. No quedan decisiones funcionales pendientes en la SPEC aprobada. El PLAN técnico está formalmente aprobado e incorpora: prueba real mínima de consumo Kotlin–Swift como primer corte; inventario de la base `miscosas.db` y archivos asociados en solo lectura, sin atribución o migración; partición Room por UID; aplicación de snapshots remotos protegida transaccionalmente frente a outbox pendiente del perfil; y proyecto Firebase de desarrollo con Emulator Suite, configuración cliente real local/no versionada y credenciales privadas fuera del cliente y Git. TASKS está aprobada; AUTH-T01–AUTH-T10 están completadas según sus evidencias. AUTH-T04 valida persistencia Android por UID y aislamiento A→B→A del perfil con tests instrumentados en Pixel 10, según comunicó Rafael y confirmó la revisión de sus aserciones. AUTH-T06 verifica perfil, hogar y outbox entre UID sintéticos en ambas plataformas, según informó Rafael; la integración con sesión real queda en AUTH-T16/AUTH-T25. En AUTH-T05, Rafael comunicó compilación iOS y tests Room reales en verde para persistencia tras cierre/reapertura y aislamiento A→B→A del perfil. AUTH-T05 no implica integración con sesión real.

Google, Apple, vinculación de proveedores, recuperación de contraseña, hogares, inventario y sincronización general quedan fuera de esta SPEC, pero siguen en el alcance global aprobado de V1 cuando corresponda. El PLAN y TASKS están aprobados. AUTH-T01–AUTH-T10 están completadas según sus evidencias; las demás tareas conservan su estado individual en TASKS. Un incremento posterior podrá abordar crear y consultar objetos localmente dentro del hogar activo, sujeto a su propio alcance y definición.

## Alcance funcional consolidado

La fuente detallada y los pendientes por bloque están en [PRODUCT_REQUIREMENTS_V1.md](PRODUCT_REQUIREMENTS_V1.md). Esta tabla es un índice, no una segunda especificación.

| Bloque aprobado para V1 | IDs |
| --- | --- |
| Inventario básico: compra, fotografías, favoritos y siete estados | V1-INV |
| Administradores y miembros, permisos y trazabilidad | V1-ROL |
| Invitaciones mediante código compartible de un uso y siete días | V1-ITC |
| Varios hogares, selector y separación de datos y operaciones | V1-HOG |
| Fotografías múltiples, principal, fuentes nativas y uso offline | V1-FOT |
| Compras multimoneda, EUR inicial, importes históricos exactos | V1-COM |
| Garantías múltiples independientes y estados calculados | V1-GAR |
| Plazo y seguimiento manual de devoluciones separados | V1-DEV |
| Documentos, escaneado nativo, asociaciones y archivos offline | V1-DOC |
| Mantenimiento puntual/periódico con dos modalidades e historial | V1-MAN |
| Historial con acontecimientos automáticos y entradas manuales | V1-HIS |
| Alertas compartidas y preferencias personales de recordatorios | V1-ALE |
| Búsqueda, filtros y orden por hogar; búsquedas recientes locales | V1-BUS |
| Estadísticas por hogar y moneda; agrupaciones anuales y mensuales | V1-EST |
| Sincronización Firebase, combinación compatible y conflictos explícitos | V1-SYN |
| Correo/contraseña, Google, Apple, vinculación y recuperación de acceso | V1-AUT |
| Cierre de sesión local, aislamiento por cuenta y cambio de cuenta | PROP-SES-001..008 |

Las reglas transversales se registran en V1-GEN. Las entradas P- conservan las cuestiones sin concretar; `PROP-SES-001..008` están aprobadas y mantienen sus IDs de trazabilidad. Las entradas X- distinguen exclusiones de V1, aplazamientos y elementos fuera de un bloque por ahora, como iCloud o resumen por email; no convertirlos en exclusiones definitivas sin acuerdo.

Pendientes relevantes: límites y conservación de archivos; umbrales de alertas; fechas y correcciones de mantenimiento; seguimiento de devoluciones y efecto en inventario; selección de garantías al filtrar/ordenar; estados incluidos en estadísticas; conflictos, eliminaciones y permisos tras revocación; validaciones de autenticación y comprobación remota del perfil. Cada decisión conserva su ID en la fuente principal. Las diferencias Figma están registradas allí con nodos reales y nivel de comprobación, sin aprobar automáticamente todo lo diseñado.

## Preparación SDMD

- [AGENTS.md](../AGENTS.md) reúne la tutoría, las reglas de arquitectura, las etapas SDMD y el uso de los cinco perfiles de `.codex/agents/`. Cada sesión debe comprobar qué agentes puede activar realmente.
- [PRODUCT_BRIEF_ORIGINAL.md](PRODUCT_BRIEF_ORIGINAL.md) conserva la visión inicial sin reescribirla; [PRODUCT_REQUIREMENTS_V1.md](PRODUCT_REQUIREMENTS_V1.md) conserva los acuerdos posteriores y será lectura obligatoria junto al estado técnico. Las SPEC enlazarán sus IDs.
- Se incorporaron [SPEC_TEMPLATE.md](SPEC_TEMPLATE.md), [PLAN_TEMPLATE.md](PLAN_TEMPLATE.md), [MOBILE_GUIDELINES.md](MOBILE_GUIDELINES.md) y [GENERIC_RULES.md](GENERIC_RULES.md) como referencias del curso de ArisGuimera. Se utilizó el contenido consultado previamente al fallar la descarga posterior; no se ha cotejado de nuevo con la versión actual de GitHub.
- [CONTINUATION_PROMPT.md](CONTINUATION_PROMPT.md) conserva su función de recuperar contexto en otro ordenador o conversación y exige consultar AGENTS, los requisitos consolidados y este punto de reanudación.
- La organización prevista es `docs/features/<funcionalidad>/SPEC.md`, `PLAN.md` y `TASKS.md`. No hay plantilla compartida de TASKS: se deriva del plan aprobado.

## Perfil y sesión: trabajo realizado

La lectura del perfil local por ID está implementada:

- `UserRepository` incorpora `findById(userId: UserId): User?`.
- `RoomUserRepository` consulta el DAO existente y transforma la entidad al modelo de dominio, devolviendo `null` si no existe.
- La lectura no escribe datos ni genera operaciones de outbox.
- Los repositorios falsos de creación y registro se han adaptado al nuevo contrato.
- Los tests verifican que se devuelve el usuario solicitado cuando hay varios perfiles y que se devuelve `null` para un ID inexistente aunque haya otro perfil guardado.
- La clase `RoomUserRepositoryTest` está en verde.

`CompleteUserProfileUseCase` y sus tests están implementados y recogidos en el commit `cfde7ca`:

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

Decisión histórica del incremento local de recuperación del perfil:

- Si existe una sesión autenticada y un perfil local, continuar normalmente.
- Si existe una sesión autenticada pero falta el perfil local, pedir de nuevo el nombre en la pantalla «Completa tu perfil».
- Al confirmar, ejecutar `CompleteUserProfileUseCase` con el UID de la sesión y el nombre introducido, sin repetir el registro remoto.
- No se guardará provisionalmente el nombre para recuperarlo tras cerrar la aplicación.

Esta descripción conserva el contexto del código y los tests existentes. El acuerdo V1-AUT-011, concretado por S26, exige comprobar el perfil remoto antes de interpretar la ausencia local como registro incompleto en un dispositivo nuevo. Si el servidor confirma que el perfil no existe, se completa con la identidad autenticada actual; un fallo de consulta no activa el formulario. La implementación sigue pendiente de SPEC/PLAN; no es una decisión funcional abierta.

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

## Adaptador de autenticación nativa: incremento en curso

Archivos existentes en `sharedLogic`:

- [NativeAuthenticationProvider.kt](../sharedLogic/src/commonMain/kotlin/com/rafario/miscosas/api/auth/NativeAuthenticationProvider.kt): interfaz pública en `api/auth` que declara `suspend fun getCurrentUserId(): String?`.
- [NativeAuthenticationRepository.kt](../sharedLogic/src/commonMain/kotlin/com/rafario/miscosas/data/repository/NativeAuthenticationRepository.kt): implementación interna de `AuthenticationRepository`. Consulta al proveedor una sola vez, devuelve `null` si no hay UID y convierte el identificador presente a `UserId`. Los errores se propagan.
- [NativeAuthenticationRepositoryTest.kt](../sharedLogic/src/commonTest/kotlin/com/rafario/miscosas/data/repository/NativeAuthenticationRepositoryTest.kt): tres tests para ausencia de sesión, conversión del UID y propagación de la misma excepción del proveedor.

Rafael confirmó que pasó la clase de tests tras completar el escenario de error. El comando utilizado en este incremento es:

```bash
./gradlew :sharedLogic:testAndroidHostTest --tests "com.rafario.miscosas.data.repository.NativeAuthenticationRepositoryTest" --console=plain
```

No se han vuelto a ejecutar esos tests durante esta actualización documental. Esta cobertura usa proveedores falsos en Android Host; no demuestra integración Firebase ni interoperabilidad o restauración de sesión en iOS.

Pendientes técnicos que deberán encajar en la SPEC y el PLAN:

- `registerWithEmail` del adaptador sigue en `TODO`, y el proveedor público aún no declara una operación de registro.
- Concretar el contrato del proveedor para esperar a que la sesión esté resuelta: una inicialización pendiente o un error no deben convertirse en ausencia de sesión.
- Validar las implementaciones nativas, sus errores y cancelación, y la interoperabilidad real del puente desde Swift.
- Conectar la lógica compartida con las aplicaciones y comprobar la recuperación del perfil tras reiniciar el proceso.

El trabajo realizado se conserva al adoptar SDMD; estos pendientes no autorizan su implementación antes de acordar el alcance y el plan.

## Dirección posterior

El alcance V1 está registrado en los requisitos, pero el orden y los detalles de implementación siguen por planificar. Esta relación conserva las áreas técnicas previstas; no constituye un PLAN aprobado:

- Completar la integración del registro, la sesión autenticada y la recuperación del perfil. Su lógica local ya está parcialmente implementada y probada; quedan la composición de producción, la fachada y los adaptadores nativos de Firebase Auth.
- Conectar onboarding y hogares en Compose y SwiftUI, incluyendo creación, pertenencia múltiple y selector, invitaciones y aceptación, salida del hogar, administración de miembros y transferencia de administración. Aplicar los roles aprobados y concretar los casos especiales pendientes.
- Desarrollar las funcionalidades de objetos, categorías, compras, fotografías, documentos/Storage, garantías, devoluciones, mantenimiento e historial, con sus estados, favoritos y archivado.
- Incorporar las alertas, recordatorios, búsqueda, filtros y estadísticas previstos en el prompt. Su comportamiento y sus criterios de aceptación se concretarán en las especificaciones.
- Implementar sincronización entre miembros y dispositivos, recuperación al cambiar de móvil, combinación de cambios compatibles, resolución explícita de conflictos incompatibles y borrado sincronizado. Concretar esquema y reglas de Firestore, subida del outbox y aplicación remota a Room sin reemplazar estos requisitos por una regla de último cambio por fecha.
- Abordar la exportación PDF/CSV en una entrega posterior. Evaluar las categorías personalizadas como ampliación futura.
- Trabajar migraciones Room, pruebas de integración, emuladores Firebase, CI, accesibilidad, localización, seguridad y documentación para portfolio conforme avance cada funcionalidad.

## Consolidación documental del 22 de octubre de 2026

Se creó la fuente principal de requisitos y se actualizaron las instrucciones de lectura, el prompt de continuidad y el README. Este último ya refleja la persistencia existente y la toolchain comprobada. El brief, las plantillas y las guías se revisaron y conservaron sin reescribir el contenido histórico ni duplicar las especificaciones.

El [informe de consolidación](REQUIREMENTS_CONSOLIDATION_REVIEW.md) registra recuentos, contraste independiente por Mobile App Builder, inspección de nodos Figma por UI Designer, lagunas y comprobaciones documentales. No se han ejecutado builds ni tests ni modificado código de aplicación en este trabajo.

La documentación se confirmó en `7b0891e` y la corrección de su punto de reanudación en `88cc7f8`. El contenido completo de `AGENTS.md` está versionado. Al actualizar este estado, el árbol de trabajo tenía archivos Kotlin de autenticación sin seguimiento; no forman parte de esos commits documentales. Consultar Git para conocer su estado vigente. En otro ordenador, recuperar la documentación versionada al actualizar o clonar el repositorio.

## Reanudación desde otro ordenador o conversación

El archivo [CONTINUATION_PROMPT.md](CONTINUATION_PROMPT.md) contiene un prompt reutilizable. Después de clonar o actualizar el repositorio en otro ordenador, se debe abrir el proyecto y pegar ese prompt en una conversación nueva. El asistente leerá AGENTS y todos los requisitos consolidados, contrastará este documento con el código y Git y revisará los documentos disponibles de la funcionalidad activa antes de proponer el siguiente paso. No será necesario deducir los acuerdos funcionales únicamente de la conversación.
