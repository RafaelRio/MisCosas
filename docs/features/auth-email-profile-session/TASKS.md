# TASKS: Acceso por correo, perfil y restauración de sesión

**SPEC:** [SPEC.md](SPEC.md) — Aprobada por Rafael (S30)
**PLAN:** [PLAN.md](PLAN.md) — Aprobado por Rafael
**Estado de TASKS:** Aprobado formalmente por Rafael; el estado de cada tarea
se refleja individualmente según la evidencia revisada.

Este desglose deriva únicamente de la SPEC y el PLAN aprobados. La existencia
de código previo no cuenta como evidencia de finalización. Rafael implementa y
prueba cada tarea personalmente; se revisa una tarea cada vez. La autorización
para crear este documento no autoriza implementar, ejecutar pruebas ni avanzar
a otra fase.

## Estados

- **Pendiente:** todavía no se ha iniciado con autorización de implementación.
- **En curso:** Rafael inició la tarea y compartió el cambio para revisión.
- **Completada:** se cumplen todos los criterios de finalización y existe la
  evidencia indicada, revisada junto con Rafael.
- **Bloqueada:** falta una dependencia, decisión o condición ambiental concreta;
  el motivo y el siguiente desbloqueo deben quedar anotados.

Los archivos enumerados son candidatos a cambiar, no una instrucción para
modificarlos todos. Si el código existente ya cumple parte de una tarea, se
comprueba y se registra evidencia en vez de rehacerlo.

## Fase A — interoperabilidad y seguridad de datos locales

### AUTH-T01 — Prueba mínima real de interoperabilidad Kotlin–Swift

- **Objetivo:** comprobar desde la aplicación iOS el consumo de una fachada
  pública de `sharedLogic`, incluida una operación asíncrona y la recepción de
  un resultado exitoso y de un error neutral. Es un experimento previo a los
  contratos definitivos de autenticación, no los fija ni sustituye CA funcional.
- **Dependencias:** ninguna.
- **Archivos candidatos:** fachada pública temporal en
  `sharedLogic/src/commonMain/`; consumidor de prueba en `iosApp/iosApp/App/`
  (por ejemplo, `ContentView.swift`); configuración del proyecto Xcode solo si
  el consumo real lo requiere.
- **RF/CA:** RF-10; prerrequisito técnico para CA-21.
- **Comprobar:** compilar y ejecutar el framework/consumidor iOS; invocar desde
  Swift la función suspendida, observar éxito y error y verificar cómo se
  representan sus tipos. No consolidar aún la API final de Auth.
- **Finalización:** Rafael aporta la llamada Swift real y el resultado observado
  en el simulador/dispositivo, registra límites de exportación y, si hace
  falta, la forma mínima de un adaptador Swift delgado. Las reglas de negocio
  permanecen en KMP. Una compilación del framework sin consumirlo desde Swift
  no basta.
- **Progreso/evidencia:** Rafael confirmó que `/usr/libexec/java_home -v 21`
  detecta OpenJDK 21.0.13 y comunicó que el Build de Xcode terminó con éxito.
  La inspección de solo lectura del header generado muestra
  `run(shouldFail:completionHandler:)`, que devuelve resultado y `NSError` por
  callback. Rafael añadió en `ContentView.swift` una vista temporal con ambos
  botones y actualización de estado en `MainActor`, y comunicó que ambos
  muestran los resultados esperados: éxito `succeeded=true`, `code=OK`; error
  neutral `succeeded=false`, `code=PROBE_ERROR`. Rafael restauró la navegación
  original y comunicó que la app funciona correctamente; la inspección
  confirmó que `SwiftInteropProbe.kt` ya no existe ni quedan referencias al
  probe. La evidencia cubre Build del framework, consumo asíncrono real desde
  Swift, ambos resultados y limpieza del código temporal.
- **Estado:** Completada — Xcode Build y ejecución final comunicados por Rafael;
  resultado Swift real observado por Rafael; restauración del consumidor y
  retirada de la fachada temporal comprobadas por inspección.

### AUTH-T02 — Inventario seguro de Room legacy y archivos asociados

- **Objetivo:** determinar si existe la antigua `miscosas.db` y si tiene datos,
  sin exponer valores privados ni atribuirlos a una cuenta. La inspección es
  deseable para conocer el contenido legado, pero no es condición para crear
  bases nuevas por UID.
- **Dependencias:** AUTH-T01.
- **Archivos/carpeta candidatos:** ubicaciones locales de datos de Android e
  iOS; procedimiento/evidencia local no sensible. No se edita ni abre la base
  mediante Room.
- **RF/CA:** RF-09; CA-19, CA-20.
- **Comprobar:** con la app cerrada, consulta SQLite estrictamente de solo
  lectura: esquema, versión, integridad y conteos de filas; localizar
  `-wal`, `-shm` y el directorio privado de archivos asociados. No leer,
  copiar a logs ni compartir datos de filas. Preservar juntos base, sidecars y
  archivos asociados; no borrar, renombrar, migrar o asignar a UID alguno.
- **Finalización:** si puede inspeccionarse con seguridad, se registra
  únicamente existencia, integridad y conteos agregados. Si no puede hacerse
  con seguridad, se registra esa limitación y la inspección queda bloqueada;
  esto no bloquea AUTH-T03 ni las bases nuevas por UID. En ambos casos, la
  condición imprescindible es preservar y aislar la base antigua y sus
  archivos asociados: nunca abrirlos desde una identidad nueva, modificarlos,
  borrarlos o asignarlos automáticamente a un UID.
