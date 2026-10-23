# MisCosas — Requisitos funcionales de V1

**Consolidación:** 22 de octubre de 2026.
**Estado del documento:** requisitos y decisiones funcionales aprobados registrados; quedan cuestiones pendientes de especificación según sus IDs.
**Etapa:** definición funcional. Este documento no constituye una SPEC de funcionalidad completa ni un PLAN aprobado y no autoriza implementación.

## Cómo leer y mantener este documento

Esta es la fuente principal del alcance funcional acordado para V1. Conserva las condiciones de Rafael, incluidas las que aún necesitan reglas detalladas. La aprobación de un bloque no implica que sus preguntas pendientes estén resueltas ni que esté implementado. No se han tomado nuevas decisiones de producto durante la consolidación.

| Estado | Identificación | Significado |
| --- | --- | --- |
| Aprobado para V1 | V1-AREA-NNN | Requisito o restricción aceptado. Puede remitir a detalles pendientes. |
| Pendiente de especificación | P-AREA-NNN | Decisión, ambigüedad o detalle aún sin resolver. No es un comportamiento elegido. |
| Propuesta no aprobada / ID de propuesta aprobada | PROP-AREA-NNN | Una propuesta no aprobada no es requisito. Si Rafael la aprueba, se conserva su ID de origen y se marca expresamente como aprobada; no se renumera. |
| Excluido de V1 o pospuesto | X-AREA-NNN | Límite explícito; cada entrada indica si afecta a V1, a este bloque o solo a una entrega posterior. |

Los identificadores son estables: no renumerarlos al insertar nuevos acuerdos; conservar la referencia de los sustituidos y explicar el cambio. El recuento de requisitos V1- se refiere a entradas con esos IDs; las propuestas aprobadas conservan sus IDs PROP- y se informan por separado. El recuento de pendientes se refiere a entradas P- marcadas como pendientes, no al número de frases o subcondiciones; las entradas P- resueltas se conservan para trazabilidad y no cuentan como pendientes abiertos. Las referencias a IDs dentro de otros párrafos no se cuentan otra vez.

Las SPEC de docs/features/ usarán sus propios RF- y CA- y referenciarán estos IDs. Desarrollarán los comportamientos pendientes y criterios observables. El PLAN contendrá clases, tablas, contratos, algoritmos, dependencias, migraciones y validación técnica. TASKS se derivará del PLAN aprobado. Una discrepancia entre documentos debe señalarse y resolverse con Rafael; no adaptar los requisitos para justificar el código existente.

El [brief original](PRODUCT_BRIEF_ORIGINAL.md) se conserva como fuente histórica íntegra. [DEVELOPMENT_STATUS.md](DEVELOPMENT_STATUS.md) describe el trabajo técnico y las evidencias, no sustituye este registro. [AGENTS.md](../AGENTS.md) mantiene las reglas de tutoría. El informe [REQUIREMENTS_CONSOLIDATION_REVIEW.md](REQUIREMENTS_CONSOLIDATION_REVIEW.md) registra la comprobación documental, no pruebas de la aplicación.

## Procedencia de los acuerdos

Los acuerdos iniciales proceden de los mensajes de Rafael anteriores a la consolidación; S19–S30 registran aprobaciones funcionales posteriores durante la reanudación. No se asignan fechas individuales de aprobación no verificadas. Las frases de esta tabla identifican el mensaje fuente; los requisitos de cada bloque desarrollan todas sus condiciones, no solo esas frases.

| Fuente | Mensaje identificable / alcance | Destino |
| --- | --- | --- |
| S00 | Prompt original; «el nombre final va a ser MisCosas» | GEN y brief histórico |
| S01 | «apruebo este alcance para el bloque de inventario básico» | INV |
| S02 | «apruebo esta distribución de roles y permisos» | ROL |
| S03 | «Prefiero la opción de código compartible» | ITC |
| S04 | «un usuario pueda pertenecer a varios hogares desde la V1» | HOG |
| S05 | «apruebo el comportamiento propuesto para las fotografías» | FOT |
| S06 | «permitir registrar compras en varias monedas desde la V1» | COM |
| S07 | «varias garantías independientes desde la V1» | GAR |
| S08 | «control del plazo de devolución como el seguimiento manual» | DEV |
| S09 | «un mismo documento pueda asociarse a varias garantías» | DOC |
| S10 | «dos modalidades de cálculo de la siguiente fecha» | MAN; aprobación repetida con igual contenido, sin duplicar requisitos |
| S11 | «combinar acontecimientos automáticos con entradas manuales» | HIS |
| S12 | «separación clara entre las alertas del inventario compartido y las preferencias individuales» | ALE |
| S13 | «con la búsqueda limitada al hogar activo» | BUS |
| S14 | «incluyendo las agrupaciones anuales y mensuales» | EST |
| S15 | «sincronización automática mediante Firebase, combinación de cambios compatibles» | SYN |
| S16 | «los tres métodos de autenticación» | AUT |
| S17 | Propuesta previa del asistente sobre cierre de sesión, cotejada por Mobile App Builder con el mensaje original; en el momento de la consolidación Rafael confirmó que todavía no estaba aprobada | PROP-SES como propuesta original, posteriormente aprobada en S19 |
| S18 | Autorización de consolidación documental, repetida tras interrupción | Este documento y enlaces de reanudación; no autoriza código ni commit |
| S19 | Aprobaciones expresas de Rafael al reanudar la definición: cierre local; tratamiento de pendientes; aislamiento y recuperación por UID y permisos; cierre offline; cancelación de recordatorios locales. Las notificaciones ya entregadas, remotas y en curso quedan pendientes | PROP-SES-001..008 aprobadas para V1; pendientes relacionados en P-SES, P-ROL, P-HOG, P-SYN y P-ALE |
| S20 | Aprobación de la política de notificaciones del dispositivo al cerrar sesión | Se completa PROP-SES-007; parte de P-ALE-003 queda resuelta y conserva pendiente la deduplicación general entre dispositivos |
| S21 | Aprobación de los criterios funcionales del nombre visible durante el registro | Se resuelve parcialmente P-AUT-001: nombre obligatorio, recorte de espacios exteriores, rechazo de valores vacíos/invisibles, soporte de nombres internacionales y límite propuesto aprobado de 100 caracteres; correo, contraseña y errores continúan pendientes |
| S22 | Aprobación de tratamiento y validación básica del correo en registro e inicio de sesión con contraseña | Se resuelve la parte del correo de P-AUT-001: obligatorio, recorte de espacios exteriores, comprobación local básica, validación definitiva por Firebase y errores coherentes que no revelen innecesariamente si la cuenta existe; contraseña y taxonomía general de errores continúan pendientes |
| S23 | Aprobación de la política de contraseña para registro e inicio de sesión con correo | Se resuelve la parte de contraseña de P-AUT-001: mínimo de 8 caracteres al registrar, sin reglas de composición, sin transformar, sin máximo propio; inicio de sesión solo exige campo no vacío y Firebase valida definitivamente. La clasificación y presentación de errores sigue pendiente |
| S24 | Aprobación de clasificación y presentación funcional de errores de autenticación por correo y contraseña | Se completa P-AUT-001. Los errores locales van junto al campo y evitan llamadas innecesarias; credenciales/cuenta no revelan si el correo existe; conexión, exceso de intentos y fallos temporales dan instrucciones comprensibles; errores inesperados son genéricos; los errores de formulario se muestran junto al campo y los generales al nivel del formulario. Se mantiene activa la protección de Firebase contra enumeración. La clasificación técnica queda para SPEC/PLAN |
| S25 | Aprobación del acceso limitado mientras el correo no está verificado | Se resuelve parcialmente P-AUT-002: el usuario mantiene sesión y puede completar/editar perfil, consultar verificación, solicitar reenvío y cerrar sesión; no crea/acepta hogares ni accede a datos de hogares/inventario. El límite debe protegerse también en operaciones y acceso a datos, no solo ocultando pantallas. Siguen pendientes detalles de reenvío, caducidad y retorno desde el enlace |
| S26 | Aprobación del comportamiento ante perfil remoto al restaurar sesión autenticada | Se resuelve funcionalmente P-AUT-005 y se concreta V1-AUT-011: recuperar el perfil remoto por Firebase UID cuando falta localmente; distinguir ausencia confirmada de error; no duplicar cuenta/perfil; conservar un perfil local válido si falla la consulta y someter su uso offline a las reglas aprobadas de acceso, sesión y sincronización |
| S27 | Aprobación del comportamiento ante reenvío y retorno desde la verificación de correo | Se completa P-AUT-002 funcionalmente: confirmar que se solicitó el envío sin prometer entrega; enlace inválido/caducado explica el problema y permite pedir otro; al volver se actualiza el estado y solo el estado confirmado desbloquea hogares/inventario; se permite comprobación manual; conexión y límites de envío se comunican con claridad. Vida útil y límites concretos de Firebase y la integración técnica quedan para SPEC/PLAN |
| S28 | Aprobación del comportamiento de restauración offline del estado de verificación previamente confirmado | Una verificación confirmada previamente por Firebase puede reconocerse offline si su estado local fiable está asociado al mismo Firebase UID. Sin prueba local fiable, la cuenta sigue limitada. Al recuperar conexión se actualizan verificación y permisos; el estado local no sustituye autorizaciones del servidor ni concede por sí solo acceso a hogares/inventario |
| S29 | Aprobación de conservación temporal y descarte de datos de formularios de autenticación/perfil | Mientras la app permanece abierta y dentro del flujo, conservar temporalmente nombre y correo entre pantallas; conservar el nombre del perfil al volver antes de guardarlo. La contraseña solo permanece en el formulario activo, se limpia al salir, completar correctamente o cerrar sesión, y nunca se guarda como borrador persistente. Al abandonar/finalizar el flujo o terminar el proceso, descartar datos parciales no guardados. Al reabrir, restaurar solo sesión y perfil guardados. Un registro completado y aún no verificado conserva cuenta/sesión en verificación; un perfil pendiente vuelve a solicitar el nombre con la identidad autenticada actual, sin crear otra cuenta |
| S30 | Aprobación del alcance incremental del cierre de sesión en la primera SPEC | El primer incremento implementa cierre local online/offline, advierte de operaciones pendientes y ofrece cancelar o cerrar conservándolas, aisladas por Firebase UID. No las borra, procesa ni presenta como sincronizadas; la opción de intentar sincronizar se aplaza hasta el incremento que incorpore esa capacidad, sin alterar el comportamiento global aprobado para V1. La gestión general de conflictos y de notificaciones se implementará en sus incrementos correspondientes; CA-30 queda diferido y no aplica a las pruebas de este incremento. El PLAN debe revisar las operaciones pendientes que el código actual puede generar o encontrar |

## Contexto y restricciones transversales

**Estado de las entradas V1-GEN: Aprobado para V1. Fuente: S00 y restricciones reiteradas en S01–S16.**

