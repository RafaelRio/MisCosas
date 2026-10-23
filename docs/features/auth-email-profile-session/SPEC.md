# SPEC: Acceso por correo, perfil y restauración de sesión

**Estado:** Aprobada <!-- Borrador | En revisión | Aprobada -->

<!-- PARA LA PERSONA
Copia esta plantilla como SPEC.md en una carpeta de la funcionalidad.
Pide al agente que la complete contigo usando MOBILE_GUIDELINES.md.
SPEC.md define qué debe cumplirse; PLAN.md desarrolla cómo implementarlo;
TASKS.md organiza los pasos de ejecución.
-->
<!-- PARA EL AGENTE
- Lee las instrucciones del proyecto y MOBILE_GUIDELINES.md. Inspecciona el
  repositorio para comprobar el comportamiento actual. Si falta la guía, pide su ubicación.
- Completa esta spec con la persona: investiga lo comprobable y consulta las
  decisiones pendientes. Haz pocas preguntas por vez y actualiza las respuestas.
- No inventes requisitos ni exclusiones. Distingue propuestas de decisiones
  confirmadas y marca como PENDIENTE lo que aún no esté resuelto.
- Aplica las consideraciones mobile relevantes sin ampliar el alcance automáticamente.
- No incluyas diseño de clases, tablas, componentes, archivos o algoritmos:
  esos detalles pertenecen a PLAN.md. Sí registra restricciones explícitas del pedido.
- Mantén el documento breve y proporcional a la funcionalidad. Conserva los comentarios.
- Un documento completo no está aprobado automáticamente. Solicita aprobación
  antes de marcarlo como Aprobada. No implementes durante esta etapa.
-->

## Qué construimos y para quién

Permitimos que una persona cree una cuenta o acceda con correo y contraseña,
complete su perfil y recupere su sesión al volver a abrir MisCosas. La persona
podrá comprobar y completar la verificación de su correo. Hasta que la
verificación esté confirmada, la cuenta conserva el acceso limitado aprobado:
no podrá crear hogares, aceptar invitaciones ni consultar o modificar datos de
hogares e inventario.

La experiencia se ofrece en Android e iOS con interfaces nativas. La lógica
compartible se mantiene en Kotlin Multiplatform cuando corresponda, sin exigir
que ambas interfaces sean idénticas.

## Situación actual

El repositorio contiene casos de uso y persistencia local de usuario, perfil y
estado de sesión, además de un flujo de registro que utiliza un contrato de
autenticación. La consulta actual del estado de sesión busca el perfil local;
todavía no realiza la recuperación remota aprobada cuando falta ese perfil.
El adaptador nativo expone la consulta del UID, pero el registro permanece sin
implementar y Firebase no está integrado en este flujo.

Android tiene pantallas de acceso y registro con acciones aún desconectadas.
En iOS, el acceso está presentado pero no conectado y registro/verificación
son destinos pendientes. Ninguna de estas observaciones implica que el flujo
completo esté implementado o validado.

## Dentro del alcance

- **RF-01 — Registro por correo y contraseña** (`V1-AUT-002`, `V1-AUT-003`):
  solicitar nombre visible, correo y contraseña. Recortar espacios exteriores
  del nombre y del correo; no transformar la capitalización del correo ni
  recortar o transformar la contraseña. Rechazar nombres vacíos o formados
  únicamente por espacios/caracteres invisibles; aceptar nombres
  internacionales de hasta 100 caracteres, sin exigir más de un carácter.
  La validación local del correo detecta campo vacío, falta de `@` o falta de
  texto a cualquiera de sus lados; Firebase valida definitivamente. En el
  alta, exigir contraseña de al menos 8 caracteres, sin reglas de composición
  ni máximo funcional propio.
- **RF-02 — Inicio de sesión** (`V1-AUT-002`, `V1-AUT-003`): aceptar correo y
  contraseña. Validar localmente que el correo pase las comprobaciones básicas
  aprobadas y que la contraseña no esté vacía; no rechazar contraseñas antiguas
  por longitud ni transformarlas. Firebase valida definitivamente.
