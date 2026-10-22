# Instrucciones para los agentes de MisCosas

## Forma de trabajo

- Comunicarse en español y explicar qué hacemos y por qué.
- Rafael escribe los documentos, el código y los tests.
- Avanzar un único paso pequeño cada vez y esperar su resultado.
- Leer el código real antes de recomendar cambios.
- No modificar archivos salvo autorización explícita para un cambio concreto.
- Aprobar una SPEC o un PLAN no autoriza al asistente a implementarlo.
- Los agentes especializados asesoran y revisan bajo estas mismas reglas.
- Indicar qué agente interviene, qué revisa y cuáles son sus hallazgos.
- No presentar una recomendación como un hecho ni una prueba sin ejecutar como validada.
- Rafael gestiona Git; no realizar stage, commits ni push.

La tutoría es el modo predeterminado, también para documentación y cambios de
formato. Una autorización puntual permite terminar ese trabajo, sin pedirla
de nuevo para sus pasos necesarios; no autoriza tareas posteriores ni todo el
roadmap. No entregar implementaciones completas salvo petición o necesidad
de ayuda expresada por Rafael.

Se permiten las lecturas e inspecciones necesarias para explicar y revisar.
Al orientar un test, proporcionar el comando y esperar el resultado de Rafael;
si pide que el asistente lo ejecute, hacerlo dentro del alcance autorizado.
Un fallo esperado del ciclo TDD no debe presentarse como trabajo terminado.

## Lecturas obligatorias

Al iniciar o retomar el trabajo:

1. Leer completamente `docs/GENERIC_RULES.md`.
2. Leer completamente `docs/PRODUCT_REQUIREMENTS_V1.md` antes de continuar
   con definición, especificaciones, planificación o implementación. Distinguir
   requisitos aprobados, pendientes, propuestas no aprobadas y exclusiones.
3. Leer `docs/DEVELOPMENT_STATUS.md` y contrastar el estado descrito
   con el código, `git status --short` y `git log --oneline -10`.
4. Si existe una funcionalidad activa en `docs/features/`, leer sus
   documentos disponibles: `SPEC.md`, `PLAN.md` y `TASKS.md`.

Según la etapa:

- Para preparar o revisar una especificación, leer completamente
  `docs/SPEC_TEMPLATE.md` y `docs/MOBILE_GUIDELINES.md`.
- Para preparar o revisar un plan, leer completamente
  `docs/PLAN_TEMPLATE.md`, la SPEC aprobada y las consideraciones
  aplicables de `docs/MOBILE_GUIDELINES.md`.

El código muestra el comportamiento actual; los requisitos consolidados y
la SPEC aprobada definen el comportamiento esperado. Si difieren, explicar
la diferencia antes de proponer cambios. Leer también el informe
`docs/REQUIREMENTS_CONSOLIDATION_REVIEW.md` al revisar esta consolidación.

Las instrucciones explícitas de Rafael prevalecen sobre estas guías, dentro
de las instrucciones superiores del entorno. Los permisos genéricos antiguos
de otros documentos, como corregir archivos automáticamente, no amplían la
autorización concreta vigente.

## Proyecto y arquitectura

MisCosas es un pasaporte digital personal y familiar para gestionar objetos,
compras, garantías, devoluciones, documentos y mantenimiento. El alcance de
cada entrega se acuerda en su SPEC; la visión del producto no autoriza a
implementar todas las funcionalidades de una vez.

| Ubicación | Responsabilidad |
| --- | --- |
| `androidApp/` | Aplicación Android e interfaz nativa con Compose. |
| `iosApp/` | Aplicación iOS e interfaz nativa con SwiftUI. |
| `sharedLogic/src/commonMain/` | Dominio, persistencia compartida y contratos de integración. |
| `sharedLogic/src/androidMain/`, `sharedLogic/src/iosMain/` | Código específico de plataforma del módulo compartido. |
| `sharedLogic/src/commonTest/` | Pruebas compartidas de lógica y adaptadores. |
| `sharedLogic/src/iosTest/` | Pruebas de integración Room en iOS. |

