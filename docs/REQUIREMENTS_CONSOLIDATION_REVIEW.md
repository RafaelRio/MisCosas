# MisCosas — Revisión de la consolidación funcional

**Fecha:** 22 de octubre de 2026.
**Alcance autorizado:** consolidación documental; sin implementar funcionalidades, modificar código de aplicación ni realizar stage, commits o push.
**Revisión de Rafael:** pendiente. La revisión técnica de fidelidad no sustituye su aprobación de decisiones aún abiertas.

## Documentos y fuentes

La fuente principal es [PRODUCT_REQUIREMENTS_V1.md](PRODUCT_REQUIREMENTS_V1.md). Registra los mensajes de aprobación de Rafael mediante las fuentes S01–S16, la visión original S00, la propuesta no aprobada S17 y la autorización documental S18. El mensaje repetido de mantenimiento se registra una sola vez. Los IDs conservan requisitos, restricciones y condiciones; no son una cuenta de frases de la conversación.

| Archivo | Trabajo de esta consolidación |
| --- | --- |
| [PRODUCT_REQUIREMENTS_V1.md](PRODUCT_REQUIREMENTS_V1.md) | Nuevo: alcance, condiciones detalladas, estados, pendientes, exclusiones, matriz Figma y aspectos para futuras SPEC/PLAN. |
| [REQUIREMENTS_CONSOLIDATION_REVIEW.md](REQUIREMENTS_CONSOLIDATION_REVIEW.md) | Nuevo: evidencia de revisión y límites de lo comprobado. |
| [DEVELOPMENT_STATUS.md](DEVELOPMENT_STATUS.md) | Actualizado: índice de bloques, reanudación exacta y diferencias con código; conserva el historial técnico y sus validaciones anteriores. |
| [CONTINUATION_PROMPT.md](CONTINUATION_PROMPT.md) | Actualizado: lectura obligatoria de requisitos, estados y Git sin ampliar permisos. |
| [AGENTS.md](../AGENTS.md) | Actualizado sobre el contenido previo: lectura obligatoria, fuentes de verdad y trazabilidad de RF/CA con IDs de producto. |
| [README.md](../README.md) | Actualizado: persistencia ya existente, estado funcional actual, enlaces y versiones comprobadas en los scripts del proyecto. |

Se revisaron y conservaron sin cambios [PRODUCT_BRIEF_ORIGINAL.md](PRODUCT_BRIEF_ORIGINAL.md), [SPEC_TEMPLATE.md](SPEC_TEMPLATE.md), [PLAN_TEMPLATE.md](PLAN_TEMPLATE.md), [GENERIC_RULES.md](GENERIC_RULES.md) y [MOBILE_GUIDELINES.md](MOBILE_GUIDELINES.md). No se borró historia ni se copiaron los requisitos en las plantillas. Su procedencia del curso y la limitación del cotejo con GitHub siguen registradas en DEVELOPMENT_STATUS; esta tarea no afirma una nueva comparación con la última versión del curso.

## Recuento comprobado

| Estado | Entradas con ID |
| --- | ---: |
| Aprobado para V1 — V1- | 217 |
| Pendiente de especificación — P- | 85 |
| Propuesta todavía no aprobada — PROP- | 8 |
| Exclusión, aplazamiento o límite de alcance — X- | 15 |

Los 217 requisitos incluyen 8 transversales y 209 distribuidos entre los 16 bloques. Los 85 pendientes incluyen 3 sobre el bloque de cierre/cambio de cuenta y 6 discrepancias transversales; pueden contener varias preguntas relacionadas. Las 8 propuestas no se suman a los requisitos aprobados. Las 15 entradas X- no son todas exclusiones definitivas de V1: se conserva el alcance temporal de cada decisión.

| Fuente / bloque | Aprobados | Pendientes propios |
| --- | ---: | ---: |
| S00 — Transversales GEN | 8 | 3 |
| S01 — Inventario INV | 9 | 3 |
| S02 — Roles y permisos ROL | 11 | 4 |
| S03 — Invitaciones ITC | 9 | 3 |
| S04 — Multihogar HOG | 12 | 3 |
| S05 — Fotografías FOT | 14 | 4 |
| S06 — Compras y multimoneda COM | 11 | 4 |
| S07 — Garantías GAR | 11 | 4 |
| S08 — Devoluciones DEV | 11 | 4 |
| S09 — Documentos DOC | 16 | 5 |
| S10 — Mantenimiento MAN | 13 | 7 |
| S11 — Historial HIS | 12 | 4 |
| S12 — Alertas y recordatorios ALE | 13 | 5 |
| S13 — Búsqueda, filtros y orden BUS | 16 | 6 |
| S14 — Estadísticas EST | 16 | 4 |
| S15 — Sincronización y conflictos SYN | 18 | 7 |
| S16 — Autenticación AUT | 17 | 6 |
| S17 — Cierre/cambio de cuenta SES | 0; 8 propuestas | 3 |
| Discrepancias transversales CRU | 0 | 6 |