- **RF-03 — Errores de autenticación** (`V1-AUT-003`, S24): mostrar errores
  locales junto al campo y evitar solicitudes innecesarias. Rechazos de
  credenciales/cuenta no deben confirmar si el correo está registrado. Los
  errores de conexión, límites de intentos y fallos temporales deben explicar
  qué puede hacer la persona; los errores inesperados usarán un mensaje
  genérico. Los errores de campo aparecen junto al campo y los generales en el
  formulario. No se muestran mensajes técnicos del SDK. Se mantiene la
  protección de Firebase contra enumeración de correos.
- **RF-04 — Verificación de correo** (`V1-AUT-007`, S25 y S27): tras el alta
  se mantiene la sesión y se informa de que el correo debe verificarse. La
  persona puede consultar el estado, solicitar reenvío, comprobarlo de nuevo
  y cerrar sesión. Si Firebase acepta el reenvío, se confirma la solicitud,
  sin afirmar que el correo se entregó. Enlaces inválidos o caducados se
  explican y permiten solicitar otro. Al volver a MisCosas tras abrir el
  enlace, se actualiza el estado; también se permite comprobación manual.
  Solo la verificación confirmada permite continuar hacia hogares e
  inventario.
- **RF-05 — Acceso limitado** (`V1-AUT-007`): mientras no esté confirmada la
  verificación, no se permite crear hogares, aceptar invitaciones, ni acceder
  para consultar o modificar datos de hogares/inventario. La restricción se
  aplica a las operaciones y al acceso a datos correspondientes, no solo a
  la navegación. El alcance no implementa los flujos de hogares o inventario.
- **RF-06 — Perfil después del alta y recuperación** (`V1-AUT-010`,
  `V1-AUT-011`, S26): si la autenticación crea la cuenta pero falla el perfil,
  se conserva la identidad autenticada y se ofrece completar/reintentar el
  perfil sin crear otra cuenta. Si hay una sesión válida pero no existe perfil
  local, consultar el perfil remoto por el Firebase UID. Si existe, recuperarlo
  y persistirlo localmente. Si el servidor confirma que no existe, permitir
  completar el perfil con la identidad ya autenticada. Un error remoto no se
  interpreta como ausencia: conservar la sesión y ofrecer reintentar, sin
  crear perfiles duplicados. Si ya hay un perfil local válido y falla una
  consulta remota, no descartarlo automáticamente.
- **RF-07 — Perfil editable** (`V1-AUT-007`): una cuenta no verificada puede
  completar y editar su perfil. En esta SPEC el único dato de perfil definido
  funcionalmente es el nombre visible del registro; no se añaden otros campos.
- **RF-08 — Restauración de sesión** (`V1-AUT-009`, `V1-AUT-011`, S28): al
  abrir la aplicación se distingue la comprobación en curso, ausencia de
  sesión, sesión autenticada con perfil recuperado o pendiente de completar,
  y un error recuperable. No se presenta registro como si no hubiera sesión
  mientras la comprobación sigue en curso ni se interpreta un fallo remoto
  como ausencia de cuenta/perfil. Sin conexión, se puede restaurar una
  verificación que Firebase confirmó previamente y que se conservó de forma
  fiable para el mismo Firebase UID. Sin esa evidencia local, la cuenta sigue
  limitada aunque el usuario haya abierto el enlace. Ese estado local no
  concede por sí solo acceso a hogares ni inventario, ni sustituye permisos o
  autorizaciones del servidor. Al recuperar conexión, se actualizan la
  verificación y los permisos correspondientes.