- **Progreso/evidencia:** en el simulador iOS iPhone 18 Pro (iOS 27.0), el
  contenedor de datos identificado mediante `simctl` fue
  `.../Containers/Data/Application/E88E6953-343B-412D-B553-35DD04751D7F`.
  Una comprobación de existencia, sin abrir SQLite, indicó que no están
  `Library/Application Support/miscosas.db`, `miscosas.db-wal` ni
  `miscosas.db-shm`; tampoco existe el directorio `Library/Application Support`
  y `Documents` existe con cero archivos. Una búsqueda de solo lectura en el
  código actual no encontró otra ruta explícita de almacenamiento de archivos
  locales. En el Pixel 10 conectado, `run-as` permitió consultar los nombres
  esperados y no existen `databases/miscosas.db`, `databases/miscosas.db-wal`
  ni `databases/miscosas.db-shm`. En `files/` se encontró únicamente
  `profileInstalled`; la búsqueda del código actual no encontró referencias a
  ese nombre. No se leyó ni modificó el archivo y se desconoce su propósito,
  por lo que debe preservarse sin atribuirlo a una identidad. La revisión cubre
  las instalaciones seleccionadas, no otros dispositivos o instalaciones
  anteriores.
- **Estado:** Completada — inventario no destructivo de las instalaciones
  seleccionadas. No se encontró una base legacy; `profileInstalled` queda
  preservado y sin clasificar. No se abrió SQLite ni se inspeccionó contenido
  personal.

### AUTH-T03 — Proveedor de base local ligado a una identidad

- **Objetivo:** disponer en la composición compartida de una instancia de Room
  asociada al UID autenticado, sin base autenticada activa antes de conocerlo.
- **Dependencias:** AUTH-T01. AUTH-T02 es deseable, pero su inspección no es
  requisito previo; sí debe respetarse la preservación y aislamiento del
  legado descritos abajo.
- **Archivos candidatos:** `MisCosasDatabaseFactory.kt`,
  `MisCosasDatabase.kt` y composición/repositorios compartidos en
  `sharedLogic/src/commonMain/`.
- **RF/CA:** RF-08, RF-09; CA-16, CA-18–CA-20, CA-22.
- **Comprobar:** pruebas comunes del proveedor de contexto: no abre base sin UID;
  el mismo UID recupera su contexto; dos UID producen contextos separados; al
  cerrar sesión se invalida el contexto activo sin borrar los archivos.
- **Finalización:** los consumidores reciben la base/repositorios de la
  identidad activa y no retienen una instancia anterior después del cierre o
  cambio de UID; pruebas deterministas cubren separación y cierre. La base
  legacy y cualquier archivo asociado permanecen sin abrir desde identidades
  nuevas, sin modificar, borrar ni atribuir automáticamente a un UID, incluso
  si no se pudo inspeccionar su contenido.
- **Progreso/evidencia:** Rafael creó
  `sharedLogic/src/commonMain/kotlin/com/rafario/miscosas/data/local/database/UserDatabaseProvider.kt`.
  El proveedor implementa apertura diferida bajo demanda, reutilización de la
  instancia para el mismo UID, cierre de la instancia anterior al cambiar de
  UID y cierre del contexto sin borrar los archivos. Rafael escribió
  `UserDatabaseProviderTest` en `commonTest` para comprobar que construir el
  proveedor no invoca el callback de apertura y comunicó que pasó
  `./gradlew :sharedLogic:testAndroidHostTest --tests "com.rafario.miscosas.data.local.database.UserDatabaseProviderTest" --console=plain`.
  Rafael añadió `UserDatabaseProviderReuseTest` en `iosTest` para comprobar la
  reutilización de la misma instancia cuando se solicita el mismo UID. La
  compilación y ejecución de
  `./gradlew :sharedLogic:iosSimulatorArm64Test --console=plain` terminaron
  con `BUILD SUCCESSFUL`, según la salida comunicada por Rafael, después de
  seleccionar `/Applications/Xcode.app/Contents/Developer` mediante
  `xcode-select`. Rafael añadió además un test que solicita bases para dos UID
  distintos, comprueba que las instancias son distintas, que el callback se
  invoca dos veces y que recibe ambos UID en el orden solicitado; Rafael
  comunicó que la suite `iosSimulatorArm64Test` volvió a terminar en verde tras
  corregir la lista mutable de UID capturados. Rafael añadió al test de cierre
  una consulta al DAO sobre la referencia anterior y
  comprobó que falla después de `closeCurrentDatabase()`; Rafael comunicó que
  `./gradlew :sharedLogic:iosSimulatorArm64Test --console=plain` terminó en
  verde. Añadió la misma comprobación al test de cambio de UID y comunicó que
  la suite iOS también pasó; esto verifica que la referencia anterior deja de
  aceptar consultas tanto tras el cierre explícito como al cambiar de UID. Aún
  no demuestra la persistencia de datos/outbox en un archivo tras cerrar. La
  persistencia en archivos de plataforma y el uso del nombre no reversible
  corresponden a AUTH-T04/AUTH-T05.
- **Estado:** Completada — proveedor compartido y pruebas reportadas como
  exitosas por Rafael. La apertura persistente por UID se valida en las tareas
  de integración de cada plataforma.

### AUTH-T04 — Persistencia Room Android por UID

- **Objetivo:** abrir y conservar en Android una base Room persistente cuyo
  nombre se derive de forma determinista y no reversible del `UserId`. Esta
  tarea establece la integración de persistencia de plataforma; no conecta la
  base al ciclo real de autenticación/sesión.
- **Dependencias:** AUTH-T03.
- **Archivos afectados:** `sharedLogic/src/androidMain/.../MisCosasDatabaseBuilder.android.kt`,
  `sharedLogic/build.gradle.kts`, `gradle/libs.versions.toml` y pruebas de
  `sharedLogic/src/androidHostTest/` y `sharedLogic/src/androidDeviceTest/`.
- **RF/CA:** RF-08, RF-09; CA-16, CA-18–CA-20, CA-22, en lo que concierne a
  persistencia Android por UID. La integración con sesión real se valida en
  AUTH-T16/AUTH-T25.
- **Comprobar:** formato determinista de nombre, apertura Room con contexto
  Android real, persistencia del mismo perfil al cerrar/reabrir y aislamiento
  A→B→A al cambiar UID mediante una instancia del proveedor. Ninguna ruta abre
  la base legacy `miscosas.db`.
- **Finalización:** pruebas Android Host e instrumentadas demuestran el nombre
  derivado del UID, apertura/cierre de Room, recuperación del dato de A tras
  reabrir y cambio A→B→A sin exponer el perfil de A a B. No se afirma con esta
  tarea que el proveedor esté conectado a autenticación real, restauración de
  sesión o cierre de sesión de la app.