## Revisión independiente

Se activó realmente el agente personalizado **Mobile App Builder**, tarea `/root/v1_consolidation_review`, para una revisión de solo lectura. Preparó una lista de contraste desde los mensajes originales antes de revisar el documento. Confirmó disponer en su contexto de las condiciones completas de los 16 bloques y del texto original de la propuesta de cierre/cambio de cuenta; retransmitió los ocho puntos de esta última al coordinador para comprobar su procedencia.

Su primer contraste completo no encontró requisitos aprobados omitidos, alterados sustancialmente ni clasificados incorrectamente. Comprobó las condiciones numeradas, los seis casos especiales de mantenimiento, los pendientes finales de cada bloque y los matices de compatibilidad, permisos del sistema y exclusiones temporales. Confirmó los recuentos anteriores y la separación de PROP-SES.

Observaciones y tratamiento:

- Sugirió reforzar la independencia de fecha, precio y vendedor. Se conserva la redacción aprobada de opcionalidad sin introducir nuevas dependencias o validaciones; las reglas detalladas se resolverán en la SPEC.
- Sugirió conservar la preferencia histórica de evitar archivos grandes como blobs en la base de datos cuando exista una estrategia mejor. Se incorporó en los aspectos futuros del PLAN, sin imponer una solución nueva.
- Se aclaró que PROP-SES es una formulación estructurada de la propuesta original, cotejada por el revisor, y no un requisito aprobado ni una cita literal.

La comprobación final de coherencia documental la realizó otro agente Mobile App Builder, tarea `/root/final_docs_check`, con GPT-6 Luna a petición de Rafael de reducir el consumo. No detectó incoherencias sustantivas en los estados de aprobación, el punto de reanudación ni la preservación del historial técnico. Esta lectura final no repite el cotejo de mensajes originales realizado por el primer revisor ni las llamadas a Figma. La revisión de la matriz y los recuentos se apoya en la evidencia de UI Designer; no equivale a validar visualmente las pantallas.

## Figma: comprobado y pendiente

Se activó realmente **UI Designer**, tarea `/root/figma_requirements_audit`. Consultó en solo lectura los metadatos del [archivo de MisCosas](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=0-1), páginas Android `0:1` e iOS `10:27400`. Los enlaces concretos están en la matriz de PRODUCT_REQUIREMENTS_V1; se evita mantener una segunda copia.

- **96 nodos reales**: 76 únicos en las 17 filas de la matriz y 20 complementarios. Hay referencias Android e iOS para los 16 bloques y la propuesta de cierre.
- Se comprobaron existencia, nombres y textos disponibles mediante metadatos. La cobertura es parcial; no se realizaron capturas ni pruebas del prototipo, de accesibilidad o de la aplicación.
- No se identificaron flujos específicos de selector multihogar, resolución de conflictos y vinculación de proveedores en los metadatos examinados. No se afirma que estén ausentes de todo el diseño; completar revisión visual y de navegación en sus SPEC.
- Hay diferencias explícitas: verificación de correo por código en iOS frente al enlace aprobado; invitaciones por email/QR no aprobadas; transferencia con consecuencias no acordadas; iCloud no aprobado; campos, filtros y estados offline aún sin correspondencia completa.
- La distribución mensual de estadísticas de iOS sí está aprobada. Resumen por email, avisos de software, búsqueda de manuales online, IA/códigos de barras y otros extras del diseño no se incorporan automáticamente al alcance.

El diseño no resuelve por sí mismo umbrales, límites, conflictos, permisos o almacenamiento. La matriz separa presencia comprobada, requisito aprobado y correspondencia pendiente.

## Lagunas y discrepancias conservadas

No se resuelven mediante suposiciones. Sus detalles siguen en P- del documento principal:

1. **Perfil remoto frente a ausencia local:** `GetSessionStateUseCase` puede devolver `ProfilePending` por ausencia local. V1-AUT-011 exige comprobar el estado remoto antes de concluir registro incompleto en un dispositivo nuevo. El comportamiento actual y sus tests no cubren todavía ese flujo final.
2. **Revocación durante uso offline:** hay que concretar cómo se detecta la pérdida de acceso y cómo se tratan datos, pendientes y notificaciones. No se promete conocimiento instantáneo de cambios remotos sin conexión.
3. **Conservación frente a eliminación voluntaria:** falta definir qué ocurre al eliminar un archivo todavía pendiente de subida, así como reemplazos y eliminación de asociaciones.
4. **Estados, archivado y devoluciones completadas:** falta acordar su relación y efectos sobre inventario, alertas y estadísticas. Se conservan los siete estados aprobados.
5. **Integración de proveedores:** correo/contraseña, Google y Apple están aprobados; disponibilidad y presentación exacta en cada plataforma necesitan el estudio acordado, sin dar por validada ninguna integración.
6. **Figma frente a decisiones funcionales:** los comportamientos añadidos o ausentes del diseño deberán concretarse antes de implementar. No tomar textos ilustrativos como reglas definitivas.

