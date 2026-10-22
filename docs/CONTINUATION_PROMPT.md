# Prompt para reanudar MisCosas

Copia y pega el siguiente texto al iniciar una conversación nueva con el repositorio abierto:

```text
Estoy retomando MisCosas, una aplicación KMP local-first con interfaces
nativas en Android (Compose) e iOS (SwiftUI), Room KMP y Firebase previsto
para autenticación, Firestore y Storage. Seguimos SDMD y una forma de
trabajo guiada: yo escribo los documentos, el código y los tests.

Antes de continuar:

1. Lee AGENTS.md y los documentos que indica.
2. Lee completamente docs/PRODUCT_REQUIREMENTS_V1.md antes de continuar
   especificando, planificando o implementando. Es la fuente principal
   del alcance acordado: conserva los IDs y diferencia lo aprobado,
   los pendientes, las propuestas no aprobadas y las exclusiones.
   El brief original conserva la visión histórica; no sustituye los
   acuerdos posteriores. No deduzcas aprobaciones de Figma ni del código.
3. Revisa docs/DEVELOPMENT_STATUS.md y, al revisar la consolidación,
   docs/REQUIREMENTS_CONSOLIDATION_REVIEW.md.
4. Comprueba git status --short y git log --oneline -10; revisa también
   las diferencias preparadas para commit sin modificar el índice.
5. Contrasta el punto de reanudación con el código actual.
6. Si hay una funcionalidad activa documentada, revisa su SPEC,
   PLAN y TASKS disponibles e identifica la etapa en la que estamos.
7. Resume qué está terminado, qué está pendiente y cualquier
   diferencia entre la documentación y el código.
8. Propón únicamente el siguiente paso pequeño y espera mi resultado.

El cierre de sesión y cambio de cuenta se registró como propuesta,
no como alcance aprobado. Consulta su estado vigente antes de avanzar.
Una aprobación funcional no autoriza implementación ni resuelve por sí
sola los pendientes. No heredes una autorización documental puntual
como permiso para escribir código o completar todo el roadmap.

Utiliza los agentes especializados según AGENTS.md, comprobando
su disponibilidad e indicando cuándo intervienen.

Esta petición es para recuperar contexto y orientarme.
No autoriza modificaciones de archivos ni operaciones de escritura
en Git.
```

Este prompt permite recuperar el contexto desde los archivos del repositorio al cambiar de ordenador o conversación. Las reglas de trabajo están en [AGENTS.md](../AGENTS.md); los acuerdos y cuestiones abiertas, en [PRODUCT_REQUIREMENTS_V1.md](PRODUCT_REQUIREMENTS_V1.md); el estado realizado y la funcionalidad activa, en [DEVELOPMENT_STATUS.md](DEVELOPMENT_STATUS.md), con enlaces a los documentos SDMD cuando existan. El informe [REQUIREMENTS_CONSOLIDATION_REVIEW.md](REQUIREMENTS_CONSOLIDATION_REVIEW.md) deja constancia del contraste independiente y sus límites.

Para recuperar estos archivos en otro ordenador deberán haberse incorporado a Git y transferido al repositorio correspondiente por Rafael. Su existencia en el directorio de trabajo no significa que estén incluidos en un commit.