- **V1-GEN-001:** El nombre final es MisCosas. Es un pasaporte digital personal y familiar de objetos y su información; comienza en España y español, preparado para internacionalización.
- **V1-GEN-002:** Android e iOS tendrán interfaces nativas, Compose y SwiftUI respectivamente, que pueden diferir visualmente. Kotlin Multiplatform compartirá lógica, modelos, validaciones, repositorios y persistencia cuando aporte valor; no se forzará compartir una implementación si la específica es claramente mejor.
- **V1-GEN-003:** El enfoque es local-first: los datos disponibles se consultan y modifican sin conexión. La persistencia local es la fuente principal de la UI; las pantallas no dependen directamente de Firebase ni de una API remota. La sincronización sirve para hogares compartidos, varios dispositivos y recuperación al cambiar de móvil.
- **V1-GEN-004:** Hogares compartidos, miembros, invitaciones, permisos y sincronización forman parte de V1. No reducirla a una aplicación individual ni confundir el orden gradual de implementación con una exclusión de estas funciones.
- **V1-GEN-005:** Mantener una arquitectura entendible y mantenible por una persona, adecuada para portfolio, con separación presentation/domain/data y preferencia MVVM. No introducir interfaces, casos de uso, capas o dependencias sin necesidad y justificación.
- **V1-GEN-006:** Conservar las categorías iniciales del brief: Tecnología, Electrodomésticos, Imagen y sonido, Hogar, Cocina, Movilidad, Herramientas, Deporte, Moda y accesorios y Otros. El modelo debe permitir una posible ampliación a categorías personalizadas sin complicar innecesariamente V1.
- **V1-GEN-007:** Usar una estrategia multiplataforma para identidades y fechas, sin tipos Android dentro de commonMain. Distinguir días de calendario, instantes UTC y horas locales según su significado; los detalles técnicos corresponden al PLAN. Dinero exacto, sin Float/Double para almacenar importes.
- **V1-GEN-008:** Respetar los diseños nativos y el sistema visual existentes. Figma orienta la experiencia, pero no aprueba funciones por sí mismo. Los comportamientos aprobados ausentes del diseño y las diferencias funcionales entre plataformas deberán identificarse y acordarse visualmente antes de implementarlos.

**Pendiente de especificación:**

- **P-GEN-001:** Completar el mapa de asuntos aún no definidos: gestión posterior del perfil, eliminación de cuenta/datos, cierre de hogar y demás acciones que aparezcan en diseños pero no tengan un bloque aprobado. Su presencia no amplía V1 automáticamente.
- **P-GEN-002:** Precisar validaciones y comportamiento móvil de cada futura SPEC, incluida accesibilidad, errores, conservación de estado y criterios observables de rendimiento. Las guías no fijan por sí solas requisitos nuevos.
- **P-GEN-003:** Acordar el orden de las SPEC y entregas después de revisar esta consolidación. Registro y recuperación de sesión/perfil sigue siendo una propuesta de primera funcionalidad, no un PLAN aprobado.

## 01. Inventario básico — INV

**Estado de las entradas V1-INV: Aprobado para V1. Fuente: S01.**

- **V1-INV-001:** Incluir registro de objetos, información de compra, fotografías, consulta, edición, favoritos y los siete estados acordados.
- **V1-INV-002:** Inicialmente solo nombre y categoría son obligatorios. Marca, modelo, número de serie, datos de compra y fotografías son opcionales.
- **V1-INV-003:** Un objeto podrá tener varias fotografías; no será necesario adjuntar ninguna para crearlo.
- **V1-INV-004:** Los favoritos serán individuales para cada miembro del hogar; los estados de los objetos serán compartidos.
- **V1-INV-005:** Mantener los siete estados funcionales ACTIVE, SOLD, GIFTED, BROKEN, LOST, RECYCLED y ARCHIVED. Su representación en el código y relación con el indicador de archivado actual se revisarán sin eliminar ninguno del alcance.
- **V1-INV-006:** Los objetos pertenecen al hogar. Los miembros con permisos suficientes pueden crear, consultar, editar y cambiar su estado conforme a ROL.
- **V1-INV-007:** Consultar, crear y modificar objetos sin Internet es fundamental. Los cambios deben persistirse y sincronizarse al recuperar conectividad conforme a SYN.
- **V1-INV-008:** Definir la resolución de modificaciones simultáneas por distintos miembros evitando pérdidas de información siempre que sea posible.
- **V1-INV-009:** Preparar el modelo para garantías, devoluciones, documentos, mantenimiento y demás funcionalidades acordadas. No implementarlas anticipadamente dentro del incremento de inventario básico ni excluirlas por ello de la V1 global.

**Pendiente de especificación:**

- **P-INV-001:** Validaciones concretas de nombre, categoría y campos opcionales; transiciones y consecuencias de los siete estados.
- **P-INV-002:** Relación funcional entre ARCHIVED, restauración y los demás estados; el código actual tiene archivado separado. No decidir por ello que uno de los estados deja de existir.
- **P-INV-003:** Datos adicionales de venta, regalo, pérdida o incidencia presentes en Figma que no estén cubiertos expresamente por el alcance anterior.

## 02. Roles, permisos y administración — ROL

**Estado de las entradas V1-ROL: Aprobado para V1. Fuente: S02.**

- **V1-ROL-001:** Usar dos roles: administrador del hogar y miembro.
- **V1-ROL-002:** Todos los miembros podrán consultar, crear, editar y cambiar el estado de cualquier objeto del hogar, independientemente de quién lo registró.
- **V1-ROL-003:** Todos los miembros podrán añadir y eliminar fotografías y gestionar sus propios favoritos; la selección de la foto principal se incluye en FOT.
- **V1-ROL-004:** Los administradores podrán invitar, retirar miembros y transferir la administración. La diferencia entre roles estará en la administración del hogar.
- **V1-ROL-005:** Puede haber varios administradores; debe existir al menos uno en todo momento.
- **V1-ROL-006:** Cualquier miembro puede abandonar voluntariamente el hogar. El último administrador deberá transferir previamente la administración o resolver el cierre del hogar.
- **V1-ROL-007:** Una persona retirada pierde acceso al inventario compartido. Los objetos que registró permanecen en el hogar.
- **V1-ROL-008:** Los cambios offline de alguien que pierde permisos antes de sincronizarlos no se aplicarán automáticamente. Una vez confirmada la pérdida de acceso, no se mostrará el inventario del hogar ni se sincronizarán sus pendientes. Se informará al usuario y no se borrarán pendientes de forma automática y silenciosa; su tratamiento y posible recuperación siguen pendientes de especificación.
- **V1-ROL-009:** El PLAN debe adaptar Item y la persistencia para que los favoritos se asocien a cada usuario, no al objeto global.
- **V1-ROL-010:** Conservar información básica de quién creó un objeto y quién lo modificó por última vez. Esto no exige un historial técnico completo de cada edición.
- **V1-ROL-011:** No introducir permisos individuales por objeto en V1. Los permisos de inventario serán iguales para los miembros y administradores autorizados.

**Pendiente de especificación:**

- **P-ROL-001:** Flujos de promoción, retirada o transferencia entre administradores y protección del último administrador; no asumir que transferir siempre degrada al anterior.
- **P-ROL-002:** Cierre de hogar y salida del último administrador/miembro: efectos, confirmaciones y tratamiento de datos.
- **P-ROL-003:** Cómo detectar una revocación cuando el dispositivo está offline y cómo tratar, conservar e informar sobre las copias locales y los cambios pendientes bloqueados. Una vez confirmada la pérdida de acceso, no se mostrará el hogar ni se sincronizarán sus pendientes; estos no se borrarán automáticamente y su posible recuperación sigue por definir.
- **P-ROL-004:** Privacidad y presentación de autoría de antiguos miembros, sin perder los objetos e historial que deban permanecer.

## 03. Invitaciones mediante código — ITC

**Estado de las entradas V1-ITC: Aprobado para V1. Fuente: S03.**

- **V1-ITC-001:** Invitar mediante código compartible en V1, orientado a familiares o personas de confianza.
- **V1-ITC-002:** Solo los administradores pueden generar y revocar invitaciones.
- **V1-ITC-003:** Cada código vale siete días desde su generación.
- **V1-ITC-004:** Cada código es de un solo uso. Para varias personas se genera un código por persona.
- **V1-ITC-005:** Cualquier usuario autenticado puede introducir un código válido. Antes de incorporarse verá el nombre del hogar y deberá confirmar expresamente que desea unirse.
- **V1-ITC-006:** Los invitados entran como miembros, nunca directamente como administradores.
- **V1-ITC-007:** Los códigos serán suficientemente aleatorios para impedir que puedan adivinarse fácilmente. Validación, consumo y asignación al hogar deben realizarse de forma segura y atómica en el backend.
- **V1-ITC-008:** Generar, validar, aceptar y revocar invitaciones requiere Internet. Esto no modifica el funcionamiento offline del inventario.
- **V1-ITC-009:** Contemplar estados pendiente, aceptada, caducada y revocada.

**Pendiente de especificación:**

- **P-ITC-001:** Comportamiento al aceptar un código de un hogar al que el usuario ya pertenece.
- **P-ITC-002:** Código que caduca, se revoca o se consume mientras el usuario está en la confirmación; reintentos y resultados comprensibles sin doble incorporación.
- **P-ITC-003:** Presentación y uso exacto del código y controles técnicos de seguridad, sin introducir un sistema complejo de permisos sobre invitaciones.

## 04. Múltiples hogares y hogar activo — HOG

**Estado de las entradas V1-HOG: Aprobado para V1. Fuente: S04.**

- **V1-HOG-001:** Un usuario puede pertenecer a varios hogares desde V1.
- **V1-HOG-002:** Cada hogar conserva inventario, miembros, invitaciones y configuración independientes. Los objetos nunca se mezclan entre hogares.
- **V1-HOG-003:** Los roles dependen de cada hogar: una misma persona puede ser administrador en uno y miembro en otro; sus permisos corresponden al hogar de la operación.
- **V1-HOG-004:** Incluir un selector para cambiar fácilmente entre hogares y mostrar claramente cuál está activo.
- **V1-HOG-005:** Recordar el último hogar seleccionado al reabrir, siempre que el usuario conserve acceso.
- **V1-HOG-006:** Un usuario puede crear varios hogares y será administrador de cada uno que cree.
- **V1-HOG-007:** Aceptar una invitación permite unirse a otro hogar sin abandonar los anteriores.
- **V1-HOG-008:** Separar los inventarios también en almacenamiento local. Consultar y modificar offline la información previamente sincronizada de los hogares accesibles, respetando las limitaciones de seguridad por definir.
- **V1-HOG-009:** Asociar los pendientes al hogar correspondiente. Cambiar de hogar no pierde datos ni mezcla operaciones pendientes.
- **V1-HOG-010:** Al abandonar o ser expulsado se pierde el acceso al inventario del hogar. Una vez confirmada la pérdida de acceso, no se mostrará ese inventario ni se sincronizarán sus operaciones pendientes. No se borrarán pendientes de forma automática y silenciosa; su tratamiento y posible recuperación quedan pendientes de especificación.
- **V1-HOG-011:** Los favoritos son por usuario y asociados a objetos de su hogar correspondiente.
- **V1-HOG-012:** No establecer inicialmente un límite arbitrario de hogares por usuario. Cualquier restricción técnica razonable futura deberá justificarse.

**Pendiente de especificación:**

- **P-HOG-001:** Selección inicial o alternativa si el último hogar ya no existe o no es accesible, y comportamiento mientras se comprueban pertenencias.
- **P-HOG-002:** Datos de un hogar todavía no descargados y límites del acceso offline, incluida la detección de una pérdida de permisos aún no recibida; coordinar con P-ROL-003 y P-SYN-004. Tras confirmar la pérdida de acceso, se aplican V1-HOG-010.
- **P-HOG-003:** Gestión de hogar activo al abrir una alerta de otro hogar y al perder acceso, sin mezclar consultas ni operaciones.

## 05. Fotografías de objetos — FOT

**Estado de las entradas V1-FOT: Aprobado para V1. Fuente: S05.**