- No hay UI compartida. Mantener las interfaces nativas de ambas aplicaciones.
- El dominio contiene modelos, contratos de repositorio y casos de uso; no
  depende de Room, Firebase, Android ni iOS. Los adaptadores implementan los
  contratos del dominio y convierten las representaciones externas.
- Room es la fuente de verdad de los datos persistentes mostrados por la UI.
  Una mutación local sincronizable guarda el dato y su operación de outbox
  dentro de una misma transacción.
- Los cambios remotos se aplican a Room. No deben entrar por una escritura
  local que genere otra operación de outbox.
- Mantener internos Room, DAOs, entidades e implementaciones concretas.
  Exponer únicamente contratos y resultados necesarios para las aplicaciones;
  comprobar su interoperabilidad real con Swift antes de darla por resuelta.
- Para autenticación, la dirección acordada es usar los SDK nativos desde
  cada aplicación mediante un puente público neutral y un adaptador Kotlin
  interno. Los tipos Firebase no deben cruzar al dominio. Contratos escritos
  y pruebas con falsos no demuestran una integración nativa terminada.
- Usar inyección por constructor y composición manual. No introducir Koin,
  Hilt, nuevas capas ni un repositorio por tabla sin una necesidad comprobada.
- Respetar la cancelación de coroutines. No convertir fallos en resultados de
  éxito, ausencia de sesión o datos vacíos para ocultarlos.
- Comprobar las versiones en `gradle/libs.versions.toml` y los scripts de
  construcción antes de proponer dependencias. No copiar el stack ni los
  comandos Android del curso como si fueran los de MisCosas.

## Documentación y fuentes de verdad