- **RF-09 — Cierre de sesión de este incremento** (`PROP-SES-001..008`, S30): permitir cierre de sesión local, especialmente desde
  verificación, sin conexión si es necesario. Dejar de mostrar de inmediato
  los datos y estado de navegación de la cuenta; conservar los datos y
  pendientes asociados a su Firebase UID, aislados de otras cuentas. Este
  incremento no borra ni procesa pendientes durante el cierre y nunca los
  presenta como sincronizados. Si hay pendientes, informa antes de cerrar y
  permite cancelar o cerrar conservándolos; el cierre local funciona online y
  offline. La opción de intentar sincronizar antes del cierre queda aplazada
  hasta el incremento que incorpore esa capacidad; no se elimina el
  comportamiento global aprobado para V1. El PLAN debe revisar las operaciones
  pendientes que puede generar o encontrar el código existente y garantizar
  que no se pierdan ni se mezclen identidades. La gestión general de conflictos
  se implementará en su incremento correspondiente. La limpieza de recordatorios
  y notificaciones de la cuenta al cerrar sesión queda para el incremento de
  notificaciones; aquí no se implementa el sistema general de alertas ni la
  deduplicación entre dispositivos.
- **RF-10 — Plataformas y consistencia observable** (`V1-GEN-002`,
  `V1-AUT-017`): proporcionar el flujo en Android e iOS, con interfaz nativa
  en cada plataforma y resultados funcionales coherentes. El diseño iOS de
  verificación por código se considera discrepancia con el requisito aprobado
  de enlace; no cambia el requisito.
- **RF-11 — Datos temporales de formularios** (`V1-AUT-003`, `V1-AUT-009`,
  `V1-AUT-010`, S29): mientras la app siga abierta y la persona permanezca en
  el flujo de autenticación/perfil, conservar temporalmente nombre y correo
  al navegar entre sus pantallas. Si vuelve atrás antes de guardar el perfil,
  conservar el nombre mientras siga en el flujo. Mantener la contraseña solo
  mientras el formulario correspondiente permanezca activo, incluso si hace
  falta corregir un error o reintentar. Al abandonar el formulario, completar
  correctamente el registro o el acceso, o cerrar sesión, descartar la
  contraseña. No almacenar contraseñas como borradores persistentes. Al
  abandonar/finalizar el flujo o terminar el proceso, descartar los datos
  parciales no guardados; no es obligatorio restaurarlos al reabrir. Al volver
  a abrir, recuperar únicamente la sesión y el perfil previamente guardados.
  Un alta terminada que siga sin verificar conserva la cuenta y sesión en el
  estado de verificación aunque se abandone el formulario. Si falta completar
  el perfil, volver a pedir el nombre usando la cuenta autenticada existente,
  sin crear otra cuenta. Android e iOS deben observar el mismo comportamiento;
  el manejo técnico de este estado queda para el PLAN.

  Nodos de Figma registrados en la matriz de requisitos: [Android Login
  (6:23685)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=6-23685),
  [Android Register (6:23743)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=6-23743),
  [Android Verify email (6:23947)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=6-23947),
  [iOS Sign in (39:41)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=39-41),
  [iOS Create account (39:92)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=39-92) y
  [iOS Verify email (39:209)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=39-209).
  La comprobación registrada fue de metadatos, no una validación visual. Los
  estados de error, restauración y reintento de perfil, bloqueo por correo no
  verificado y finalización del perfil no tienen correspondencia comprobada
  en estos nodos; proponer su integración visual antes de implementarlos.

## Fuera de alcance

- Inicio de sesión/registro mediante Google o Apple y vinculación de
  proveedores (`V1-AUT-004`, `V1-AUT-005` siguen en el alcance global de V1).
- Recuperación de contraseña (`V1-AUT-008`), que se tratará en otro
  incremento; este flujo aprobado de V1 no se elimina.
- Creación/administración de hogares, selector, invitaciones, inventario y
  sus operaciones (`V1-AUT-012` y áreas relacionadas); esta SPEC solo cubre
  que una cuenta no verificada no pueda acceder a ellos.