- **V1-FOT-001:** Permitir cámara, galería y archivos en Android e iOS, mediante mecanismos nativos.
- **V1-FOT-002:** Las fotografías son opcionales y múltiples. Crear y editar objetos sin fotografías.
- **V1-FOT-003:** Un objeto puede tener una fotografía principal para el listado; todas se consultan desde la ficha. Si solo hay una, será automáticamente la principal.
- **V1-FOT-004:** Al eliminar la principal y quedar otras, elegir otra automáticamente de forma determinista. El usuario podrá cambiarla después.
- **V1-FOT-005:** Todos los miembros pueden añadir, eliminar y seleccionar la fotografía principal de cualquier objeto del hogar.
- **V1-FOT-006:** Añadir y eliminar fotografías offline, con efecto inmediato en la interfaz y cambios pendientes de sincronización.
- **V1-FOT-007:** Conservar persistentemente las imágenes pendientes de subida incluso tras cierre o reinicio. No eliminarlas antes de confirmar su correcta sincronización.
- **V1-FOT-008:** Mostrar offline las fotografías disponibles localmente. Si una no se ha descargado, indicar su indisponibilidad sin bloquear el resto de la ficha.
- **V1-FOT-009:** Mostrar errores de sincronización comprensibles y permitir reintentos sin perder imágenes ni operaciones pendientes.
- **V1-FOT-010:** Cancelar la selección o denegar permisos no debe perder el formulario. Se puede continuar sin fotografías o utilizar otra fuente disponible.
- **V1-FOT-011:** Admitir JPEG, PNG y HEIC/HEIF cuando sean compatibles. Definir límites de cantidad, tamaño y resolución según costes de Firebase y transferencia; evitar restricciones arbitrarias y aplicar compresión/redimensionamiento conveniente sin degradar excesivamente la calidad.
- **V1-FOT-012:** Las fotos pertenecen al objeto y hogar, son compartidas con sus miembros autorizados y no accesibles a usuarios ajenos.
- **V1-FOT-013:** Diferenciar fotografía sincronizada, pendiente de subida y pendiente de descarga. La disponibilidad local y el estado de sincronización son conceptos independientes.
- **V1-FOT-014:** Resolver en su especificación los conflictos entre modificaciones, eliminaciones y selección de foto principal.

**Pendiente de especificación:**

- **P-FOT-001:** Límites concretos, compatibilidad y transformación de formatos; calidad, tamaño y resolución de las imágenes.
- **P-FOT-002:** Política de caché y conservación después de sincronizar, incluida descarga para consulta offline.
- **P-FOT-003:** Regla determinista para sustituir la principal y conflictos sobre esa elección.
- **P-FOT-004:** Relación entre conservación de pendientes y eliminación intencionada de una imagen antes de subirla; no inferir una excepción al requisito de conservación.

## 06. Compra y multimoneda — COM

**Estado de las entradas V1-COM: Aprobado para V1. Fuente: S06 y datos de compra aprobados en S01.**

- **V1-COM-001:** Los datos de compra son opcionales. Registrar fecha, precio y vendedor/tienda sin obligar a completar todos los datos.
- **V1-COM-002:** Permitir varias monedas desde V1, con EUR como predeterminada inicial al introducir el precio. El usuario puede seleccionar otra al introducir o editar la compra.
- **V1-COM-003:** Incluir monedas reconocidas por ISO 4217, como EUR, USD, GBP, JPY y CHF, sin limitar artificialmente el listado a unas pocas.
- **V1-COM-004:** Conservar el precio histórico original y su moneda. No convertir automáticamente los importes ni calcular el valor actual del objeto.
- **V1-COM-005:** Almacenar importes exactos respetando los decimales de cada moneda y evitando errores de coma flotante.
- **V1-COM-006:** Precio vacío significa información desconocida; cero es válido y distinto de desconocido.
- **V1-COM-007:** Rechazar importes negativos. Si hay precio, debe existir moneda asociada.
- **V1-COM-008:** Mostrar precios según las convenciones de moneda y configuración regional del dispositivo sin alterar el valor almacenado.
- **V1-COM-009:** Introducir, consultar y modificar precios y monedas offline, igual que el inventario compartido.
- **V1-COM-010:** La fecha de compra representa un día de calendario y no cambia al usar otra zona horaria.
- **V1-COM-011:** El modelo debe permitir futuras conversiones, estadísticas y valoraciones económicas sin modificar los importes históricos originales. Esto no incorpora funciones financieras adicionales a V1; las estadísticas aprobadas se concretan en EST.

**Pendiente de especificación:**

- **P-COM-001:** Inclusión de forma de pago.
- **P-COM-002:** Tratamiento de fechas de compra futuras.
- **P-COM-003:** Validación de entrada, catálogo de monedas y presentación de sus decimales; no elegir aún límites de importe ni una dependencia técnica.
- **P-COM-004:** Cualquier otro dato de compra mostrado en Figma, como número de pedido, requiere decidir su alcance; no está aprobado por aparecer en el diseño.

## 07. Garantías independientes — GAR

**Estado de las entradas V1-GAR: Aprobado para V1. Fuente: S07.**

- **V1-GAR-001:** Un objeto puede existir sin garantías o tener varias independientes, como legal, comercial del fabricante y ampliación contratada. Añadir una no modifica ni sustituye las existentes.
- **V1-GAR-002:** Registrar tipo, proveedor, fecha de inicio, fecha final, notas y documentos asociados. Los mínimos obligatorios quedan pendientes.
- **V1-GAR-003:** Mostrar por garantía los estados pendiente de inicio, activa, próxima a vencer, vencida e información insuficiente. Calcularlos desde las fechas disponibles; no almacenarlos como estados fijos que se desactualicen.
- **V1-GAR-004:** Las fechas son días de calendario estables ante cambios de zona horaria. La fecha final no puede ser anterior al inicio.
- **V1-GAR-005:** Las fechas introducidas por el usuario son la referencia. No presumir automáticamente una duración legal de garantía en V1.
- **V1-GAR-006:** Cada garantía puede tener avisos de vencimiento independientes. Configuración, destinatarios y offline se rigen por ALE y sus pendientes.
- **V1-GAR-007:** Todos los miembros pueden consultar, añadir y editar garantías de cualquier objeto, incluso offline, y sincronizar después.
- **V1-GAR-008:** Asociar documentos como facturas, certificados o contratos de ampliación, reutilizando la gestión DOC sin duplicar funciones de archivos.
- **V1-GAR-009:** Permitir eliminar una garantía sin afectar al objeto ni a sus otras garantías. Concretar efectos sobre documentos y eliminaciones pendientes de sincronización.
- **V1-GAR-010:** Cada garantía tiene identidad propia para permitir cambios independientes y aplicar la estrategia de conflictos.
- **V1-GAR-011:** Preparar la extensión futura a reclamaciones, reparaciones cubiertas, condiciones especiales y otros datos, sin implementarlos ahora.

**Pendiente de especificación:**

- **P-GAR-001:** Datos mínimos obligatorios y significado de fechas desconocidas.
- **P-GAR-002:** Umbral de próxima a vencer y reglas de cálculo en límites/fechas incompletas, incluida la relación con pendiente de inicio.
- **P-GAR-003:** Efectos de eliminar garantías con documentos y operaciones pendientes.
- **P-GAR-004:** Resolución concreta de modificaciones simultáneas de una garantía y sus relaciones.

## 08. Plazo y seguimiento de devolución — DEV

**Estado de las entradas V1-DEV: Aprobado para V1. Fuente: S08.**

- **V1-DEV-001:** Los datos de devolución son opcionales. Se puede registrar vendedor y fecha límite sin completar necesariamente ambos.
- **V1-DEV-002:** Mostrar días restantes y estado del plazo: abierto, próximo a finalizar, vencido o información insuficiente; umbral pendiente.
- **V1-DEV-003:** La fecha límite es un día de calendario que no cambia con la zona horaria.
- **V1-DEV-004:** No presumir plazos legales de 14, 30 u otros días. Usar la información del usuario.
- **V1-DEV-005:** Separar el plazo del seguimiento manual: sin devolución solicitada, devolución solicitada, devolución completada y objeto conservado.
- **V1-DEV-006:** Marcar solicitada solo registra que el usuario inició el trámite con el vendedor. MisCosas no envía solicitudes ni contacta con la tienda.
- **V1-DEV-007:** Permitir corregir el seguimiento ante errores o cambios de situación. Las transiciones y consecuencias se concretarán.
- **V1-DEV-008:** Completar la devolución no elimina automáticamente el objeto. Decidir después si archivar, añadir un estado específico o proponer una acción posterior; no aplicar un cambio automático irreversible.
- **V1-DEV-009:** Permitir recordatorios antes del vencimiento. Dejar de emitir avisos del plazo cuando la devolución esté completada o se haya decidido conservar el objeto.
- **V1-DEV-010:** Todos los miembros pueden consultar y modificar la devolución de cualquier objeto offline; conservar y sincronizar después respetando permisos y conflictos.
- **V1-DEV-011:** La interfaz distinguirá claramente estado del plazo y seguimiento, especialmente solicitada frente a completada.

**Pendiente de especificación:**

- **P-DEV-001:** Umbral de proximidad y tratamiento de fecha desconocida.
- **P-DEV-002:** Transiciones permitidas, correcciones y reactivación de avisos al cambiar un seguimiento completado/conservado.
- **P-DEV-003:** Efecto definitivo de completar una devolución sobre inventario, estados y estadísticas; no añadir RETURNED por suposición.
- **P-DEV-004:** Consecuencias de correcciones simultáneas y sincronización del seguimiento.

## 09. Documentos y asociaciones — DOC

**Estado de las entradas V1-DOC: Aprobado para V1. Fuente: S09.**

- **V1-DOC-001:** Los documentos son opcionales y múltiples por objeto, sujetos a un límite técnico por definir.
- **V1-DOC-002:** Clasificar como ticket, factura, garantía, manual u otros (RECEIPT, INVOICE, WARRANTY, MANUAL, OTHER). Permitir cambiar la clasificación.
- **V1-DOC-003:** Incorporar desde archivos, cámara o galería mediante mecanismos nativos Android/iOS.
- **V1-DOC-004:** Incluir escaneado con cámara en V1 aprovechando APIs nativas cuando estén disponibles; sin reconocimiento documental propio ni OCR.
- **V1-DOC-005:** Admitir PDF e imágenes habituales. Otros formatos y límites quedan pendientes.
- **V1-DOC-006:** Todos los miembros autorizados pueden consultar, abrir, renombrar, reclasificar y eliminar documentos de cualquier objeto del hogar.
- **V1-DOC-007:** Compartir/exportar un documento mediante opciones nativas. Esto no cambia sus permisos en MisCosas; advertir que el archivo sale del entorno privado de la aplicación.
- **V1-DOC-008:** Permitir sustituir explícitamente el archivo de un documento existente sin perder necesariamente metadatos ni asociaciones. Concretar el comportamiento ante errores y conflictos.
- **V1-DOC-009:** Un documento se almacena una sola vez por objeto y puede asociarse a varias garantías del mismo objeto, sin duplicar el archivo por sus asociaciones. Permitir ampliar las asociaciones a devoluciones o mantenimiento en el futuro.
- **V1-DOC-010:** Desvincular una garantía solo elimina esa relación. Eliminar el documento lo retira del objeto y afecta a todas sus asociaciones; informar de las consecuencias y solicitar confirmación previamente.
- **V1-DOC-011:** Incorporar documentos offline. Conservar persistentemente archivos pendientes hasta confirmar su subida; editar también sus metadatos y asociaciones sin conexión.
- **V1-DOC-012:** Abrir offline los archivos disponibles localmente. Indicar cuando falta descargar un documento sin bloquear el resto del objeto.
- **V1-DOC-013:** Separar disponibilidad local y sincronización, con errores reintentables sin perder archivos ni operaciones.
- **V1-DOC-014:** Los documentos pertenecen al hogar y solo son accesibles por miembros autorizados. No exponer archivos privados mediante enlaces públicos permanentes.
- **V1-DOC-015:** Evitar duplicaciones innecesarias y definir límites razonables considerando Firebase Storage, transferencia y espacio local.
- **V1-DOC-016:** La identidad documental es independiente de las asociaciones. Distinguir archivo físico, metadatos y relaciones; eliminar una relación no debe borrar accidentalmente el archivo.

