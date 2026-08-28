# Prompt para reanudar MisCosas

Copia y pega el siguiente texto al iniciar una conversación nueva con el repositorio abierto:

```text
Estoy continuando el desarrollo de MisCosas, una aplicación KMP local-first con Compose en Android, SwiftUI en iOS, Room KMP y Firebase previsto para autenticación, Firestore y Storage.

Antes de proponer o modificar nada:

1. Lee COMPLETAMENTE docs/DEVELOPMENT_STATUS.md.
2. Comprueba git status --short y los últimos commits con git log --oneline -10.
3. Inspecciona los archivos citados en el punto de reanudación y contrasta la documentación con el código. El código y Git son la fuente de verdad si hay alguna diferencia.
4. Dime en pocas palabras cuál es el punto exacto y propón únicamente el siguiente micro-paso.

Forma de trabajo obligatoria:

- Háblame en español y explícame qué hacemos, por qué y cómo se comportaría en un caso real.
- Este proyecto es para aprender: normalmente escribo yo el código y los tests.
- No me entregues una implementación completa para copiar y pegar salvo que te lo pida o te diga que no sé continuar.
- Avanza un solo micro-paso TDD cada vez y espera mi resultado antes de seguir.
- Puedes corregir directamente imports, formato, comas y otros cambios mecánicos.
- Revisa el código real antes de recomendar abstracciones o dependencias.
- No introduzcas Firebase, Koin, repositorios, tablas o campos por anticipación sin un consumidor y una decisión justificada.
- Mantén Room, DAOs, entidades e implementaciones concretas internos; no expongas esos tipos a Swift.
- Actualiza docs/DEVELOPMENT_STATUS.md al cerrar cada bloque.
- De Git me encargo yo: al terminar solo debes darme el mensaje de commit recomendado, sin hacer commit, push, stage ni otras operaciones por mí.

Retoma desde la sección “Próximo bloque” de docs/DEVELOPMENT_STATUS.md. No ejecutes de golpe el roadmap posterior: vuelve a evaluarlo y guíame paso a paso.
```

Este prompt no depende de una conversación concreta. Al mantenerse actualizado `DEVELOPMENT_STATUS.md`, seguirá funcionando aunque cambien el ordenador, la ruta local o el estado del proyecto.