- Motor general de sincronización, conflictos entre miembros, sincronización
  de archivos y funcionamiento general de recordatorios. El intento de
  sincronización desde el cierre y la limpieza de notificaciones aprobados
  para V1 quedan aplazados a los incrementos que incorporen esas capacidades;
  CA-30 conserva la trazabilidad del requisito, pero no aplica a este
  incremento.
- Revocación de sesiones de otros dispositivos o eliminación de cuenta
  (`P-SES-003`, sin decisión de incluirlos como funciones independientes).
- Nuevos atributos de perfil no definidos funcionalmente.

## Flujo de usuario

1. Una persona sin sesión puede abrir registro o inicio de sesión e introducir
   sus credenciales. La validación local señala errores antes de contactar con
   el servicio.
2. Si el alta es aceptada, MisCosas conserva la sesión autenticada y el perfil
   disponible; muestra la pantalla de verificación. Si completar el perfil
   falla, permite reintentar con la misma identidad.
3. La persona solicita el correo de verificación y abre su enlace. Al volver a
   MisCosas, la app comprueba el estado; mientras no confirme la verificación,
   mantiene la pantalla limitada y permite volver a comprobar o pedir otro
   enlace.
4. Al iniciar la app con una sesión existente, MisCosas restaura la sesión y
   el perfil local/remoto según corresponda. Si el perfil falta localmente,
   consulta el remoto por UID antes de ofrecer completarlo.
5. Una sesión cuyo correo está confirmado continúa al punto de entrada
   autenticado de la app. La creación/selección de hogares y el inventario no
   forman parte de esta SPEC.
6. Desde verificación, la persona puede cerrar sesión online u offline. Si hay
   pendientes, se informa antes de cerrar y puede cancelar o cerrar
   conservándolos; este incremento no intenta sincronizarlos. Al terminar, la
   cuenta anterior deja de estar visible en la app y los pendientes no son
   accesibles desde otra identidad.
7. Mientras permanece en el flujo, la persona puede volver entre formularios
   conservando temporalmente nombre/correo y, antes de guardar, el nombre del
   perfil. La contraseña solo permanece en el formulario activo. Si abandona
   o termina el proceso, se descartan los borradores no guardados; al abrir de
   nuevo se restaura solo la sesión/perfil guardados. Si un registro ya se
   completó y falta verificar, la sesión continúa en verificación; si falta
   completar el perfil, se solicita el nombre con esa misma identidad.

## Datos y reglas de negocio

- Identidad de cuenta: Firebase UID; un correo coincidente no sustituye la
  identidad original ni permite mezclar perfiles o pendientes.
- Perfil definido en este incremento: identificador, nombre visible y los
  metadatos ya contemplados por el modelo existente. El único campo editable
  aprobado aquí es el nombre visible.
- La contraseña no se presenta como dato de perfil ni se conserva como parte
  del perfil local.
- Los estados de sesión y verificación deben distinguir ausencia, carga,
  sesión autenticada, verificación pendiente, perfil pendiente y error
  recuperable. Su representación técnica se decidirá en el PLAN.
- Una operación de autenticación nueva requiere Internet. El cierre de sesión
  local no depende de una llamada remota.
- Para un alta remota aceptada con fallo posterior al guardar el perfil, no se
  repite el alta de autenticación al reintentar el perfil.
- Los borradores de autenticación/perfil no son datos persistentes; solo se
  conserva temporalmente la información aprobada mientras el flujo permanece
  activo. La gestión concreta de ese estado temporal corresponde al PLAN.

## Comportamiento mobile y casos alternativos