**Pendiente de especificación:**

- **P-DOC-001:** Cantidad, tamaño, formatos adicionales y alternativas cuando no haya escáner nativo disponible.
- **P-DOC-002:** Política de conservación, caché y descarga local, y relación entre borrar voluntariamente y conservar subidas pendientes.
- **P-DOC-003:** Reemplazo: metadatos/asociaciones conservados, errores, cancelaciones, versiones y modificaciones simultáneas.
- **P-DOC-004:** Efectos exactos de eliminación sobre relaciones, historial y operaciones pendientes; conflictos entre eliminación y edición.
- **P-DOC-005:** Resolución de conflictos de metadatos, archivo físico y asociaciones entre miembros.

## 10. Mantenimiento y realizaciones — MAN

**Estado de las entradas V1-MAN: Aprobado para V1. Fuente: S10.**

- **V1-MAN-001:** Un objeto puede tener varias tareas independientes o ninguna.
- **V1-MAN-002:** Registrar nombre, descripción, instrucciones o notas, periodicidad y primera fecha prevista; concretar campos obligatorios.
- **V1-MAN-003:** Representar intervalos en días, semanas, meses y años; respetar el calendario para meses y años, no una cadena libre como único modelo de periodicidad.
- **V1-MAN-004:** Ofrecer modalidad desde la última realización como predeterminada: sumar la periodicidad a la fecha real de finalización. Una tarea mensual prevista el 1 de abril completada el 10 vence después el 10 de mayo.
- **V1-MAN-005:** Ofrecer calendario fijo: mantener las fechas de la periodicidad original independientemente de cuándo se realice. En el ejemplo anterior, la siguiente seguirá el 1 de mayo.
- **V1-MAN-006:** Permitir elegir y editar la modalidad al crear o modificar la tarea periódica, explicando brevemente su diferencia.
- **V1-MAN-007:** Incluir mantenimientos puntuales sin repetición.
- **V1-MAN-008:** Cada realización registra fecha, miembro que la registra y notas opcionales. Conservar las realizaciones anteriores; no confundir registrar con demostrar quién ejecutó físicamente la tarea.
- **V1-MAN-009:** Mostrar tareas futuras, que vencen hoy y vencidas; distinguir puntuales completadas de pendientes.
- **V1-MAN-010:** Todos los miembros pueden crear, consultar, editar y completar tareas, incluso offline.
- **V1-MAN-011:** Recordatorios configurables por tarea conforme a ALE; reglas y destinatarios personales allí acordados.
- **V1-MAN-012:** Conservar y sincronizar realizaciones offline sin perder historial.
- **V1-MAN-013:** Incorporar al PLAN la adaptación del modelo para identificar al miembro que registra cada realización.

**Pendiente de especificación:**

- **P-MAN-001:** Campos obligatorios y validación de intervalos y primera fecha.
- **P-MAN-002:** Corrección o eliminación de una realización errónea y efecto en vencimientos/historial.
- **P-MAN-003:** Pausar y reanudar tareas periódicas: reglas y efectos.
- **P-MAN-004:** Eliminación de una tarea con historial.
- **P-MAN-005:** Días 31, 29 de febrero y periodicidad fija cuando se omiten varios vencimientos; no fijar por suposición una regla de ajuste.
- **P-MAN-006:** Realizaciones duplicadas por miembros offline; distinguir duplicado funcional de reintento técnico idempotente.
- **P-MAN-007:** Cambio de periodicidad o modalidad después de registrar realizaciones.

## 11. Historial del objeto — HIS

**Estado de las entradas V1-HIS: Aprobado para V1. Fuente: S11.**

- **V1-HIS-001:** Generar acontecimientos automáticos por creación, incorporación de información de compra, garantías y documentos añadidos, mantenimiento realizado y cambios de estado; incluir venta, regalo, incidencias, archivado y restauración cuando correspondan.
- **V1-HIS-002:** Permitir entradas manuales para reparaciones y otros acontecimientos relevantes que no procedan de operaciones de la aplicación. No obligar a volver a introducir acontecimientos que MisCosas ya conoce.
- **V1-HIS-003:** Mostrar qué ocurrió, fecha correspondiente, miembro que lo registró y detalles relevantes disponibles.
- **V1-HIS-004:** Separar fecha real del acontecimiento y fecha/hora de registro en MisCosas, permitiendo incorporar sucesos pasados sin alterar su posición cronológica.
- **V1-HIS-005:** Todos los miembros autorizados consultan la cronología. Las entradas manuales pueden añadirse offline y sincronizarse después.
- **V1-HIS-006:** Generar automáticamente acontecimientos de realizaciones, estados y otras operaciones reutilizando sus registros originales cuando sea posible. Los reintentos de sincronización no duplican entradas.
- **V1-HIS-007:** Registrar una cronología útil de la vida del objeto, no cada pequeña edición ni un registro técnico excesivo.
- **V1-HIS-008:** Ordenar por fecha del acontecimiento y usar un criterio estable cuando coincida la fecha.
- **V1-HIS-009:** Conservar acontecimientos de miembros que abandonan el hogar, respetando reglas de privacidad por definir.
- **V1-HIS-010:** Eliminar documentos, garantías u otros registros relacionados no debe dejar referencias rotas ni producir pérdidas silenciosas de información histórica.
- **V1-HIS-011:** Respetar pantallas y flujos de historial Android/iOS en Figma; identificar comportamientos aprobados no representados y decidir su integración antes de implementar, sin rediseños ni pantallas innecesarias.
- **V1-HIS-012:** Usar identificadores estables y asociación al objeto y hogar para funcionamiento offline, sincronización y múltiples miembros.

**Pendiente de especificación:**

- **P-HIS-001:** Permisos y reglas para editar/eliminar entradas manuales, corregir acontecimientos y resolver conflictos.
- **P-HIS-002:** Precisión de la fecha del suceso, dirección de orden y desempate estable; no imponer el orden actual del DAO como decisión de producto.
- **P-HIS-003:** Datos históricos a conservar cuando desaparece un registro relacionado o un miembro, respetando privacidad y referencias.
- **P-HIS-004:** Integración visual de entradas manuales y correcciones que no estén representadas.

## 12. Alertas y recordatorios — ALE

**Estado de las entradas V1-ALE: Aprobado para V1. Fuente: S12.**

- **V1-ALE-001:** Las situaciones que requieren atención pertenecen al objeto y hogar; todos los miembros autorizados pueden consultarlas independientemente de sus preferencias de notificación.
- **V1-ALE-002:** Incluir garantías próximas a vencer, plazos de devolución próximos a finalizar, mantenimiento próximo o que vence hoy y mantenimiento atrasado. Umbrales y reglas exactas pendientes.
- **V1-ALE-003:** Cada miembro activa/desactiva sus recordatorios y configura la antelación sin afectar a los demás.
- **V1-ALE-004:** Mostrar objeto, hogar, motivo y fecha relevante. Al seleccionar, abrir la garantía, devolución o tarea correspondiente aunque sea de otro hogar distinto al activo.
- **V1-ALE-005:** Los recordatorios pueden proceder de cualquiera de los hogares del usuario; cambiar de hogar activo no los desactiva.
- **V1-ALE-006:** Calcular alertas con información local. Los recordatorios locales funcionan offline cuando el sistema operativo lo permita; los cambios de otros miembros se reflejan al sincronizar.
- **V1-ALE-007:** Denegar notificaciones no impide usar la app ni consultar alertas internas. El usuario puede cambiar los permisos desde los ajustes del sistema.
- **V1-ALE-008:** Cambios de fechas, estados, realizaciones y devoluciones actualizan alertas/recordatorios, evitando avisos obsoletos o duplicados siempre que sea posible. Aplicar la supresión acordada para devolución completada o conservada.
- **V1-ALE-009:** Silenciar y descartar son acciones individuales que no modifican el estado compartido del objeto.
- **V1-ALE-010:** Conservar y sincronizar las preferencias personales entre los dispositivos del mismo usuario, respetando permisos de notificación particulares de cada dispositivo.
- **V1-ALE-011:** Las notificaciones no deben revelar información de hogares a los que el usuario ha perdido acceso. Al cerrar sesión se aplica también la política aprobada en PROP-SES-007; los demás casos de privacidad ante avisos programados, datos locales y cambios de permisos siguen pendientes.
- **V1-ALE-012:** Respetar listado, detalle y configuración de Figma Android/iOS; identificar diferencias con el alcance antes de implementar.
- **V1-ALE-013:** Evitar notificaciones duplicadas y distinguir alertas calculadas, preferencias personales y notificaciones programadas en cada dispositivo. Compartir la lógica adecuada en KMP y usar APIs nativas Android/iOS.

**Pendiente de especificación:**

- **P-ALE-001:** Horarios, zonas horarias, umbrales, repeticiones, antelaciones y configuración inicial; distinguir umbral de una alerta compartida de antelación personal.
- **P-ALE-002:** Significado, duración y sincronización de silencio y descarte.
- **P-ALE-003:** Siguen pendientes la deduplicación general entre dispositivos y el comportamiento ante avisos duplicados o cambios remotos recibidos tarde. La política específica al cerrar sesión quedó resuelta por S20 y se registra en PROP-SES-007: retirar del centro de notificaciones del dispositivo actual las notificaciones ya entregadas de la cuenta; no mostrar nuevas notificaciones de esa cuenta ni permitir acceder a sus datos desde ellas; descartar las notificaciones de esa cuenta que estén en curso durante el cierre; no afectar las notificaciones de otros dispositivos. Los recordatorios locales programados del dispositivo actual también se cancelan al cerrar sesión.
- **P-ALE-004:** Tratamiento de archivados y otros estados, tareas pausadas y correcciones que reactiven avisos.
- **P-ALE-005:** Pérdida de acceso offline y privacidad de avisos hasta conocer la revocación; no prometer conocimiento instantáneo de cambios remotos. La cancelación local de recordatorios al cerrar sesión se rige por PROP-SES-007.

## 13. Búsqueda, filtros y ordenación — BUS

**Estado de las entradas V1-BUS: Aprobado para V1. Fuente: S13.**