Este flujo se adapta de [Curso-SDD-Mobile de ArisGuimera](https://github.com/ArisGuimera/Curso-SDD-Mobile).
Sus plantillas son una guía de proceso; la arquitectura es la de MisCosas.

- `AGENTS.md` establece las instrucciones del proyecto y el uso de agentes.
- `docs/PRODUCT_REQUIREMENTS_V1.md` es la fuente principal del alcance
  funcional aprobado, sus restricciones, exclusiones y decisiones pendientes.
  Sus IDs son estables. No confundir aprobación de alcance con una SPEC
  completa, un PLAN aprobado o una funcionalidad implementada. Cierre de
  sesión y cambio de cuenta siguen como propuesta hasta aprobación expresa.
- `docs/PRODUCT_BRIEF_ORIGINAL.md` conserva íntegra la visión inicial como
  fuente histórica. Los acuerdos posteriores se registran en los requisitos;
  no reescribir el brief para hacer desaparecer decisiones anteriores.
- `docs/GENERIC_RULES.md` contiene reglas generales complementarias.
- `docs/MOBILE_GUIDELINES.md` ayuda a identificar comportamientos móviles
  aplicables; no amplía el alcance por sí misma.
- `docs/SPEC_TEMPLATE.md` y `docs/PLAN_TEMPLATE.md` son plantillas compartidas.
  Conservar su estructura y comentarios al crear documentos de funcionalidad.
- `docs/DEVELOPMENT_STATUS.md` resume el estado, decisiones y punto de
  reanudación, y debe enlazar los documentos de la funcionalidad activa.
- `docs/CONTINUATION_PROMPT.md` sirve para retomar el trabajo; debe remitir a
  estas reglas y no conceder permisos más amplios que el modo acordado.

Organización prevista por funcionalidad:

```text
docs/features/<nombre-de-la-funcionalidad>/
  SPEC.md
  PLAN.md
  TASKS.md
```

Crear cada documento cuando corresponda a su etapa. Reutilizar una carpeta
existente al continuar una funcionalidad. No reescribir todo el proyecto para
adoptar SDMD: registrar lo existente y distinguirlo de los cambios propuestos.
Las especificaciones definen lo esperado; el código y las evidencias describen
lo realizado. No cambiar requisitos para encubrir una implementación incompleta.
Al cerrar un acuerdo, indicar si ya está registrado en archivos o solo en la
conversación. Proponer su incorporación documental cuando falte, escribiendo
solo dentro de la autorización vigente. La memoria de la conversación no
sustituye los archivos que necesita una sesión nueva.

## Flujo SDMD y TDD

1. **SPEC:** completar la plantilla con Rafael, por secciones. Definir objetivo,
   situación actual, alcance, flujos, reglas, casos alternativos y criterios
   observables. Usar los identificadores `RF-` y `CA-` de la plantilla y
   enlazarlos con los IDs de `PRODUCT_REQUIREMENTS_V1.md`. Mantener
   como `PENDIENTE` las decisiones sin resolver; no inventar requisitos.
   Las clases, tablas y algoritmos pertenecen al PLAN.
2. **Revisión de SPEC:** conservar los estados de la plantilla. Solo marcar
   `Aprobada` cuando Rafael lo confirme; resolver antes los pendientes que
   afecten al alcance y comportamiento. Una plantilla rellenada no implica
   aprobación. No comenzar el PLAN hasta aprobar la SPEC.
3. **PLAN:** usar la plantilla y referenciar la SPEC aprobada y su versión.
   Verificar componentes existentes, contratos, plataformas, dependencias,
   errores, cancelación y validaciones. Identificar los agentes que ayudarán
   y el objetivo acotado de cada intervención. Solo marcar `Aprobado` tras
   confirmación de Rafael.
4. **TASKS:** derivar las tareas del PLAN aprobado; no existe una plantilla
   compartida de TASKS. Cada tarea tendrá ID estable, objetivo, alcance,
   dependencias, RF/CA relacionados, comprobación y estado. Deben ser pequeñas
   y ejecutables, con evidencia suficiente para decidir cuándo están completas.
5. **Implementación guiada:** tras acordar comenzar, Rafael ejecuta un paso
   cada vez. Aplicar TDD cuando proceda: escribir el test, comprobar el fallo
   pertinente, implementar lo mínimo y validar. Si el comportamiento ya existe,
   reconocerlo; no fingir un ciclo rojo-verde ni introducir fallos artificiales.
6. **Validación y cierre:** contrastar los criterios con los resultados reales.
   Registrar qué pasó, qué falló o qué quedó pendiente y en qué plataforma.
   Proponer las actualizaciones de TASKS y DEVELOPMENT_STATUS; escribirlas
   directamente solo si Rafael autoriza ese cambio. Revisar diferencias e
   índice de Git en modo lectura y sugerir un mensaje de commit. Rafael lo hace.

Si aparece una contradicción o falta una decisión de producto, revisar la
parte afectada de SPEC/PLAN antes de implementar ese comportamiento. Formular
pocas preguntas cada vez. Respetar las decisiones ya tomadas y las
autorizaciones vigentes sin pedir confirmaciones repetidas.

## Agentes especializados

Los perfiles de `.codex/agents/` complementan estas instrucciones. Utilizarlos
para tareas acotadas cuando su especialidad aporte valor, no a los cinco en
cada cambio.

| Agente | Perfil | Responsabilidad habitual |
| --- | --- | --- |
| Mobile App Builder | `.codex/agents/engineering-mobile-app-builder.toml` | Arquitectura, PLAN, desglose técnico y revisión de integración KMP/nativa y tests. |
| UX Researcher | `.codex/agents/design-ux-researcher.toml` | Flujos, estados alternativos y criterios de aceptación de SPEC. |
| UI Designer | `.codex/agents/design-ui-designer.toml` | Contraste con Figma, consistencia visual y accesibilidad de interfaz. |
| Rapid Prototyper | `.codex/agents/engineering-rapid-prototyper.toml` | Experimentos concretos para resolver incertidumbres técnicas del PLAN. |
| App Store Optimizer | `.codex/agents/marketing-app-store-optimizer.toml` | Preparación de fichas y presentación del producto al abordar publicación. |

- Antes de delegar, comprobar la disponibilidad real del agente en la sesión.
  Tener su TOML no demuestra que esté activado. Si no se puede activar, indicarlo;
  nunca simular la intervención de un agente por conocer su nombre.
- Informar del agente elegido, su objetivo y sus hallazgos. Asignar contexto,
  alcance y entregable, indicando que el modo predeterminado es solo lectura.
- Los agentes respetan la tutoría, el alcance y los permisos de Rafael.
  Una delegación no amplía la autorización para editar, ejecutar o publicar.
  Si se autorizan ediciones delegadas, asignar archivos concretos y preservar
  los cambios de otros participantes.
- El asistente coordinador contrasta las recomendaciones y explica las
  discrepancias. Ningún perfil impone dependencias o sustituye decisiones
  de producto, criterios de aceptación o validaciones.
- Las revisiones UX sin investigación real son hipótesis o evaluaciones,
  no resultados de entrevistas, pruebas con usuarios ni métricas observadas.

## Diseño y Figma

Referencia de diseño: [MisCosas App en Figma](https://www.figma.com/design/Mej8DX4EZrTL98FRWyV7Jo/MIsCosas-App?node-id=0-1).

Consultar los flujos relevantes durante la especificación y las pantallas
concretas durante la implementación. Registrar enlaces a los nodos utilizados.
Figma orienta el diseño; los estados de error, conectividad y ciclo de vida
deben quedar definidos en la SPEC. No tratar cada pantalla diseñada como una
funcionalidad aprobada ni afirmar que se revisó un diseño inaccesible.

## Validación y comandos

Elegir comprobaciones proporcionales a cada tarea y criterio, desde la raíz
con el wrapper. En `commonTest` usar bibliotecas compatibles con KMP como
`kotlin.test` y `kotlinx-coroutines-test`; reservar herramientas específicas
de Android o iOS para los tests de su plataforma.

Comandos de referencia del proyecto:

```bash
# Una clase concreta: sustituir el marcador por su nombre completo real.
./gradlew :sharedLogic:testAndroidHostTest --tests "<paquete.ClaseTest>" --console=plain

# Tests del módulo compartido en Android Host.
./gradlew :sharedLogic:testAndroidHostTest --console=plain

# Tests comunes e integración iOS; requiere entorno Apple compatible.
./gradlew :sharedLogic:iosSimulatorArm64Test --console=plain

# Comprobar el framework compartido para el simulador iOS.
./gradlew :sharedLogic:linkDebugFrameworkIosSimulatorArm64 --console=plain

# Compilar la aplicación Android.
./gradlew :androidApp:assembleDebug --console=plain
```

- `sharedLogic` usa el plugin Android KMP con HostTest: su tarea es
  `testAndroidHostTest`, no `testDebugUnitTest`.
- Android Host no ejecuta las pruebas Room ubicadas en `iosTest`. Compilar
  el framework iOS tampoco compila ni valida la aplicación SwiftUI completa.
- Al cambiar una API pública consumida por Swift, planificar también la
  compilación y comprobación del consumidor iOS; verificar su esquema y entorno
  reales antes de indicar comandos Xcode. No asumir rutas locales universales.
- Para cambios documentales basta revisar contenido, enlaces y coherencia;
  no exigir builds ni tests artificiales. Los comandos anteriores no son una
  lista que deba ejecutarse tras cada micro-paso.
- Distinguir resultados observados por el asistente de resultados comunicados
  por Rafael, y ejecuciones actuales de resultados históricos. No equiparar
  tests con falsos, compilaciones o capturas con un flujo real validado.
- Registrar los requisitos de dispositivo, simulador o servicios cuando un
  criterio los necesite. Si no se puede validar, dejarlo pendiente con motivo
  y siguiente comprobación concreta.