| Situación | Comportamiento esperado |
| --- | --- |
| Carga o acción en curso | Mostrar que se comprueba sesión/perfil o se procesa autenticación; no presentar por error el estado de usuario sin sesión. Evitar que una respuesta tardía o doble acción produzca altas repetidas. |
| Sin datos | Sin sesión, mostrar la entrada de autenticación; perfil remoto confirmado ausente permite completar el perfil con la sesión actual. |
| Entrada inválida | Mostrar el error junto al campo según las validaciones aprobadas y no iniciar la solicitud de autenticación. |
| Error o espera excesiva | Mantener la sesión si ya existe, usar mensajes comprensibles y ofrecer reintentar cuando proceda; no convertir fallo remoto en perfil inexistente. |
| Sin conexión o conexión interrumpida | Registro e inicio de sesión requieren conexión. Informar de errores sin afirmar éxito. Se puede restaurar offline una verificación confirmada localmente para esa UID; sin prueba fiable, mantener acceso limitado. Esa verificación no concede por sí sola acceso a hogares/inventario. El cierre de sesión local es posible offline, conserva pendientes y avisa que se necesitará Internet para volver a iniciar sesión. Al reconectar, refrescar verificación y permisos. |
| Cancelar o volver atrás | No se crea cuenta por cancelar. Mientras continúa en el flujo activo se conservan nombre/correo y, antes de guardar, el nombre del perfil; la contraseña solo permanece en su formulario activo. Abandonar/finalizar descarta datos parciales no guardados. En el cierre de sesión, cancelar mantiene la cuenta actual. |
| Pasar a segundo plano y regresar | Al volver desde el enlace de verificación, refrescar el estado; si aún no está confirmado, conservar la pantalla limitada y permitir comprobar manualmente. |
| Recrear la pantalla | Volver a representar el estado autenticado y de verificación conocido; no iniciar un alta duplicada. Mientras la app siga abierta y en el flujo, conservar temporalmente los campos no secretos aprobados; no persistir contraseñas. |
| Reabrir después de terminarse el proceso | Comprobar la sesión; recuperar el perfil remoto por UID si falta localmente; distinguir error de ausencia confirmada y ofrecer reintento. Descartar formularios parciales; si el perfil no se guardó, volver a pedir el nombre con la misma cuenta. |
| Enlace de verificación inválido/caducado | Explicar que no pudo validarse y ofrecer solicitar otro enlace; no desbloquear hogares/inventario. |
| Reenvío rechazado, conexión o límite | Mostrar el resultado real y una orientación clara; confirmar solicitud solo si Firebase la acepta, sin prometer entrega. |
| Cierre con pendientes | En este incremento, informar y ofrecer cancelar o cerrar conservando pendientes. No intentar sincronizarlos; esa opción se incorpora en el incremento de sincronización. |

**Puntos de la guía no aplicables y motivo:** No hay listas, navegación de inventario, permisos de cámara ni trabajo periódico en segundo plano en este incremento. Tamaños de pantalla, accesibilidad, textos y teclado sí aplican a los formularios nativos y deben comprobarse durante implementación/validación.

## Restricciones del pedido

- Comportamiento compartido en Android e iOS según aporte valor; interfaces nativas con Jetpack Compose y SwiftUI.
- Esta SPEC define resultados y criterios observables. No decide clases, interfaces, SDKs concretos por plataforma, persistencia remota, enlaces de aplicación, algoritmos, dependencias ni composición: eso corresponde al PLAN.
- No cambiar el alcance global de V1 por posponer funciones fuera de este incremento.
- No mostrar información técnica del SDK ni revelar innecesariamente si existe una cuenta.
- No considerar una consulta remota fallida como prueba de ausencia de perfil o de sesión.
- No borrar ni exponer datos pendientes al cambiar/cerrar sesión.
- Consultar antes de incorporar un comportamiento funcional que no tenga respaldo en los requisitos aprobados.

## Criterios de aceptación