- **V1-BUS-001:** Buscar solo en el hogar activo. El selector permite cambiar de hogar y las búsquedas nunca mezclan inventarios.
- **V1-BUS-002:** Buscar por nombre, marca, modelo, serie y categoría, con coincidencias parciales y sin distinguir mayúsculas/minúsculas. Ignorar diferencias de acentuación es una preferencia expresa; su regla exacta queda pendiente.
- **V1-BUS-003:** Filtrar por categoría, marca, fecha de compra, precio y estado del objeto.
- **V1-BUS-004:** Filtrar por estado de garantía, devolución, favoritos personales, archivados y mantenimiento pendiente.
- **V1-BUS-005:** Combinar búsqueda, filtros y ordenación; mostrar filtros activos y permitir eliminarlos individualmente o limpiar todos.
- **V1-BUS-006:** Ordenar por nombre, fecha de compra, precio y vencimiento de garantía; direcciones, criterios secundarios y predeterminados pendientes.
- **V1-BUS-007:** Filtrar y ordenar por precio en una moneda seleccionada; no comparar directamente importes de monedas distintas ni convertir divisas.
- **V1-BUS-008:** Favoritos muestra exclusivamente los objetos marcados por el usuario autenticado.
- **V1-BUS-009:** Definir la coincidencia y orden de objetos con varias garantías de fechas/estados distintos.
- **V1-BUS-010:** Buscar, filtrar y ordenar con datos locales, incluidos cambios pendientes. Si el inventario local está incompleto, indicarlo para no presentar resultados parciales como completos.
- **V1-BUS-011:** Distinguir hogar sin objetos de búsqueda sin coincidencias; en este último caso conservar texto y filtros para modificarlos.
- **V1-BUS-012:** Al entrar en un objeto y volver, mantener búsqueda, filtros, ordenación y desplazamiento del listado.
- **V1-BUS-013:** Asociar cada consulta a su hogar. Decidir después si conservar consultas independientes o restablecer al cambiar de hogar.
- **V1-BUS-014:** Incluir búsquedas recientes personales, conservadas localmente y eliminables. No es necesaria su sincronización entre dispositivos en V1.
- **V1-BUS-015:** Respetar pantallas de búsqueda, filtros, orden y vacíos de Figma Android/iOS. Integrar filtros faltantes siguiendo el sistema visual, sin rediseño arbitrario.
- **V1-BUS-016:** Responder con fluidez sobre el inventario local, sin consultar la red en cada interacción.

**Pendiente de especificación:**

- **P-BUS-001:** Reglas de combinación dentro de un filtro y entre filtros; tratamiento de valores desconocidos.
- **P-BUS-002:** Inclusión de archivados por defecto y relación con filtros de estado.
- **P-BUS-003:** Conservación de consultas al cambiar hogares y al reabrir la app; ámbito, conservación y borrado de búsquedas recientes.
- **P-BUS-004:** Orden por defecto, direcciones y desempates; garantías múltiples al filtrar y ordenar.
- **P-BUS-005:** Normalización de acentos y caracteres; conservar el matiz «preferiblemente» sin convertirlo en una regla técnica ya elegida.
- **P-BUS-006:** Criterios medibles de fluidez y presentación de cobertura local incompleta.

## 14. Estadísticas del inventario — EST

**Estado de las entradas V1-EST: Aprobado para V1. Fuente: S14.**

- **V1-EST-001:** Estadísticas exclusivas del hogar activo, consultables por todos sus miembros autorizados, sin mezclar hogares.
- **V1-EST-002:** Mostrar cantidad de objetos incluidos según los criterios que se definan para estados y archivados.
- **V1-EST-003:** Mostrar suma de precios de compra registrados como importe histórico, no como valor actual, reventa o depreciación.
- **V1-EST-004:** Selector de moneda para los importes económicos: sumar únicamente esa moneda, sin conversiones. Los recuentos generales permanecen independientes del selector.
- **V1-EST-005:** Mostrar número de objetos por categoría e importes históricos, dejando claro qué recuento es general y qué importe pertenece a la moneda elegida.
- **V1-EST-006:** Mostrar objetos adquiridos e importe registrado por año según la fecha de compra introducida.
- **V1-EST-007:** Incluir agrupación mensual contemplada en Figma iOS y mantener coherencia visual/funcional con Android. Usar fecha de compra y selector de moneda para importes.
- **V1-EST-008:** Mostrar marcas y categorías más habituales por cantidad de objetos, tratando coherentemente desconocidos/no informados.
- **V1-EST-009:** Mostrar distribución de garantías por estado y, separadamente, objetos con cobertura vigente. Cada garantía se cuenta individualmente; un objeto con varias activas solo cuenta una vez como cubierto.
- **V1-EST-010:** Distinguir precio desconocido y cero; indicar cuántos objetos tienen precio registrado. No asignar compras sin fecha a meses/años ficticios.
- **V1-EST-011:** Calcular con datos locales y cambios pendientes; advertir si el inventario está incompleto para no mostrar resultados parciales como definitivos.
- **V1-EST-012:** Recalcular al cambiar objetos, precios, fechas, categorías, marcas, estados o garantías. Evitar cifras independientes que queden desactualizadas.
- **V1-EST-013:** Aplicar criterios coherentes de inclusión en todos los gráficos; concretar vendido, regalado, perdido, roto, reciclado, archivado y devolución completada.
- **V1-EST-014:** Respetar pantallas/gráficos Android/iOS; identificar diferencias visuales intencionadas y diferencias a unificar funcionalmente. No añadir gráficos/pantallas sin justificación.
- **V1-EST-015:** Cálculos locales eficientes, sin consultas de red en cada apertura de estadísticas.
- **V1-EST-016:** Formatos regionales de moneda, fechas y cantidades. Los gráficos ofrecerán valores numéricos comprensibles y estados vacíos cuando no haya datos suficientes.

**Pendiente de especificación:**

- **P-EST-001:** Criterios iniciales y seleccionables de inclusión de estados, archivados y devoluciones completadas.
- **P-EST-002:** Denominadores/porcentajes, desconocidos y presentación de recuentos generales frente a importes por moneda.
- **P-EST-003:** Reglas de cobertura vigente, garantías próximas a vencer y desconocidas, sin duplicar objetos.
- **P-EST-004:** Selección de periodos anuales/mensuales, detalles de gráficos y criterios de rendimiento.

## 15. Sincronización y conflictos — SYN

**Estado de las entradas V1-SYN: Aprobado para V1. Fuente: S15.**

- **V1-SYN-001:** Persistir cambios en el dispositivo antes de sincronizar. Conservar pendientes tras cierre o reinicio.
- **V1-SYN-002:** Con conexión, sesión válida y permisos suficientes, intentar enviar pendientes y recuperar cambios remotos automáticamente mediante Firebase, respetando límites de segundo plano Android/iOS.
- **V1-SYN-003:** Incluir sincronización manual conforme a «Sincronizar ahora» de Figma.
- **V1-SYN-004:** Cada operación conserva hogar, objeto y registro de destino cuando corresponda. Cambiar de hogar no mezcla, pierde ni reasigna operaciones; las operaciones personales conservan su ámbito de usuario.
- **V1-SYN-005:** Favoritos y preferencias de recordatorios pertenecen al usuario y se sincronizan solo entre sus dispositivos autorizados.
- **V1-SYN-006:** Distinguir guardado local, pendiente, sincronizando, sincronizado, error y conflicto. Guardado local no equivale a visibilidad para otros miembros.
- **V1-SYN-007:** Combinar automáticamente cambios independientes compatibles; por ejemplo, nombre modificado y fotografía añadida.
- **V1-SYN-008:** Ante modificaciones incompatibles conservar las versiones necesarias y pedir resolución explícita. No imponer el último cambio solo por fecha si eso pierde información.
- **V1-SYN-009:** Tratar conjuntamente unidades lógicas, como importe y moneda, para no mezclar versiones en combinaciones incorrectas.
- **V1-SYN-010:** Identificar hogar/objeto/registro en conflicto, mostrar diferencias relevantes y permitir decidir la resolución. Mantener utilizables las partes que no dependan del conflicto.
- **V1-SYN-011:** Conservar fotografías y documentos pendientes hasta confirmar sincronización; los reintentos no duplican archivos ni eliminan originales prematuramente.
- **V1-SYN-012:** Reintentos idempotentes de creación de objetos, acontecimientos, documentos y realizaciones: no generan registros duplicados. Concretar detalles en SPEC/PLAN.
- **V1-SYN-013:** Al entrar en otro dispositivo, recuperar hogares autorizados y datos sincronizados. Explicar que los cambios que nunca llegaron al servidor no son recuperables automáticamente desde otro dispositivo.
- **V1-SYN-014:** Al detectar pérdida de pertenencia/permisos, impedir nuevas sincronizaciones no autorizadas, no mostrar el inventario afectado y no sincronizar sus pendientes. Informar al usuario y no borrar pendientes de forma automática y silenciosa; su tratamiento y posible recuperación quedan pendientes de especificación.
- **V1-SYN-015:** Definir políticas para eliminación simultánea con edición, realizaciones duplicadas, garantías/documentos concurrentes y cambios de permisos.
- **V1-SYN-016:** Respetar elementos de sincronización Figma de ambas plataformas. Documentar los estados/pantallas adicionales necesarios para conflictos y proponer su integración visual antes de implementar.
- **V1-SYN-017:** No borrar pendientes ni versiones en conflicto por un reintento fallido. Proporcionar recuperación y errores comprensibles.
- **V1-SYN-018:** Comprobar autorizaciones en el servidor. Tener datos u operaciones en caché no autoriza a modificar un hogar sin permisos.

**Pendiente de especificación:**

- **P-SYN-001:** Políticas concretas de compatibilidad, unidades lógicas, versiones conservadas y quién puede resolver cada conflicto.
- **P-SYN-002:** Eliminación frente a edición y conservación de trabajo afectado, incluidas referencias entre registros.
- **P-SYN-003:** Realizaciones offline duplicadas y conflictos de garantías, documentos y fotografía principal.
- **P-SYN-004:** Cómo detectar revocaciones aún no recibidas mientras se está offline, privacidad de notificaciones hasta conocerlas y tratamiento y posible recuperación de pendientes bloqueados. Tras confirmar la pérdida de acceso, se aplican V1-ROL-008, V1-HOG-010 y V1-SYN-014; los pendientes no se borran automáticamente ni se sincronizan.
- **P-SYN-005:** Recuperación ante errores, reintentos, operaciones interrumpidas, información parcial y confirmación separada de archivos.
- **P-SYN-006:** Integración visual para resolver conflictos, sincronizar manualmente y presentar estados en ambas plataformas.
- **P-SYN-007:** Algoritmos de idempotencia, transacciones, versiones remotas, reglas Firebase y migraciones pertenecen al PLAN tras concretar el comportamiento; no adoptarlos por el estado actual del outbox.

## 16. Registro, acceso y recuperación — AUT

**Estado de las entradas V1-AUT: Aprobado para V1. Fuente: S16.**