- **Progreso/evidencia:** Rafael implementó `returnDeterministicName` con
  SHA-256 sobre bytes UTF-8, modificó `createAndroidDatabaseBuilder` para
  recibir `UserId` y creó `createAndroidUserDatabaseProvider`, que captura
  `applicationContext` y construye Room persistente por UID. `AndroidDatabaseFileNameTest`
  pasó en Android Host según informó Rafael. Se configuró `androidDeviceTest`
  con árbol `test`, AndroidX Test runner/core/JUnit; el source set compiló y
  las pruebas instrumentadas llegaron al Pixel 10. Tras renombrar un método
  `commonTest` con espacios que D8 rechazaba, Rafael comunicó ejecuciones
  instrumentadas verdes. `opensUidDatabaseWithAndroidContext` abre Room por la
  factoría y ejecuta una consulta; `persistsUserWhenSameUidDatabaseIsReopened`
  recupera un perfil tras cierre y reapertura; `switchingUidKeepsProfilesIsolatedAndPreservesData`
  guarda `profile-a` en A, comprueba que B no lo ve y vuelve a A para comprobar
  que el perfil persiste. La inspección confirma que esas aserciones corresponden
  a los comportamientos descritos.
- **Límite de evidencia:** las pruebas de esta tarea usan `UserEntity`; la
  conservación del outbox se verificó posteriormente en AUTH-T06. La conexión
  con la sesión real y la invalidación del contexto al
  cerrar sesión/cambiar la cuenta quedan expresamente en AUTH-T16 y AUTH-T25.
- **Estado:** Completada según los criterios de persistencia Android por UID
  anteriores; no implica integración real del ciclo de sesión.

### AUTH-T05 — Persistencia Room iOS por UID

- **Objetivo:** abrir y conservar en iOS una base Room persistente por `UserId`,
  con aislamiento equivalente a Android. Esta tarea integra persistencia de
  plataforma, pero no conecta la base al ciclo real de autenticación/sesión.
- **Dependencias:** AUTH-T03, AUTH-T01.
- **Archivos candidatos:** `sharedLogic/src/iosMain/.../MisCosasDatabaseBuilder.ios.kt`,
  composición de `iosApp/iosApp/App/`.
- **RF/CA:** RF-08, RF-09; CA-16, CA-18–CA-20, CA-22.
- **Comprobar:** integración Room real con UID A/B; verificar apertura,
  persistencia al cerrar/reabrir, aislamiento entre bases y que no se abre la
  base legacy. No usar solo la compilación del framework como evidencia.
- **Finalización:** pruebas de iOS demuestran persistencia aislada por UID.
  Restauración autenticada, cierre de sesión y cambio de identidad real se
  conectan y validan en AUTH-T16/AUTH-T25.
- **Progreso/evidencia:** Rafael adaptó `createIosDatabaseBuilder` para
  recibir `UserId`, derivar el nombre del archivo mediante SHA-256 de sus bytes
  UTF-8 y guardarlo en `Application Support`; añadió
  `createIosUserDatabaseProvider` reutilizando `buildMisCosasDatabase` y
  `UserDatabaseProvider`. Rafael comunicó compilaciones exitosas de
  `:sharedLogic:compileKotlinIosSimulatorArm64`. El test
  `persistsProfileAfterClosingAndReopeningDatabase` en
  `sharedLogic/src/iosTest/` escribe un perfil, cierra el proveedor, abre otro
  para el mismo UID y comprueba el nombre recuperado; Rafael comunicó que
  `:sharedLogic:iosSimulatorArm64Test` terminó en verde. La revisión confirma
  que la prueba cierra la primera base antes de abrir la segunda y no usa la
  base legacy. El test `isolatesProfilesWhenSwitchingUids` usa el proveedor con
  UID A→B→A, confirma que B no encuentra el perfil guardado por A y que A lo
  recupera al volver. Rafael comunicó que `:sharedLogic:iosSimulatorArm64Test`
  terminó en verde con ambos tests. La inspección confirma que el test usa
  Room real mediante la factoría iOS, no una base en memoria.
- **Estado:** Completada — la persistencia tras cierre/reapertura y el
  aislamiento A→B→A del perfil en iOS están probados con Room real, según el
  resultado comunicado por Rafael. La integración con autenticación real y la
  conservación del outbox no forman parte de esta evidencia; corresponden a
  AUTH-T16/AUTH-T25 y AUTH-T06 o una validación posterior, respectivamente.

### AUTH-T06 — Verificar aislamiento local entre UID

- **Objetivo:** demostrar de extremo a extremo que cada cuenta solo ve su base,
  perfil y operaciones pendientes, conservando los datos al cerrar sesión.
- **Dependencias:** AUTH-T04, AUTH-T05.
- **Archivos candidatos:** tests de `sharedLogic/src/iosTest/` y pruebas de
  composición/ciclo de cuenta en `androidApp/` e `iosApp/`.
- **RF/CA:** RF-09; CA-19, CA-20.
- **Comprobar:** usar UID sintéticos y el proveedor local para sembrar perfil,
  otro dato y una operación de outbox de A; cerrar ese contexto y abrir B.
  Comprobar que B no ve el perfil, el dato ni la operación; volver a abrir A y
  confirmar que los tres siguen allí. Verificar que cerrar/cambiar contexto no
  borra ni marca como sincronizada la operación y que la base legacy no se abre
  ni se reasigna. No se necesita Firebase Authentication real en esta tarea;
  la conexión del UID de sesión se valida en AUTH-T16.
- **Finalización:** pruebas de integración en ambas plataformas demuestran la
  partición de datos y outbox por UID, conservación de A y ausencia de borrado
  o falsa sincronización. La comprobación de perfil de AUTH-T04 no cuenta como
  evidencia de persistencia del outbox.