- **CA-01 · RF-01:** Dado un nombre compuesto solo por espacios o caracteres invisibles, cuando se intenta registrar, entonces se señala el campo y no se inicia el alta.
- **CA-02 · RF-01:** Dado un nombre internacional válido de 1 a 100 caracteres, cuando se registra, entonces se conservan sus caracteres internos y solo se eliminan espacios exteriores.
- **CA-03 · RF-01/RF-02:** Dado un correo vacío o que no cumple la comprobación local básica, cuando se envía el formulario, entonces se señala el campo y no se solicita autenticación.
- **CA-04 · RF-01:** Dada una contraseña de alta con menos de 8 caracteres, cuando se intenta registrar, entonces se señala el campo; una contraseña de 8 o más no se rechaza por reglas de composición ni por un máximo propio.
- **CA-05 · RF-02:** Dada una contraseña no vacía en el acceso, aunque tenga menos de 8 caracteres, cuando se envía, entonces la aplicación permite que Firebase determine si las credenciales son válidas.
- **CA-06 · RF-03:** Dadas credenciales rechazadas en el acceso, cuando se muestra el error, entonces el texto no confirma si el correo está registrado y no expone mensajes técnicos del SDK.
- **CA-07 · RF-03:** Dado un fallo de conexión, límite de intentos o problema temporal, cuando falla la operación, entonces se muestra una explicación comprensible y no se presenta éxito.
- **CA-08 · RF-04:** Dada una solicitud de reenvío aceptada por Firebase, cuando termina, entonces se confirma que se solicitó el envío, sin afirmar que llegó al buzón.
- **CA-09 · RF-04/RF-05:** Dada una cuenta con correo no confirmado, cuando intenta crear/aceptar hogar o acceder a datos de hogar/inventario, entonces la operación se rechaza y la app mantiene el estado limitado.
- **CA-10 · RF-04:** Dado un enlace inválido o caducado, cuando la persona vuelve a MisCosas, entonces ve una explicación y una opción para solicitar otro, y sigue sin acceso a hogares/inventario.
- **CA-11 · RF-04/RF-08:** Dada una cuenta que abre el enlace y vuelve a la app, cuando la verificación consta como confirmada, entonces el flujo refleja el nuevo estado; si no consta, conserva acceso limitado y permite reconsultar.
- **CA-12 · RF-06:** Dada una sesión autenticada sin perfil local y un perfil remoto existente para ese UID, cuando se restaura la sesión, entonces el perfil se recupera y no se crea una segunda cuenta.
- **CA-13 · RF-06:** Dada una respuesta remota que confirma que no existe perfil, cuando la persona completa su perfil, entonces se utiliza la identidad autenticada actual.
- **CA-14 · RF-06/RF-08:** Dada una consulta remota fallida, cuando se restaura sesión, entonces se conserva la sesión, no se muestra el formulario como si la ausencia estuviera confirmada y se ofrece reintentar.
- **CA-15 · RF-06:** Dado un perfil local válido y un fallo de consulta remota, cuando se presenta el resultado, entonces no se elimina ni descarta automáticamente el perfil local.
- **CA-16 · RF-08:** Dada una sesión existente al abrir la app, mientras se comprueba, entonces no se presenta la pantalla de registro como estado definitivo; si no hay sesión, se presenta autenticación; si la comprobación falla, se presenta un error recuperable.
- **CA-17 · RF-07:** Dada una sesión no verificada, cuando la persona completa o modifica el nombre visible, entonces el perfil puede actualizarse sin desbloquear hogares/inventario.
- **CA-18 · RF-09:** Dado un cierre de sesión online u offline sin operaciones pendientes, cuando la persona lo confirma, entonces la sesión local se cierra, dejan de mostrarse los datos y el estado de navegación de esa cuenta y, si estaba offline, se informa que hará falta Internet para volver a iniciar sesión.
- **CA-19 · RF-09 — Aplicable en este incremento:** Dada una cuenta con operaciones pendientes, cuando la persona inicia el cierre, entonces se le informa antes de cerrar y puede cancelar o cerrar conservándolas. Si cancela, la sesión permanece activa; si cierra, los pendientes siguen asociados al UID original, inaccesibles desde otra cuenta y sin presentarse como sincronizados. Este incremento no los borra ni intenta sincronizarlos.
- **CA-20 · RF-09:** Dada una sesión cerrada, cuando se muestra el estado de la app o inicia sesión otra identidad, entonces no se muestran datos ni estado de navegación de la cuenta anterior.
- **CA-21 · RF-10:** Dado el mismo resultado funcional en Android e iOS, cuando se completan los flujos aprobados, entonces ambos comunican estados, restricciones y errores coherentes aunque su presentación nativa difiera.
- **CA-22 · RF-08:** Dada una cuenta cuya verificación fue confirmada antes por Firebase y quedó conservada localmente para su UID, cuando la app se reabre sin conexión, entonces restaura ese estado, sin desbloquear por ello hogares o inventario. Si no existe información local fiable o la verificación nunca fue confirmada, mantiene el acceso limitado. Al volver la conexión, actualiza el estado y comprueba permisos; no permite reutilizar la verificación con otra UID.
- **CA-23 · RF-11:** Dada una persona que navega entre pantallas del mismo flujo mientras la app sigue abierta, cuando vuelve al registro/acceso o al perfil antes de guardar, entonces conserva temporalmente nombre/correo y el nombre del perfil según corresponda.
- **CA-24 · RF-11:** Dada una contraseña en un formulario activo, cuando falla una operación, entonces sigue disponible para corregir/reintentar en ese formulario. Cuando se abandona el formulario, el registro/acceso termina correctamente o se cierra sesión, la contraseña se descarta; no aparece como borrador persistente.
- **CA-25 · RF-11:** Dado un flujo con datos parciales no guardados, cuando la persona lo abandona o termina el proceso, entonces esos datos se descartan. Al reabrir, solo se recuperan sesión y perfil guardados; una cuenta creada pero no verificada conserva la sesión y vuelve al estado de verificación, y un perfil incompleto vuelve a pedir el nombre con la identidad existente.
- **CA-26 · RF-01:** Dado un nombre de más de 100 caracteres, cuando se valida el registro, entonces se señala el campo y no se inicia el alta; un nombre válido de exactamente 100 caracteres no se rechaza por longitud.
- **CA-27 · RF-01/RF-03:** Dado un registro rechazado por credenciales o cuenta, cuando se muestra el error, entonces el mensaje no confirma si el correo ya está registrado y no expone información técnica del SDK.
- **CA-28 · RF-04/RF-06:** Dado un alta de autenticación aceptada, cuando se completa el perfil, entonces la sesión permanece activa y se presenta el estado de verificación; si falla el guardado del perfil y se reintenta, se usa la misma identidad autenticada y no se crea otra cuenta.
- **CA-29 · RF-03/RF-04:** Dado un error inesperado o un reenvío de verificación rechazado, cuando se informa el resultado, entonces se muestra un mensaje genérico comprensible o el error aplicable, respectivamente, y nunca se afirma que la solicitud fue aceptada si Firebase la rechazó.
- **CA-30 · RF-09 — Diferido; no aplicable a este incremento:** Cuando se incorpore el sistema de notificaciones, cerrar sesión cancelará los recordatorios locales de la cuenta en el dispositivo actual, retirará sus notificaciones entregadas y descartará las que estén en curso. No se mostrarán nuevas notificaciones de esa cuenta ni se afectarán otras cuentas o dispositivos. Este criterio conserva el requisito aprobado de V1, pero no se usa para validar el primer incremento.