- **V1-AUT-001:** Cuenta autenticada obligatoria para pertenecer a hogares y acceder a sus inventarios.
- **V1-AUT-002:** Incluir correo/contraseña, Google y Apple en V1, mediante Firebase Authentication.
- **V1-AUT-003:** Registro por correo requiere nombre visible, correo y contraseña. El nombre visible es obligatorio; se eliminan los espacios al principio y al final, y se rechaza si queda vacío o compuesto únicamente por espacios o caracteres invisibles. Se aceptan nombres internacionales y Unicode, con acentos, espacios internos, guiones, apóstrofes y otros signos habituales; no se separa en nombre y apellidos ni se exige una longitud mínima superior a un carácter. El límite máximo aprobado es 100 caracteres. Para registro e inicio de sesión con contraseña, el correo es obligatorio y se eliminan sus espacios exteriores, pero no se cambia su capitalización ni se transforma de otra manera. La comprobación local es básica: detecta campo vacío, ausencia de `@` o falta de texto a uno u otro lado de `@`; no usa una expresión regular estricta ni reproduce todas las reglas de Firebase. Firebase hace la validación definitiva. Al registrar, la contraseña debe tener al menos 8 caracteres; no se exigen mayúsculas, números, símbolos ni otras reglas de composición. La contraseña no se recorta ni transforma. En el inicio de sesión solo se valida localmente que no esté vacía, sin rechazar credenciales por longitud. Firebase hace la validación definitiva y la política del proyecto exigirá 8 caracteres como mínimo en altas nuevas. No hay máximo funcional propio y se permite pegar desde gestores de contraseñas. Los errores se mostrarán clara y coherentemente en Android e iOS, sin depender directamente de los mensajes del SDK ni revelar innecesariamente si existe una cuenta; la clasificación técnica concreta queda para SPEC/PLAN. Durante el flujo activo se conservan temporalmente nombre y correo entre pantallas y el nombre del perfil al volver antes de guardarlo. La contraseña solo se conserva en el formulario activo, se descarta al salir de él, tras un registro/acceso exitoso o al cerrar sesión, y nunca se guarda como borrador persistente. Al abandonar/finalizar el flujo o terminar el proceso se descartan datos parciales no guardados (S29).
- **V1-AUT-004:** Permitir registro/acceso Google y Apple. Mantener opciones Figma por plataforma y estudiar ambos métodos en Android/iOS para garantizar continuidad entre dispositivos; no dar por resuelta su disponibilidad solo por aparecer un botón.
- **V1-AUT-005:** Permitir vincular distintos métodos a una misma identidad Firebase. No fusionar cuentas diferentes automáticamente por parecer de la misma persona; resolver credenciales ya utilizadas mediante proceso seguro.
- **V1-AUT-006:** Cambiar Android↔iOS conservando perfil, hogares, objetos y preferencias sincronizadas de la misma cuenta.
- **V1-AUT-007:** Cuentas de correo/contraseña verifican el correo mediante enlace enviado por email en ambas plataformas. Mientras no esté verificado, la persona puede mantener sesión, completar/editar su perfil, consultar el estado de verificación, pedir el reenvío y cerrar sesión; no puede crear hogares, aceptar invitaciones ni acceder a datos de hogares o inventario. La aplicación lo comunica claramente. Estas restricciones deben protegerse en las operaciones y el acceso a datos correspondientes, no solo mediante navegación o pantallas. Una vez confirmada la verificación, puede continuar sin registrarse de nuevo. Si Firebase acepta una solicitud de reenvío, confirmar que se solicitó sin garantizar la entrega. Si el enlace no es válido o caducó, explicar el problema y ofrecer pedir otro. Al volver a MisCosas tras abrir el enlace, actualizar el estado; si aún no está confirmado, mantener el acceso limitado y permitir una comprobación manual. Comunicar con claridad errores de conexión o límites de reenvío sin afirmar que se envió si Firebase rechazó la solicitud. Los límites concretos y la integración técnica quedan para SPEC/PLAN.
- **V1-AUT-008:** Recuperar contraseña mediante enlace por correo, mostrando el resultado real y permitiendo volver al acceso. Se aplica a cuentas que dispongan de contraseña, sin confundir recuperación con proveedores externos.
- **V1-AUT-009:** Al iniciar, distinguir comprobación en curso, sesión activa, sesión inexistente y error. No mostrar incorrectamente registro mientras se restaura sesión. Si Firebase confirmó previamente el correo y ese estado fiable se conservó asociado al mismo Firebase UID, se puede restaurar ese estado sin conexión. Si no hay una prueba local fiable, no presumir que está verificado. Al recuperar conectividad, actualizar el estado. Al reabrir tras abandonar el flujo o terminarse el proceso, recuperar solo la sesión autenticada y los datos de perfil guardados, no los borradores parciales (S29).
- **V1-AUT-010:** Si Firebase crea la cuenta pero falla el perfil, permitir completarlo sin registrar una segunda cuenta. Si el proceso terminó antes de guardar el nombre, volver a solicitarlo con la identidad autenticada existente. Si el correo de un registro completado sigue sin verificarse, conservar la cuenta y la sesión en su estado de verificación; abandonar el formulario no equivale a cerrar sesión (S29).
- **V1-AUT-011:** Si hay una sesión autenticada y no existe perfil local, consultar el perfil remoto correspondiente al Firebase UID. Si existe, recuperarlo y persistirlo localmente; si el servidor confirma que no existe, permitir completar el perfil con la identidad autenticada actual, sin crear otra cuenta. Si la consulta falla, conservar la sesión y permitir reintentar; no interpretar el error como ausencia ni crear perfiles duplicados por cambiar de dispositivo o por una consulta fallida. Si existe un perfil local válido pero falla la consulta remota, no descartarlo automáticamente; su uso offline debe respetar las reglas aprobadas de seguridad, sesión y sincronización.
- **V1-AUT-012:** Sin hogares, ofrecer crear uno o aceptar invitación; con varios, aplicar reglas de selección ya aprobadas.
- **V1-AUT-013:** Registro, nuevos inicios de sesión, vinculación y recuperación de contraseña requieren conexión. Tras cerrar sesión offline, se requiere conexión a Internet para volver a iniciar sesión, incluso con la misma cuenta. El acceso posterior al inventario local sigue las reglas offline de sesiones previamente establecidas. Si se restaura offline una verificación ya confirmada, ello no concede por sí solo acceso a hogares/inventario; dicho acceso sigue sujeto a permisos y disponibilidad local aprobados.
- **V1-AUT-014:** Usar mecanismos oficiales Firebase/proveedores; no almacenar contraseñas ni tokens sensibles inseguramente. El estado local de verificación debe estar asociado estrictamente al Firebase UID y no puede reutilizarse con otra cuenta. Al recuperar conexión, consultar el estado y los permisos que correspondan; el estado local no sustituye la autorización del servidor. El cierre local y el cambio de cuenta se rigen por SES; no eliminan la cuenta ni cierran sesiones de otros dispositivos.
- **V1-AUT-015:** Contemplar correo oculto de Apple y datos de perfil que solo se proporcionan en la primera autorización.
- **V1-AUT-016:** Respetar Figma Android/iOS y adaptar la verificación iOS al enlace aprobado. Definir visualmente vinculación y errores adicionales antes de implementar.
- **V1-AUT-017:** Mantener sesión, perfil y estados coherentes en ambas plataformas, con lógica compartible en KMP e integraciones nativas adecuadas.

**Pendiente de especificación:**

- **P-AUT-003:** Vinculación segura, credenciales en otra cuenta, posibles identidades duplicadas y recuperación de acceso sin fusión automática.
- **P-AUT-004:** Disponibilidad e integración de ambos proveedores en cada plataforma, datos iniciales de Apple y correo oculto; validar continuidad real.
- **P-AUT-006:** La conducta funcional de cierre local y cambio de cuenta está aprobada en SES. Siguen pendientes la política de almacenamiento, caducidad y limpieza de datos locales (P-SES-002), el tratamiento de pendientes tras revocación (P-SES-002, P-ROL-003 y P-SYN-004) y si la revocación de otras sesiones o la eliminación de cuenta se ofrecerán como funciones independientes (P-SES-003).

## Cierre de sesión y cambio de cuenta — SES

**Estado:** Las decisiones `PROP-SES-001..008` están aprobadas para V1 por Rafael (S19). Se conservan sus identificadores de origen para mantener la trazabilidad. Esta aprobación funcional no inicia una SPEC, un PLAN ni la implementación.

- **PROP-SES-001 — Aprobado para V1:** Cerrar sesión afecta únicamente al dispositivo actual. No elimina la cuenta ni sus datos, y no cierra las sesiones de otros dispositivos. Cambiar de cuenta consiste en cerrar la sesión actual e iniciar sesión con otra cuenta.
- **PROP-SES-002 — Aprobado para V1:** Si hay cambios, fotografías, documentos o conflictos pendientes, avisar antes de cerrar sesión y ofrecer cancelar, intentar sincronizar o cerrar conservando todos los pendientes asociados a la cuenta y al hogar originales. Solo se intentará sincronizar si hay conexión. Si quedan pendientes, errores o conflictos tras el intento, informar del resultado y esperar una decisión expresa; no cerrar sesión automáticamente. Nunca descartar pendientes de forma silenciosa. **Aplazamiento incremental S30:** la primera SPEC implementa aviso, cancelación y cierre conservando los pendientes, pero aplaza la opción de intentar sincronizar hasta el incremento que incorpore esa capacidad. Esta secuenciación no modifica el comportamiento global de V1.
- **PROP-SES-003 — Aprobado para V1:** Conservar los datos locales, documentos, preferencias y operaciones pendientes asociados a la identidad original, pero hacerlos inaccesibles mientras la sesión esté cerrada y desde cualquier otra cuenta.
- **PROP-SES-004 — Aprobado para V1:** Para recuperar esos datos se debe autenticar exactamente la misma identidad de Firebase, identificada por el mismo UID. La coincidencia de la dirección de correo no basta.
- **PROP-SES-005 — Aprobado para V1:** Antes de mostrar los datos de un hogar o sincronizar sus operaciones, comprobar que la identidad conserva permisos de acceso. Si los ha perdido, no mostrar el inventario ni sincronizar sus operaciones pendientes. La pérdida de permisos no debe borrar pendientes de forma automática y silenciosa; su tratamiento y posible recuperación siguen pendientes de especificación.
- **PROP-SES-006 — Aprobado para V1:** Al cerrar sesión, dejar de mostrar inmediatamente los datos del usuario, sus hogares y su estado de navegación. Mantener separados por identidad los favoritos, las preferencias personales, las búsquedas recientes y cualquier otro estado local del usuario.
- **PROP-SES-007 — Aprobado para V1:** Al cerrar sesión, cancelar en el dispositivo actual los recordatorios locales programados y retirar del centro de notificaciones las notificaciones ya entregadas que pertenezcan a esa cuenta. No mostrar nuevas notificaciones de esa cuenta después del cierre ni permitir acceder a sus datos desde ellas. Descartar las notificaciones de esa cuenta que estén en curso durante el cierre. No afectar las notificaciones de otros dispositivos. Al volver a iniciar sesión con la misma identidad, los recordatorios podrán recalcularse y programarse según las preferencias y permisos vigentes. La deduplicación general entre dispositivos sigue pendiente en P-ALE-003. **Aplazamiento incremental S30:** esta gestión se implementará con la funcionalidad de notificaciones; CA-30 de la primera SPEC conserva el requisito y no aplica a las pruebas de dicho incremento.
- **PROP-SES-008 — Aprobado para V1:** Permitir cerrar sesión sin conexión y sin intentar sincronizar. Antes de confirmar, informar de que se necesitará conexión a Internet para volver a iniciar sesión, incluso con la misma cuenta. No borrar automáticamente datos locales pendientes por cerrar sesión offline.

**Decisión de aprobación resuelta:**

- **P-AUT-001 (resuelto por S21–S24):** Se aprobaron los criterios funcionales de nombre visible, correo, contraseña y errores de registro/inicio de sesión, registrados en V1-AUT-003. La normalización Unicode y el cómputo exacto del límite de 100 caracteres del nombre se concretarán como detalles de especificación sin cambiar el comportamiento aprobado. La clasificación técnica de errores y su representación compartible se decidirán en SPEC/PLAN.
- **P-AUT-002 (resuelto por S25 y S27):** Se aprobó el acceso limitado hasta verificar y el flujo funcional de reenvío, enlaces no válidos/caducados y comprobación del estado al regresar. La vida útil concreta del enlace, límites del proveedor y detalles de integración quedan para SPEC/PLAN.
- **P-AUT-005 (resuelto por S26):** Se aprobó cómo recuperar o completar el perfil tras restaurar una sesión y cómo distinguir ausencia confirmada de error remoto. La integración concreta con el proveedor, persistencia local y estados de interfaz se desarrollará en SPEC/PLAN.
- **P-SES-001 (resuelto):** Rafael aprobó para V1 las decisiones funcionales de `PROP-SES-001..008` descritas arriba (S19); S20 concreta el tratamiento de notificaciones de PROP-SES-007. La primera SPEC incremental de autenticación/cierre local se aprobó formalmente después (S30); esto no aprueba un PLAN ni autoriza implementación.