- **Progreso/evidencia:** Rafael añadió en
  `sharedLogic/src/androidDeviceTest/.../AndroidUserDatabaseProviderTest.kt`
  una prueba instrumentada que siembra perfil, hogar y operación pendiente en
  A, comprueba que B no los encuentra y que vuelven a estar en A al regresar.
  La aserción del outbox compara la fila completa por su destino y mutation ID;
  `SyncOutboxEntity` no contiene un estado de sincronización. Los identificadores
  de prueba llevan el prefijo `auth-t06-android`. Rafael intentó ejecutar
  `:sharedLogic:connectedAndroidDeviceTest`, pero Gradle informó
  `No connected devices` en el primer intento. Tras conectar el móvil, Rafael
  comunicó que `:sharedLogic:connectedAndroidDeviceTest` terminó en verde. La
  prueba iOS equivalente está escrita en
  `sharedLogic/src/iosTest/.../IosUserDatabaseProviderTest.kt`: siembra perfil,
  hogar y outbox de A, comprueba ausencia desde B y conservación al volver a A.
  La inspección confirma que usa `createIosUserDatabaseProvider()` y cierra el
  proveedor en `finally`. En la primera ejecución iOS, Rafael comunicó 344
  tests con uno fallido por una discrepancia en el texto del perfil. Tras
  corregirlo, Rafael comunicó que `:sharedLogic:iosSimulatorArm64Test` pasó en
  verde. La evidencia cubre el aislamiento y conservación del perfil, hogar y
  fila pendiente del outbox en ambas plataformas, mediante Room real y UID
  sintéticos. Los builders usados derivan los nombres de base del UID y no
  abren `miscosas.db`; no se modifica ni reasigna la base legacy.
- **Límite de evidencia:** el outbox no tiene una columna de estado de
  sincronización; la igualdad de la fila persistida demuestra que la operación
  no se eliminó ni cambió durante el cambio de UID. No se integraron Firebase ni
  sesiones reales, que corresponden a AUTH-T16/AUTH-T25.
- **Estado:** Completada — tests Android instrumentados e iOS comunicados en
  verde por Rafael; Room real verifica separación A/B y conservación A.

## Fase B — contratos y lógica compartida

### AUTH-T07 — Definir estados compartidos de sesión y resultados neutrales

- **Objetivo:** modelar en KMP los estados observables de restauración,
  autenticación, verificación, perfil pendiente y error recuperable, usando lo
  aprendido en AUTH-T01 y sin filtrar tipos Firebase al dominio.
- **Dependencias:** AUTH-T01, AUTH-T03.
- **Archivos candidatos:** `SessionState.kt`,
  `AuthenticationRepository.kt`, `NativeAuthenticationProvider.kt`,
  `NativeAuthenticationRepository.kt` y modelos neutrales nuevos bajo
  `sharedLogic/src/commonMain/`.
- **RF/CA:** RF-03, RF-04, RF-06–RF-10; CA-06–CA-22, CA-27–CA-29.
- **Comprobar:** tests comunes para estados/resultados, errores y cancelación;
  desde Swift consumir el contrato que vaya a usarse realmente y comprobar
  async/resultado/error antes de extenderlo.
- **Finalización:** estados no confunden error remoto con ausencia, cancelación
  con error, ni sesión no verificada con acceso listo; API exportada resulta
  consumible sin duplicar reglas de negocio en Swift.
- **Estado:** Completada. Rafael comunicó tests comunes en verde para los
  estados, resultados neutrales, conversión de `NativeAuthenticationRepository`
  y `GetSessionStateUseCase`, incluidos error recuperable, propagación de
  errores y cancelación. Xcode compiló referencias Swift a `SessionState` y
  `NativeSessionLookupResult`; en ejecución, Rafael comprobó las variantes
  `Authenticated` (incluidos UID y verificación), `NoSession` y
  `RecoverableError`. Tras retirar el control temporal de `ContentView.swift`,
  comunicó un Build de Xcode exitoso. La llamada suspendida genérica y el
  tratamiento de éxito/error se validaron en AUTH-T01. No se integraron SDKs
  Firebase ni la restauración/ciclo real de sesión; permanecen en AUTH-T11,
  AUTH-T12 y AUTH-T16.

### AUTH-T08 — Validación y casos de uso de alta, acceso y perfil

- **Objetivo:** completar las reglas compartidas aprobadas y orquestar alta,
  inicio de sesión, perfil incompleto y reintento con el UID actual.
- **Dependencias:** AUTH-T07.
- **Archivos candidatos:** `RegisterWithEmailUseCase.kt`,
  `CreateUserUseCase.kt`, `CompleteUserProfileUseCase.kt`,
  `GetSessionStateUseCase.kt`, `RegistrationPartiallyCompletedException.kt` y
  `sharedLogic/src/commonTest/`.
- **RF/CA:** RF-01–RF-03, RF-06–RF-08, RF-11; CA-01–CA-07, CA-12–CA-17,
  CA-23–CA-29.
- **Comprobar:** tests `kotlin.test` para límites/normalización, llamadas
  evitables, credenciales, perfil recuperable, error vs ausencia, cancelación y
  password fuera de persistencia. Usar falsos para puertos; no afirmar Firebase
  integrado por pasar estos tests.
- **Finalización:** casos comunes cumplen las reglas y conservan la identidad
  autenticada tras fallo parcial; nunca guardan contraseñas en `User`, Room ni
  outbox.
- **Progreso/evidencia:** Rafael comunicó tests Android Host en verde para
  normalización/validación del registro, reintento del perfil después de un
  fallo de guardado, conversión y reenvío del UID al iniciar sesión, y
  validaciones compartidas del inicio de sesión. La suite Android Host de
  `GetSessionStateUseCaseTest` y la suite completa `iosSimulatorArm64Test`
  terminaron en verde, incluida la propagación del fallo de lectura del perfil
  local. El test integrado de reintento tras fallo parcial de registro pasó en
  Android Host e iOS Simulator: usa el UID y nombre de la excepción, guarda el
  perfil al segundo intento y no vuelve a registrar la cuenta. También
  comunicó enlace del framework iOS y compilación de Xcode exitosos tras añadir
  `signInWithEmail` al puente neutral. No se ha integrado Firebase ni se ha
  validado el acceso real en un proveedor nativo.