Persisten además los pendientes propios de cada bloque: límites, caché, fechas especiales, duplicados, transiciones, destinatarios y repeticiones, moneda seleccionada, inclusión en estadísticas, recuperación y resolución de conflictos. No se han reducido a una lista genérica que sustituya sus condiciones detalladas.

## Comprobaciones documentales y preservación

Se comparó el directorio de trabajo contra una instantánea de 229 archivos existentes tomada al reanudar esta consolidación, antes de las ediciones. Se conservó además la diferencia binaria completa del índice de Git para detectar cualquier cambio accidental.

- Conteo de definiciones V1-/P-/PROP-/X- mediante sus encabezados de entrada: sin IDs duplicados ni referencias numéricas a IDs inexistentes.
- `git diff --check`: sin errores en los cambios seguidos por Git. La comprobación final de los seis documentos creados/modificados verificó 47 enlaces locales existentes y ausencia de espacios finales, incluidos los archivos nuevos sin seguimiento.
- Solo se modificaron cuatro archivos preexistentes: AGENTS, README, CONTINUATION_PROMPT y DEVELOPMENT_STATUS. Se añadieron los dos documentos de esta consolidación. No se eliminaron archivos.
- Código, tests, configuraciones, brief, plantillas, guías y perfiles de agentes conservan sus bytes respecto de la instantánea. No se ejecutaron builds ni tests: no corresponden a este cambio documental.
- El índice de Git permanece idéntico al inicial. No se realizó stage, commit ni push. Los resultados de pruebas descritos en DEVELOPMENT_STATUS siguen siendo históricos o comunicados por Rafael, no nuevas ejecuciones.
- No se incorporaron credenciales ni archivos de configuración privados. Los enlaces Figma contienen identificadores del diseño proporcionado por Rafael, no credenciales de acceso. Esta comprobación no constituye una auditoría de secretos del repositorio completo.

## Estado de Git y futuro commit

Estado al cerrar esta consolidación; debe comprobarse otra vez antes de preparar un commit:

| Situación | Archivos |
| --- | --- |
| Añadido al índice, con cambios posteriores en el directorio de trabajo (`AM`) | AGENTS.md y los tres archivos Kotlin del puente/adaptador de autenticación. |
| Modificados, sin preparar (` M`) | README.md, docs/CONTINUATION_PROMPT.md, docs/DEVELOPMENT_STATUS.md. |
| Nuevos de esta consolidación, sin seguimiento (`??`) | docs/PRODUCT_REQUIREMENTS_V1.md, docs/REQUIREMENTS_CONSOLIDATION_REVIEW.md. |
| Documentos previos aún sin seguimiento (`??`), conservados | docs/PRODUCT_BRIEF_ORIGINAL.md, docs/GENERIC_RULES.md, docs/MOBILE_GUIDELINES.md, docs/SPEC_TEMPLATE.md, docs/PLAN_TEMPLATE.md. |

**AGENTS.md tiene 0 bytes en el índice.** El contenido completo está en el directorio de trabajo. Se conserva el índice por instrucción de Rafael; un commit con el índice actual no incluiría sus instrucciones actualizadas. Los tres archivos Kotlin preparados contienen solo esqueletos anteriores de cuatro líneas cada uno, no sus implementaciones actuales. No preparar ni confirmar ese conjunto sin revisar ambas versiones.

Para un futuro commit documental, se recomiendan los seis archivos creados/modificados en esta tarea y los cinco documentos previos sin seguimiento de los que depende la navegación documental. Revisarlos antes y elegirlos expresamente. Los cambios Kotlin de `NativeAuthenticationProvider.kt`, `NativeAuthenticationRepository.kt` y `NativeAuthenticationRepositoryTest.kt` son trabajo preexistente y deben revisarse por separado; no forman parte de esta consolidación.

El último commit comprobado sigue siendo `cfde7ca` — `Add local profile recovery and session state resolution`. Ninguno de los archivos nuevos de esta consolidación está aún versionado mediante un commit.

## Continuación

Rafael revisará la fidelidad del documento principal y este informe. Después decidirá qué archivos incorporar a Git. Para otra sesión en el mismo directorio basta disponer de estos archivos; para otro ordenador, Rafael deberá incorporarlos y transferirlos mediante su flujo de Git. El prompt de continuidad exige leerlos antes de especificar, planificar o implementar.

El último avance técnico sigue siendo el test de propagación de errores de `NativeAuthenticationRepository`, cuyo éxito comunicó Rafael. Cierre de sesión/cambio de cuenta no está aprobado. No se ha iniciado una SPEC de funcionalidad, un PLAN ni código nuevo durante esta consolidación.