## Cómo se comprueba el comportamiento

| Criterio | Condiciones y pasos | Resultado esperado |
| --- | --- | --- |
| CA-01–CA-05, CA-26 | Formularios con entradas válidas e inválidas, incluida longitud de nombre en el límite, contraseña corta para acceso y espacios en contraseña. | Validación conforme a RF-01/RF-02; no se transforman contraseñas ni se envían solicitudes evitables. |
| CA-06–CA-08, CA-27, CA-29 | Acceso/registro con credenciales rechazadas, correo ya utilizado, escenarios de conexión, límite, problema temporal, error inesperado y reenvío aceptado/rechazado. | Mensajes seguros, comprensibles y sin afirmar éxito cuando el servicio no aceptó la operación. |
| CA-09–CA-11 | Cuenta no verificada; abrir enlace válido/no válido/caducado, volver y realizar comprobación manual. Intentar las rutas/operaciones restringidas. | Solo verificación confirmada cambia el acceso; estados limitados permanecen bloqueados. |
| CA-12–CA-17, CA-28 | Sesión restaurada con perfil local, perfil remoto, ausencia remota confirmada y error remoto; completar/reintentar perfil tras alta aceptada. | Se conserva UID, se recupera o completa el perfil según el resultado real y no se duplican cuentas/perfiles. |
| CA-16 | Inicio en frío con sesión ausente, válida, carga aún en curso y error de consulta. | Cada estado se presenta de forma distinta y recuperable, sin falsos estados de registro. |
| CA-18–CA-20 | Cierre conectado/desconectado, con o sin operaciones pendientes; cancelar o confirmar el cierre; cambiar identidad después del cierre. | La sesión se cierra solo al confirmar; los pendientes se conservan asociados al UID y no se muestran ni procesan desde otra cuenta o se presentan como sincronizados. No se intenta sincronizar en este incremento. |
| CA-21 | Recorrido de los casos equivalentes en dispositivos o simuladores Android e iOS. | Resultados funcionales coherentes; se registran diferencias visuales nativas permitidas. |
| CA-22 | Restaurar offline una sesión verificada previamente, una cuenta no verificada y otra UID en el mismo dispositivo; después recuperar conexión. | El estado previo se reconoce solo para la UID correcta; sin prueba local fiable no se asume verificación; al volver la red se actualizan verificación y permisos. |
| CA-23–CA-25 | Navegar atrás/adelante durante registro, acceso y perfil; simular errores recuperables, éxito, cierre de sesión y terminación/reapertura de la app. | Campos no secretos se conservan solo durante el flujo activo; contraseñas se limitan al formulario activo y no son persistentes; al reabrir se restaura únicamente sesión/perfil guardados. |