- **Cierre:** completada para los casos de uso y reglas compartidas definidos
  por esta tarea. Los criterios globales de perfil remoto, Firebase y ciclo
  real de sesión conservan su alcance y su validación en AUTH-T11–AUTH-T16 y
  AUTH-T25; este cierre no afirma que estén integrados.
- **Estado:** Completada.

### AUTH-T09 — Guardias compartidas de acceso para cuenta no verificada

- **Objetivo:** impedir operaciones y lecturas protegidas de hogar/inventario
  desde las capas de acceso a datos, no solo mediante navegación.
- **Dependencias:** AUTH-T07, AUTH-T08.
- **Archivos candidatos:** `CreateHouseholdUseCase.kt`,
  `HouseholdRepository`, `ItemRepository` y política compartida de sesión en
  `sharedLogic/src/commonMain/`.
- **RF/CA:** RF-05, RF-07; CA-09, CA-17.
- **Comprobar:** tests comunes de usuario no verificado al crear hogar, leer o
  modificar hogares/objetos; verificar acceso permitido al editar únicamente
  el perfil según la SPEC.
- **Finalización:** cada ruta existente a datos protegidos rechaza la operación
  con estado no verificado; no se modifica el alcance implementando flujos de
  hogares/inventario.
- **Estado:** Pendiente.

## Fase C — entorno Firebase e integración remota

### AUTH-T10 — Preparar Firebase de desarrollo y proteger archivos locales

- **Objetivo:** establecer el entorno de desarrollo aprobado con Emulator Suite
  y evitar añadir por accidente los archivos Firebase reales al repositorio.
- **Dependencias:** AUTH-T01; disponibilidad de un proyecto Firebase de
  desarrollo para las comprobaciones que lo necesiten.
- **Archivos candidatos:** `.gitignore`, configuración Gradle Android,
  configuración Xcode/SPM, configuración local no versionada y reglas Firebase
  del proyecto de desarrollo.
- **RF/CA:** RF-01–RF-08; CA-06–CA-17, CA-27–CA-29.
- **Comprobar:** antes de instalar archivos locales, revisar que `.gitignore`
  cubre `google-services.json` y `GoogleService-Info.plist`; probar Auth y
  Firestore Rules Emulator. No versionar credenciales privadas ni incluirlas
  en clientes. Las opciones Firebase públicas no sustituyen reglas.
- **Finalización:** procedimiento local queda documentado, reglas de perfil
  propio y denegación de recursos fuera de alcance se ejercitan en Emulator, y
  no se incorpora configuración cliente real ni secreto al repositorio.
- **Estado:** Pendiente.

### AUTH-T11 — Integrar Firebase Authentication en Android

- **Objetivo:** implementar el adaptador Android para sesión, registro/acceso
  por email y contraseña, refresh, verificación y traducción de errores al
  contrato neutral.
- **Dependencias:** AUTH-T07, AUTH-T08, AUTH-T10.
- **Archivos candidatos:** `androidApp/build.gradle.kts`, catálogo de versiones,
  inicialización Firebase y adaptador del puente Android en `androidApp/` o
  `sharedLogic/src/androidMain/`, según el límite aprobado del PLAN.
- **RF/CA:** RF-01–RF-04, RF-08; CA-01–CA-08, CA-10–CA-11, CA-16, CA-27,
  CA-29.
- **Comprobar:** integración contra Auth Emulator para registro, acceso,
  credenciales rechazadas, red/límites y reenvío; comprobar cancelación y que
  la UI no presenta éxito ante rechazo.
- **Finalización:** Firebase Android se traduce al contrato común sin tipos
  Firebase en dominio; los mensajes respetan enumeración protegida y estados
  aprobados. Adjuntar resultados del entorno Android usado.
- **Estado:** Pendiente.

### AUTH-T12 — Integrar Firebase Authentication en iOS

- **Objetivo:** implementar el adaptador iOS con Firebase Apple SDK y exponer
  a Swift el mismo comportamiento neutral ya validado, sin trasladar reglas de
  negocio a Swift.
- **Dependencias:** AUTH-T07, AUTH-T08, AUTH-T10, AUTH-T01.
- **Archivos candidatos:** `iosApp/iosApp.xcodeproj/project.pbxproj`,
  `iosApp/iosApp/App/iOSApp.swift`, puente Swift/Kotlin y adaptador iOS en
  `sharedLogic/src/iosMain/` si el diseño aprobado lo requiere.
- **RF/CA:** RF-01–RF-04, RF-08, RF-10; CA-01–CA-08, CA-10–CA-11, CA-16,
  CA-21, CA-27, CA-29.
- **Comprobar:** integración con Firebase/Auth Emulator según soporte del SDK,
  y recorrido iOS controlado de restauración, registro, acceso y errores.
  Compilar y consumir la API desde el target SwiftUI real.
- **Finalización:** el comportamiento y categorías de errores son coherentes
  con Android; se registra por separado qué se validó en Emulator y qué exigió
  dispositivo/simulador o servicio controlado.
- **Estado:** Pendiente.

### AUTH-T13 — Aplicar snapshots remotos del perfil sin generar outbox

- **Objetivo:** incorporar el camino Room de aplicación de perfil descargado,
  diferenciado de `save()` local sincronizable.
- **Dependencias:** AUTH-T03, AUTH-T07.
- **Archivos candidatos:** `UserRepository.kt`, `RoomUserRepository.kt`,
  `UserDao.kt`, `SyncOutboxDao.kt` y tests de integración Room en
  `sharedLogic/src/iosTest/`.
- **RF/CA:** RF-06, RF-08; CA-12–CA-15, CA-28.
- **Comprobar:** integración Room real: snapshot remoto sin pendiente actualiza
  el perfil sin crear outbox; `save()` local sigue encolando dato y outbox de
  forma atómica.
- **Finalización:** los dos caminos tienen efectos separados y pruebas que
  comprueban contenido, outbox y rollback ante fallo transaccional.
- **Estado:** Pendiente.