**Pendiente de especificación:**

- **P-SES-002:** Definir la política técnica de almacenamiento y protección, la caducidad y limpieza de caché, y el tratamiento y posible recuperación de pendientes asociados a un hogar cuyo acceso se ha perdido. El comportamiento funcional de no mostrar esos datos, no sincronizarlos y no borrar pendientes silenciosamente ya está aprobado.
- **P-SES-003:** La revocación de sesiones en otros dispositivos y la eliminación de cuenta no forman parte del cierre local aprobado. No queda decidido aquí si se ofrecerán como funcionalidades independientes de V1.

## Exclusiones, aplazamientos y ampliaciones futuras

El alcance de cada exclusión importa: «fuera de este bloque por ahora» no equivale a «excluido definitivamente de V1». No ampliar estas exclusiones ni usar el brief histórico para reducir decisiones posteriores.

- **X-ROL-001 — Excluido de V1:** permisos individuales por objeto; se mantienen los dos roles y permisos de inventario iguales.
- **X-ITC-001 — Excluido de V1:** invitaciones vinculadas a direcciones de correo y sistema complejo de permisos sobre invitaciones.
- **X-COM-001 — Excluido de V1:** tipos de cambio, conversiones de precios y servicios externos de divisas.
- **X-COM-002 — Fuera del alcance vigente:** cálculo de valor actual del objeto, reventa o depreciación; la extensibilidad futura no autoriza estas funciones.
- **X-GAR-001 — Excluido de V1:** presuponer una duración legal automática. Reglas por país se contemplan como posible ampliación posterior.
- **X-GAR-002 — Pospuesto:** reclamaciones, reparaciones cubiertas y condiciones especiales de garantías; mantener extensibilidad.
- **X-DEV-001 — Excluido de V1:** cálculos legales automáticos del plazo, integraciones con vendedores, etiquetas, solicitudes automáticas, seguimiento de transportistas y procesamiento de reembolsos.
- **X-DOC-001 — Excluido de V1:** OCR y desarrollo de reconocimiento documental propio. Escaneado nativo sí está aprobado.
- **X-DOC-002 — Pospuesto:** ampliar las asociaciones documentales a otras funciones como devolución o mantenimiento; está aprobada la extensibilidad, no todos esos flujos.
- **X-ALE-001 — Fuera del bloque por ahora:** resumen semanal por email y avisos de actualizaciones de software. No aprobados automáticamente por Figma; revisión separada si corresponde, sin declararlos una exclusión definitiva de V1.
- **X-BUS-001 — Fuera de V1 acordada:** búsqueda global entre hogares y buscador remoto independiente.
- **X-BUS-002 — No requerido en V1:** sincronización de búsquedas recientes; su conservación local sí está aprobada.
- **X-SYN-001 — Fuera de este bloque / no aprobado:** copias de seguridad independientes iCloud. Son distintas de la sincronización compartida Firebase y no quedan aprobadas por el diseño iOS.
- **X-GEN-001 — Pospuesto según brief:** exportación de inventario/informes PDF y CSV. No confundirla con compartir/exportar un documento individual, que sí está en V1.
- **X-GEN-002 — Posibilidad futura según brief:** categorías personalizadas; no están aprobadas para V1.

## Correspondencia con Figma

Fecha de comprobación: 22 de octubre de 2026. Agente: UI Designer (`/root/figma_requirements_audit`).

Revisión en SOLO LECTURA con `figma_get_metadata` del archivo `Mej8DX4EZrTL98FRWyV7Jo`, páginas `0:1` «Android - Light Mode» y `10:27400` «iOS - Light Mode». Se han contrastado la existencia de nodos, nombres y textos expuestos por metadatos; algunos nombres de capa están truncados. No es inspección visual por capturas, prueba de prototipo, revisión de accesibilidad ni validación de implementación. La comprobación de Figma fue solo lectura; el coordinador incorporó después esta evidencia al documento.

La existencia de una pantalla no demuestra soporte completo de requisitos ni su aprobación funcional. «No identificado» se limita a las páginas y metadatos consultados; la inspección visual y de conexiones deberá completarse en cada SPEC.

Tabla de 17 áreas (17 bloques con comportamiento funcional aprobado); 76 nodos únicos en la tabla y 20 complementarios: 96 nodos comprobados. Las correspondencias son parciales; verificar un nodo no demuestra cobertura integral. El selector multihogar no tiene un flujo específico identificado.

| Bloque | Android: nombres e IDs comprobados | iOS: nombres e IDs comprobados | Diferencias, alcance y pendientes |
| --- | --- | --- | --- |
| Inventario y estados | [02 · Android Add / Essentials (10:28587)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=10-28587); [03 · Product Summary / Change status (19:7085)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=19-7085); [05 · Product / Edit details (22:191)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=22-191) | [02 · iOS Add / Essentials (51:80)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=51-80); [03 · iOS / Change status sheet (19:13116)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=19-13116); [05 · iOS / Edit product (19:13351)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=19-13351) | Alta, edición y siete estados identificados. El texto de alta podría sugerir que se necesita fotografía: revisarlo para expresar que es opcional, como establece el requisito aprobado. La privacidad de favoritos por usuario y los permisos compartidos no se demuestran con estas pantallas. |
| Hogares, miembros y administración | [Crear hogar (6:24119)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=6-24119); [26 · Android / Member detail (30:713)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=30-713); [29 · Android / Transfer administration (30:898)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=30-898) | [11 · iOS / Create home (39:317)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=39-317); [26 · iOS / Member detail (38:2594)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=38-2594); [29 · iOS / Transfer administration (38:2715)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=38-2715) | Crear hogar, miembro y transferencia identificados. En iOS la transferencia se describe como irreversible y el administrador anterior pasa a miembro: esas consecuencias no están aprobadas y deben concretarse con múltiples administradores. Casos último administrador, cierre de hogar y permisos offline pendientes de correspondencia. |
| Invitaciones | [Unirse a un hogar (6:24163)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=6-24163); [23 · Android / Invite member (30:560)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=30-560); [24 · Android / Sent invitations (30:612)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=30-612) | [12 · iOS / Join home (39:355)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=39-355); [23 · iOS / Invite member (38:2439)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=38-2439); [24 · iOS / Sent invitations (38:2507)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=38-2507) | Código y siete días aparecen; Android muestra vista previa antes de unirse. iOS contiene envío por email y escaneo QR, que no quedan aprobados por aparecer en Figma. Las listas usan emails. No se verifica el flujo completo de un solo uso, consumo atómico, revocación durante confirmación y usuario ya miembro. |
| Multihogar | [22 · Android / Home overview (30:435)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=30-435) | [22 · iOS / Home overview (38:2305)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=38-2305) | Son vistas de un hogar, no prueba de selector multihogar. No se ha identificado un flujo específico de selector, recuerdo del hogar activo, roles independientes o pérdida de acceso. Correspondencia de esos requisitos: PENDIENTE; los inventarios deben permanecer separados. |
| Fotografías | [04 · Android Add / Photo source (55:160)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=55-160); [28 · Product / Photos (22:3425)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=22-3425) | [04 · iOS Add / Photo source (51:1011)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=51-1011); [28 · iOS / Product gallery (19:16146)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=19-16146) | Fuentes cámara/fotos/archivos y galerías identificadas. Principal y elección determinista de sustituta, subidas duraderas, disponibilidad local frente a sincronización, reintentos y conflictos no quedan especificados en estos nodos. |
| Compras y multimoneda | [07 · Purchase / Details (22:437)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=22-437); [08 · Purchase / Edit (22:599)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=22-599) | [07 · iOS / Purchase details (19:13570)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=19-13570); [08 · iOS / Edit purchase (19:13733)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=19-13733) | Detalle y edición, precio, moneda, fecha y tienda identificados. Edición en ambas plataformas tiene forma de pago (pendiente); detalle iOS además número de pedido (sin aprobación identificada). No se verifica catálogo ISO completo, precisión por moneda, desconocido frente a cero ni fechas futuras. |
| Garantías | [09 · Warranty / Active (22:721)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=22-721); [10 · Warranty / Edit (22:896)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=22-896); [13 · Warranty / Add extended (22:1247)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=22-1247) | [09 · iOS / Warranty active (19:13854)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=19-13854); [10 · iOS / Edit warranty (19:14007)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=19-14007); [13 · iOS / Add extended warranty (19:14420)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=19-14420) | Activa, edición y ampliación identificadas. También hay vistas próximas a vencer/vencidas. No demuestra gestión de todas las garantías independientes ni varios documentos por garantía; el editor presenta un documento. Opciones de aviso ilustradas no aprueban umbrales. Pendientes inicio futuro, información insuficiente y casos de eliminación. |
| Devoluciones | [07 · Purchase / Details (22:437)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=22-437); [08 · Purchase / Edit (22:599)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=22-599) | [07 · iOS / Purchase details (19:13570)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=19-13570); [08 · iOS / Edit purchase (19:13733)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=19-13733) | Plazo de devolución representado dentro de compra. No se ha identificado pantalla del seguimiento manual con los cuatro estados aprobados, ni sus transiciones o consecuencias sobre el inventario. Correspondencia parcial: no equivale a flujo de devolución completo. |
| Documentos | [14 · Documents / List (22:1382)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=22-1382); [15 · Documents / Add source (56:2)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=56-2); [18 · Documents / Actions (22:2006)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=22-2006) | [14 · iOS / Documents (19:14553)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=19-14553); [15 · iOS / Add document source sheet (19:14697)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=19-14697); [18 · iOS / Document actions sheet (19:14932)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=19-14932) | Listado, fuentes (incluido escanear), abrir/renombrar/compartir/reemplazar/eliminar identificados. Pendientes asociación múltiple, desvincular frente a eliminar, advertencia al compartir, reemplazo con error/conflicto y estados offline. Escanear documento no implica OCR. |
| Mantenimiento | [21 · Maintenance / Tasks (22:2341)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=22-2341); [24 · Maintenance / New task (22:2804)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=22-2804); [25 · Maintenance / Edit task (22:2955)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=22-2955) | [21 · iOS / Maintenance (19:15213)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=19-15213); [24 · iOS / Add maintenance task (19:15611)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=19-15611); [25 · iOS / Edit maintenance task (19:15741)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=19-15741) | Listado y formularios de tarea identificados. No se identifica selector de las dos modalidades aprobadas ni tareas puntuales, pausa/reanudación, corrección de realización y duplicados concurrentes. La antelación ilustrada no es una regla aprobada. |
| Historial | [27 · Product / History (22:3253)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=22-3253) | [27 · iOS / Product history (19:15997)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=19-15997) | Cronologías verificadas. No se identifica alta/corrección de acontecimiento manual ni distinción explícita entre fecha real y registro. iOS muestra «Información editada / Actualizado número de serie»: no adoptar como obligación de registrar cada pequeña edición; aplica cronología útil aprobada. |
| Alertas y recordatorios | [01 · Android / Alerts list (30:5)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=30-5); [03 · Android / Alert detail (30:244)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=30-244); [04 · Android / Alert settings (30:316)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=30-316) | [Screen (38:3)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=38-3); [Screen (38:178)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=38-178); [Screen (38:270)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=38-270) | Listas, detalle y configuración identificados; los tres frames iOS se llaman exactamente «Screen». Resumen semanal por email y actualización de software figuran pero no están aprobados. Tampoco se aprueban por estos diseños contactar servicio técnico, novedades de reparaciones o avisos de actividad. Silenciar/descartar, alcance personal, persistencia multidispositivo, revocación y navegación entre hogares pendientes de correspondencia completa. |
| Búsqueda, filtros y ordenación | [Busqueda (5:22513)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=5-22513); [Inventario: Filtros (5:22520)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=5-22520); [Inventario ordenacion (5:22511)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=5-22511) | [03 · iOS Home / Search (40:352)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=40-352); [10 · iOS Objects / Filters sheet (40:1015)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=40-1015); [12 · iOS Objects / Sort sheet (40:1174)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=40-1174) | Búsqueda con recientes, filtros y orden identificados. Cobertura incompleta de marca, fechas, precio/moneda, devolución y estado del objeto; iOS no muestra mantenimiento en su filtro. «30 días» en filtro iOS no fija el umbral de garantía. Estado por hogar, parcialidad local y borrado de recientes pendientes. |
| Estadísticas | [06 · Android / Statistics overview (35:175)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=35-175); [08 · Android / Statistics by year (35:479)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=35-479); [09 · Android / Statistics brands and warranties (35:557)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=35-557) | [06 · iOS / Statistics overview (38:531)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=38-531); [08 · iOS / Statistics by year (38:851)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=38-851); [09 · iOS / Statistics by brand (38:986)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=38-986) | Resumen, año y marcas/garantías verificados. iOS muestra gasto mensual; su coherencia funcional con Android está aprobada. Etiquetas «valor total» deben interpretarse como precio histórico, no valoración actual. Pendientes selector de moneda, recuentos independientes y distinción garantías/objetos cubiertos, desconocidos e inventario parcial. |
| Sincronización | [19 · Android / Storage and data (35:1366)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=35-1366) | [19 · iOS / Data management (38:2061)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=38-2061) | Android incluye «Sincronizar ahora»; iOS muestra copia/restauración iCloud, que no está aprobada y no equivale a sincronización compartida Firebase. No se identifica flujo de resolución de conflictos, sus estados ni la recuperación de operaciones pendientes. |
| Autenticación | [Login (6:23685)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=6-23685); [Register (6:23743)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=6-23743); [Verificar correo v2 (6:23947)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=6-23947) | [03 · iOS / Sign in (39:41)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=39-41); [04 · iOS / Create account (39:92)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=39-92); [07 · iOS / Verify email (39:209)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=39-209) | Android muestra Google y verificación por enlace; iOS Apple y código. El acuerdo exige correo+Google+Apple, verificación por enlace, errores coherentes sin filtrar cuentas y un estado limitado mientras no se verifica. Adaptar iOS y estudiar continuidad de proveedores; los nodos no verifican todos los estados de error ni el bloqueo de acceso a hogares/inventario. Vinculación, credenciales ocupadas, perfil remoto pendiente, recuperación de perfil y restauración de sesión requieren correspondencia adicional. |
| Cierre de sesión y cambio de cuenta — comportamiento aprobado; detalles pendientes | [21 · Android / Log out confirmation (35:1528)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=35-1528) | [21 · iOS / Log out confirmation (38:2286)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=38-2286) | Metadatos consultados directamente: ambas pantallas muestran confirmación/cancelación, pero no opciones para cambios pendientes ni cierre offline. El texto Android que afirma que los datos están guardados en la nube puede ser engañoso si hay cambios aún locales. El diseño deberá reflejar el aviso y las opciones aprobadas. La limpieza de notificaciones al cerrar sesión está aprobada (S20/PROP-SES-007); la deduplicación general entre dispositivos sigue pendiente (P-ALE-003). |