Los instrumentos, tipos de tests, entornos de autenticación y comandos se
definirán en el PLAN. Los criterios que requieran Firebase real o simuladores
no se considerarán validados por tests con falsos.

## Decisiones pendientes

**Ninguna decisión funcional pendiente para el alcance aprobado.**

**Pendiente de diseño antes de implementar:** Figma iOS presenta verificación
por código, mientras el requisito aprobado exige enlace. Adaptar esa pantalla
y proponer la integración de los estados sin correspondencia comprobada. Esto
no cambia el requisito funcional.

**Dependencias técnicas para el PLAN (no son decisiones funcionales nuevas):**

- El PLAN debe inspeccionar el outbox y otros estados pendientes que el código
  existente pueda generar o encontrar. El cierre debe aislarlos por Firebase
  UID y conservarlos; no debe exponerlos a otra identidad. La sincronización
  previa al cierre se aplaza en este incremento. El intento de sincronizar al
  cerrar sesión permanece aprobado para V1 y se abordará cuando exista esa
  capacidad; el aplazamiento no reduce el alcance global.
- La limpieza de recordatorios y notificaciones al cerrar sesión permanece
  aprobada para V1, pero se aplaza al incremento que incorpore notificaciones.
  CA-30 mantiene la trazabilidad y no aplica a la validación de esta SPEC.
- La recuperación de un perfil remoto debe persistirlo localmente sin
  convertir una descarga en una mutación pendiente de subida.
- Debe comprobarse en Android e iOS que la sesión y el retorno desde el enlace
  de verificación producen estados recuperables, respetando cancelación y
  errores del proveedor.

<!-- ANTES DE SOLICITAR APROBACIÓN
Comprueba que el alcance está acordado, los flujos son coherentes, los puntos
mobile relevantes están cubiertos y cada requisito tiene criterios comprobables.
Resuelve las dudas y los marcadores pendientes. Mantén el diseño técnico en PLAN.md.
-->