### AUTH-T14 — Proteger perfil local pendiente frente a snapshot antiguo

- **Objetivo:** hacer transaccional la comprobación de outbox antes de aplicar
  un snapshot del mismo perfil.
- **Dependencias:** AUTH-T13.
- **Archivos candidatos:** `RoomUserRepository.kt`, `SyncOutboxDao.kt` y tests
  Room en `sharedLogic/src/iosTest/`.
- **RF/CA:** RF-06, RF-08; CA-12, CA-14–CA-15.
- **Comprobar:** crear perfil local y outbox pendiente; intentar aplicar un
  snapshot remoto distinto; confirmar que la transacción conserva íntegros
  tanto el perfil local como la operación pendiente y no los marca como
  sincronizados. También probar snapshot sin operación pendiente.
- **Finalización:** la prueba falla si se sobrescribe el perfil, se altera o
  elimina el outbox o se encola otra operación; no se implementa resolución
  general de conflictos.
- **Estado:** Pendiente.

### AUTH-T15-ANDROID — Leer y escribir el perfil remoto por UID en Android

- **Objetivo:** conectar Firestore `users/{uid}` desde el adaptador Android al
  puerto remoto compartido y sus reglas por UID.
- **Dependencias:** AUTH-T10, AUTH-T11, AUTH-T13, AUTH-T14.
- **Archivos candidatos:** adaptador Android de perfil en `androidApp/` o
  `sharedLogic/src/androidMain/`, reglas Firestore del proyecto de desarrollo
  y modelos de mapeo privados que use esa integración.
- **RF/CA:** RF-05–RF-08; CA-09, CA-12–CA-15, CA-17, CA-28.
- **Comprobar:** Firestore Rules Emulator con propietario y UID distinto;
  leer existente, confirmar ausencia, crear/actualizar el perfil propio y
  simular error remoto. La ausencia solo se informa ante respuesta remota
  confirmada; el fallo conserva la sesión.
- **Finalización:** ninguna consulta usa UID del formulario; las reglas
  deniegan otros perfiles y recursos fuera del alcance en Emulator. Snapshot
  pasa por AUTH-T14. La integración iOS se comprueba por separado en
  AUTH-T15-IOS.
- **Estado:** Pendiente.

### AUTH-T15-IOS — Leer y escribir el perfil remoto por UID en iOS

- **Objetivo:** conectar Firestore `users/{uid}` desde el adaptador iOS al
  puerto remoto compartido y las mismas reglas por UID.
- **Dependencias:** AUTH-T10, AUTH-T12, AUTH-T13, AUTH-T14, AUTH-T01.
- **Archivos candidatos:** adaptador Firestore de `iosApp/` o
  `sharedLogic/src/iosMain/`, modelo de mapeo privado y configuración Firebase
  iOS local.
- **RF/CA:** RF-05–RF-08, RF-10; CA-09, CA-12–CA-15, CA-17, CA-21, CA-28.
- **Comprobar:** consumir el puerto desde SwiftUI real; comprobar lectura,
  ausencia confirmada, error remoto y escritura del perfil propio. Validar las
  reglas con Firestore Rules Emulator donde el flujo iOS lo permita y contrastar
  el rechazo de un UID ajeno.
- **Finalización:** iOS conserva la misma semántica Android sin duplicar reglas
  de negocio; no usa UID de formulario y toda aplicación local pasa por
  AUTH-T14. Registrar qué casos necesitaron una comprobación controlada fuera
  de Emulator.
- **Estado:** Pendiente.

## Fase D — restauración, verificación y experiencia nativa

### AUTH-T16 — Orquestar restauración, partición local y ciclo de sesión

- **Objetivo:** conectar el UID observado desde la autenticación nativa con la
  factoría Room de cada plataforma durante restauración y uso de la sesión;
  cerrar/inutilizar el contexto anterior en cierre de sesión y cambio de UID,
  antes de exponer datos de otra identidad. Orquestar también perfil local,
  perfil remoto y estados comunes.
- **Dependencias:** AUTH-T06, AUTH-T08, AUTH-T11, AUTH-T12, AUTH-T13, AUTH-T14,
  AUTH-T15-ANDROID y AUTH-T15-IOS. AUTH-T06 depende de AUTH-T04/AUTH-T05, por
  lo que la dependencia de persistencia es acíclica: T04/T05 → T06 → T16.
- **Archivos candidatos:** `GetSessionStateUseCase.kt`, repositorios, factorías
  por plataforma y composición raíz Android/iOS.
- **RF/CA:** RF-06, RF-08, RF-09; CA-12–CA-20, CA-28.
- **Comprobar:** tests comunes para perfil local/remoto/ausente, error remoto y
  cancelación; integración Android/iOS de sesión existente y arranque en frío.
  Verificar que el UID actual abre solo su partición; logout cierra/inutiliza
  ese contexto sin borrar archivos/outbox; el cambio A→B nunca deja accesible
  ni reutiliza un contexto de A; volver a A recupera su partición y pendientes,
  siempre sujeto a permisos vigentes. Comprobar callbacks tardíos y errores de
  restauración sin repoblar estado de la identidad anterior.
- **Finalización:** los estados de sesión/perfil son observables y coherentes;
  un error remoto no crea perfil/cuenta ni descarta un perfil local válido. La
  composición de ambas plataformas usa Room para el UID autenticado, y los
  recorridos de restauración, cierre y cambio de UID prueban que no se conserva
  ni expone contexto anterior. La conservación de outbox debe tener evidencia
  explícita (AUTH-T06 o esta tarea); no inferirla de persistir una fila de
  perfil.
- **Estado:** Pendiente.

### AUTH-T17 — Persistir y refrescar verificación ligada al UID

- **Objetivo:** cumplir la restauración offline de verificación previamente
  confirmada y refrescarla al recuperar conexión.
- **Dependencias:** AUTH-T11, AUTH-T12, AUTH-T16.
- **Archivos candidatos:** modelo/almacenamiento local de verificación bajo
  `sharedLogic/`, adaptadores Firebase nativos y tests comunes/por plataforma.