Referencias complementarias comprobadas:

- [25 · Android / Invitation received (30:670)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=30-670): Invitación recibida y confirmación del hogar.
- [25 · iOS / Invitation received (38:2567)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=38-2567): Invitación recibida y confirmación del hogar.
- [22 · Maintenance / Complete task (22:2529)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=22-2529): Realización de mantenimiento.
- [22 · iOS / Complete maintenance (19:15355)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=19-15355): Realización de mantenimiento.
- [19 · Documents / Delete confirmation (22:2200)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=22-2200): Eliminar documento.
- [19 · iOS / Delete document alert (19:14983)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=19-14983): Eliminar documento.
- [Restaurar contraseña (6:23857)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=6-23857): Recuperación de contraseña.
- [05 · iOS / Recover access (39:145)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=39-145): Recuperación de acceso.
- [Instrucciones recuperacion (6:23903)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=6-23903): Instrucciones tras solicitar recuperación.
- [06 · iOS / Recovery email sent (39:177)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=39-177): Correo de recuperación enviado.
- [07 · Android / Statistics by category (35:360)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=35-360): Estadísticas de categoría.
- [07 · iOS / Statistics by category (38:708)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=38-708): Estadísticas de categoría.
- [15 · Android / Notification settings (35:1030)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=35-1030): Ajustes de notificaciones: también contiene novedades/actividad/resumen no aprobados automáticamente.
- [15 · iOS / Notification settings (38:1608)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=38-1608): Ajustes de notificaciones: también contiene avisos SAT/nuevos objetos/resumen no aprobados automáticamente.
- [01 · Android Add / Method (10:28508)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=10-28508): Método de alta: OCR, catálogo por código de barras e IA aparecen como Próximamente, sin aprobación V1.
- [01 · iOS Add / Method (53:2)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=53-2): Método de alta: mismas opciones futuras; no confundir escaneo documental aprobado con extracción automática.
- [20 · Documents / Search manual online (22:1896)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=22-1896): Buscar manual online, sin aprobación identificada.
- [20 · iOS / Search manual online (19:15119)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=19-15119): Buscar manual online, sin aprobación identificada.
- [10 · Android / Export inventory (35:670)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=35-670): Exportar inventario: pospuesto en el brief; distinto de compartir un documento aprobado V1.
- [10 · iOS / Export inventory (38:1129)](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=38-1129): Exportar inventario: no incorporarlo automáticamente a V1.

Pendientes transversales: revisar cada flujo visual en la SPEC correspondiente, mantener diseños nativos, proponer integración de comportamientos aprobados ausentes antes de implementar, confirmar diferencias intencionadas entre plataformas, no deducir límites/umbrales/seguridad de textos ilustrativos y mantener MisCosas como nombre final aunque haya capas históricas con «Cosas».

## Ambigüedades y discrepancias que no se han resuelto por suposición

- **P-CRU-001 (resuelto por S26; brecha de implementación):** La regla histórica de pedir nombre solo se aplica después de comprobar que el perfil remoto no existe. La implementación actual todavía no hace esa comprobación; no es una decisión funcional pendiente.
- **P-CRU-002:** Privacidad tras revocación y notificaciones offline: preservar la exigencia de seguridad, pero especificar el intervalo en que el dispositivo aún desconoce cambios de permisos. No prometer revocación instantánea sin red.
- **P-CRU-003:** Conservación hasta subir frente a eliminación voluntaria de archivos pendientes: falta decidir la excepción o flujo de cancelación, sin borrar silenciosamente.
- **P-CRU-004:** Los siete estados, archivado separado actual y una devolución completada no forman aún una regla única. Falta acordar cómo afectan inventario, recordatorios y estadísticas.
- **P-CRU-005:** «Ambos proveedores en ambas plataformas» requiere estudio explícito; los tres métodos sí están aprobados, pero su presentación/integración exacta por plataforma no se da por decidida.
- **P-CRU-006:** Aprobación funcional frente a diseño: umbrales, límites, datos de ejemplo, botones y acciones adicionales de Figma no constituyen reglas aprobadas. Mantener las diferencias en la matriz y resolverlas antes de implementar.

## Aspectos para las futuras SPEC y PLAN

Esta lista identifica trabajo derivado; no decide su implementación ni crea tareas ejecutables.

| Área | Diferencia o aspecto a desarrollar después | Referencias |
| --- | --- | --- |
| Favoritos y autoría | El Item actual guarda un favorito global; adaptar identidad por usuario y autoría básica. | V1-INV-004, V1-ROL-009, V1-ROL-010 |
| Hogares y permisos | OWNER/MEMBER actuales no demuestran administración múltiple, invitaciones, selector ni revocación. | ROL, ITC, HOG |
| Compras | Validar catálogo/precisión por moneda, entrada y formato regional; no introducir conversiones. | COM, BUS, EST |
| Garantías/documentos | Estado pendiente de inicio, documentos múltiples por garantía y asociaciones reutilizables; conservación y eliminaciones. Conservar la preferencia del brief de evitar archivos grandes como blobs en la base de datos cuando exista una estrategia mejor; concretar almacenamiento de archivos y metadatos en el PLAN. | GAR, DOC, S00 |
| Devoluciones | El modelo actual exige fecha y no tiene todos los estados de seguimiento aprobados. | DEV |
| Mantenimiento | Incorporar modalidad, tareas puntuales y miembro que registra; resolver correcciones/fechas antes de diseñar cálculos. | MAN |
| Historial | Separar fecha real y registro, vincular orígenes, generar eventos y asegurar idempotencia/conservación. | HIS |
| Alertas | Separar estado calculado, preferencias personales y programación nativa por dispositivo. | ALE |
| Consulta y estadísticas | Definir filtros/agrupaciones, cobertura local, monedas, garantías múltiples, accesibilidad y rendimiento observable. | BUS, EST |
| Sincronización | El outbox existente no implementa envío/descarga, conflictos ni permisos. Diseñar protocolo, versiones, archivos, migraciones y recuperación tras aprobar reglas. | SYN |
| Autenticación | Completar integración nativa, vinculación y estados; recuperar perfil remoto, validar cancelación e interoperabilidad Swift. | AUT |
| Fechas e identidades | Estrategia compartida de IDs, fechas civiles, instantes UTC y zonas; seleccionar campos de sincronización por necesidad, no añadirlos todos automáticamente. | V1-GEN-007, SYN |
| Cierre y cambio de cuenta | Comportamiento funcional aprobado; concretar los pendientes aplicables antes de SPEC/PLAN. | PROP-SES, P-SES |

Las SPEC deben preservar todos estos requisitos y resolver los pendientes que afecten a su comportamiento antes de marcarse Aprobadas. El PLAN desarrollará la solución técnica y su validación; los tests existentes no prueban automáticamente el alcance nuevo.

## Punto de reanudación funcional

La consolidación documental fue revisada y confirmada en el commit `7b0891e` (`docs: consolidate MisCosas V1 product requirements`). Posteriormente Rafael aprobó funcionalmente `PROP-SES-001..008` (S19), concretó su política de notificaciones al cerrar sesión (S20), aprobó las decisiones de autenticación para el primer incremento (S21–S29) y delimitó el cierre incremental de la primera SPEC (S30). La primera SPEC [SPEC.md](features/auth-email-profile-session/SPEC.md), su [PLAN](features/auth-email-profile-session/PLAN.md) y sus [TASKS](features/auth-email-profile-session/TASKS.md) están aprobados formalmente. El intento de sincronizar desde el cierre y la limpieza de notificaciones permanecen en el alcance global de V1, aplazados a sus incrementos correspondientes. Recuento actual: 217 entradas V1 aprobadas, 80 cuestiones P- abiertas, 8 entradas PROP-SES aprobadas y 15 exclusiones/aplazamientos X-. AUTH-T01 y AUTH-T02 están completadas; el estado de implementación y sus evidencias se mantienen en `DEVELOPMENT_STATUS.md`.