- **RF/CA:** RF-04, RF-08; CA-11, CA-16, CA-22.
- **Comprobar:** prueba con dos UID, cuenta verificada previamente, no verificada
  y sin evidencia local; reiniciar offline y volver online. Solo la UID correcta
  restaura evidencia y esta no habilita por sí sola hogares/inventario.
- **Finalización:** tests e integración Android/iOS reflejan los estados
  aprobados y la confirmación online; sin información fiable, se mantiene
  acceso limitado.
- **Estado:** Pendiente.

### AUTH-T18 — Enlace de verificación y actualización en Android

- **Objetivo:** conectar solicitud/reenvío, procesamiento de enlace de acción
  Firebase y retorno a la app Android.
- **Dependencias:** AUTH-T11, AUTH-T15-ANDROID, AUTH-T16, AUTH-T17.
- **Archivos candidatos:** pantallas de verificación en `androidApp/`,
  `AndroidManifest.xml`, configuración App Links y adaptador Auth Android.
- **RF/CA:** RF-04, RF-08; CA-08, CA-10–CA-11, CA-29.
- **Comprobar:** Emulator para aceptación/rechazo de reenvío cuando sea
  compatible; enlace válido, inválido/caducado y retorno con app abierta/cerrada
  mediante prueba controlada. Confirmación solo tras estado Firebase actualizado.
- **Finalización:** resultado comprensible, reintento disponible cuando
  corresponde y el acceso sigue limitado hasta verificación confirmada.
- **Estado:** Pendiente.

### AUTH-T19 — Enlace de verificación y actualización en iOS

- **Objetivo:** integrar Universal Links y el flujo de enlace de verificación
  aprobado en SwiftUI, adaptando la pantalla Figma que actualmente muestra
  código.
- **Dependencias:** AUTH-T01, AUTH-T12, AUTH-T15-IOS, AUTH-T16, AUTH-T17.
- **Archivos candidatos:** `iosApp/iosApp/App/iOSApp.swift`,
  `AuthenticationFlowView.swift`, pantalla de verificación, Associated Domains
  y configuración de dominio/AASA según corresponda.
- **RF/CA:** RF-04, RF-08, RF-10; CA-08, CA-10–CA-11, CA-21, CA-29.
- **Comprobar:** navegación con enlaces válidos, inválidos y caducados en
  simulador/dispositivo y dominio de prueba; comprobar regreso al estado
  verificado solo tras confirmación Firebase.
- **Finalización:** la pantalla usa enlace, no un código solicitado al usuario;
  los estados y errores son equivalentes funcionalmente a Android y coherentes
  visualmente con SwiftUI.
- **Estado:** Pendiente.

### AUTH-T20 — Formularios y flujo de autenticación Android

- **Objetivo:** conectar pantallas Compose para registro, acceso, perfil
  pendiente y verificación a los casos de uso compartidos.
- **Dependencias:** AUTH-T08–AUTH-T11, AUTH-T16–AUTH-T18.
- **Archivos candidatos:** `androidApp/.../presentation/{login,register,welcome}/`,
  `navigation/AppNavGraph.kt`, `MisCosasApp.kt` y pantallas de perfil/verificación.
- **RF/CA:** RF-01–RF-08, RF-10–RF-11; CA-01–CA-17, CA-21, CA-23–CA-29.
- **Comprobar:** pruebas de lógica/UI disponibles y recorrido manual Compose de
  validación, carga, errores, campos temporales, perfil pendiente y retorno de
  verificación. Resolver con Rafael el tratamiento visual de acciones Figma
  fuera de esta SPEC antes de cerrar la pantalla; no conectar proveedores
  excluidos.
- **Finalización:** el flujo refleja estados reales sin mensajes falsos de
  éxito; contraseña solo está en el formulario activo; UI no es la única
  protección del acceso limitado.
- **Estado:** Pendiente.

### AUTH-T21 — Formularios y flujo de autenticación iOS

- **Objetivo:** conectar SwiftUI a la fachada compartida para acceso, registro,
  perfil pendiente, verificación y restauración.
- **Dependencias:** AUTH-T01, AUTH-T08, AUTH-T12, AUTH-T16–AUTH-T17,
  AUTH-T19.
- **Archivos candidatos:** `iosApp/iosApp/Navigation/AuthenticationFlowView.swift`,
  `Presentation/Login/LoginView.swift`, destinos de registro/perfil/verificación,
  `ContentView.swift` y composición iOS.
- **RF/CA:** RF-01–RF-08, RF-10–RF-11; CA-01–CA-17, CA-21, CA-23–CA-29.
- **Comprobar:** consumidor Swift real; pruebas UI/manuales en simulador o
  dispositivo para los estados y errores; comparar resultados funcionales con
  Android. Acordar con Rafael cómo presentar acciones Figma fuera de esta SPEC.
- **Finalización:** SwiftUI comunica estados correctos y conserva datos solo
  según RF-11; reglas de negocio siguen en KMP; el diseño adapta código a
  enlace sin añadir nuevos requisitos.
- **Estado:** Pendiente.

## Fase E — cierre seguro de sesión y validación integral

### AUTH-T22 — Cierre de sesión común y conservación de pendientes

- **Objetivo:** coordinar logout local online/offline, detección de outbox,
  advertencia, cancelación o confirmación conservando datos por UID.
- **Dependencias:** AUTH-T03–AUTH-T09, AUTH-T11–AUTH-T16.
- **Archivos candidatos:** contratos/casos de uso de autenticación y sesión,
  consulta al outbox por partición UID, composición de repositorios.
- **RF/CA:** RF-09; CA-18–CA-20.
- **Comprobar:** tests comunes con y sin pendientes; cancelar mantiene sesión;
  confirmar cierra el contexto y no elimina/procesa operaciones. La opción de
  sincronizar antes del cierre no se implementa en esta tarea.
- **Finalización:** no quedan datos visibles tras confirmar; offline informa
  que se necesita conexión para volver a autenticarse; pendientes permanecen
  asociados a UID/hogar originales y no se declaran sincronizados.
- **Estado:** Pendiente.

### AUTH-T23 — Presentar cierre de sesión seguro en Android

- **Objetivo:** conectar la acción de cierre Compose, incluido el aviso con
  pendientes, cancelación, confirmación y resultado offline.
- **Dependencias:** AUTH-T20, AUTH-T22.
- **Archivos candidatos:** pantallas de verificación/perfil en `androidApp/`,
  navegación y estado UI de sesión.
- **RF/CA:** RF-09–RF-10; CA-18–CA-21.
- **Comprobar:** recorrido manual online/offline con y sin outbox; cancelar,
  confirmar y abrir luego otra identidad. Verificar ausencia de contenido y
  navegación de la cuenta anterior.
- **Finalización:** opciones y mensajes concuerdan con la SPEC y la sesión se
  cierra solo al confirmar; no se invoca sync.
- **Estado:** Pendiente.

### AUTH-T24 — Presentar cierre de sesión seguro en iOS

- **Objetivo:** conectar la acción de cierre SwiftUI con estados equivalentes
  funcionalmente a Android.
- **Dependencias:** AUTH-T21, AUTH-T22.
- **Archivos candidatos:** pantalla de verificación/perfil SwiftUI,
  `AuthenticationFlowView.swift` y consumidor de estado compartido.
- **RF/CA:** RF-09–RF-10; CA-18–CA-21.
- **Comprobar:** recorrido en simulador/dispositivo online/offline con y sin
  pendientes; cancelar, confirmar y cambiar de identidad; volver a la cuenta
  original y verificar conservación.
- **Finalización:** no se muestran datos de la cuenta tras logout y los
  pendientes no se descartan ni se intentan sincronizar.
- **Estado:** Pendiente.

### AUTH-T25 — Recorrido de aceptación y regresión de ambas plataformas

- **Objetivo:** comprobar la matriz completa aplicable de la SPEC y documentar
  evidencia Android e iOS antes de declarar terminado el incremento.
- **Dependencias:** AUTH-T01–AUTH-T24, AUTH-T15-ANDROID y AUTH-T15-IOS.
- **Archivos candidatos:** tests existentes/nuevos de `sharedLogic`, pruebas
  Android e iOS y registro de evidencia/estado del incremento.
- **RF/CA:** RF-01–RF-11; CA-01–CA-29. CA-30 está diferido y no aplica.
- **Comprobar:** regresión de casos de uso, Room real en iOS, Firebase Auth y
  Firestore Rules Emulator, recorridos UI, ciclo de vida, enlaces, offline y
  cambio de UID. Verificar en ambas plataformas restauración, logout y cambio
  A→B→A sin reutilizar contexto de la identidad anterior, y conservación del
  outbox sin borrarlo ni presentarlo como sincronizado. Completar CA-01–CA-29
  por la matriz del PLAN; no usar el resultado de una plataforma como evidencia
  de la otra.
- **Finalización:** solo se completa cuando CA-01 a CA-29 están comprobados
  satisfactoriamente con evidencia observada en las plataformas que aplican.
  Un CA fallido o bloqueado puede registrarse con su motivo y siguiente paso,
  pero impide declarar AUTH-T25 y el incremento como completados/validados.
  CA-30 permanece diferido y no se prueba en este incremento. Revisar
  discrepancias Figma y documentar diferencias nativas admitidas.
- **Estado:** Pendiente.

## Dependencias críticas y límites

```text
AUTH-T01 → AUTH-T07 → AUTH-T08 → adaptadores Firebase → perfil remoto/restauración
AUTH-T01 → AUTH-T02 (inventario deseable, no bloqueante)
AUTH-T01 + preservación/aislamiento legacy → AUTH-T03 → AUTH-T04/AUTH-T05 → AUTH-T06
AUTH-T10 → AUTH-T11/AUTH-T12 → AUTH-T15-ANDROID/AUTH-T15-IOS
AUTH-T13 → AUTH-T14 → AUTH-T15-ANDROID/AUTH-T15-IOS
AUTH-T06 + AUTH-T08 + AUTH-T11/T12 + AUTH-T13/T14 + AUTH-T15-ANDROID/IOS → AUTH-T16 → verificación y pantallas
AUTH-T16 → AUTH-T17/AUTH-T18/AUTH-T19 → AUTH-T25
AUTH-T22 → AUTH-T23/AUTH-T24 → AUTH-T25
```

- AUTH-T01 es el primer corte técnico y no autoriza fijar anticipadamente el
  contrato final de autenticación.
- AUTH-T02 conserva el legado; no incluye migración ni exploración de valores
  privados. Si el inventario no puede mantenerse seguro, se detiene esa tarea.
- AUTH-T14 solo resuelve la protección de mutaciones pendientes del perfil; no
  implementa resolución general de conflictos.
- No se construyen sincronización general, reintentos/outbox remoto, gestión de
  conflictos, Storage ni sistema de notificaciones. CA-30 está diferido.
- Las pantallas Figma contienen acciones de Google/Apple y confirmación de
  contraseña que no se implementan en esta SPEC. Su tratamiento visual debe
  acordarse con Rafael antes de dar por cerradas AUTH-T20/AUTH-T21; no conectar
  acciones falsas ni inferir una decisión de producto.

## Registro de progreso

AUTH-T01–AUTH-T08 están completadas con la evidencia registrada en cada tarea;
AUTH-T09–AUTH-T25 siguen pendientes. AUTH-T04 acredita persistencia Room
Android por UID y pruebas instrumentadas en Pixel 10, pero no integra el ciclo
real de sesión. Sus tests verifican `UserEntity`; AUTH-T06 verifica además
perfil, hogar y conservación de outbox en ambas plataformas. AUTH-T16 tiene asignado conectar la factoría al
ciclo real de restauración/cierre/cambio de UID y comprobar que no se reutilice
ni exponga el contexto anterior; AUTH-T25 conserva la validación final de los
criterios aplicables. Al revisar trabajo de Rafael, actualizar solo la tarea
correspondiente y registrar el resultado, plataforma, comprobaciones ejecutadas
y evidencia compartida.
