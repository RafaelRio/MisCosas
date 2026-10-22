# MisCosas — Prompt original del producto

## Procedencia y uso

Rafael compartió este prompt en la conversación del proyecto como el texto con el que comenzó su desarrollo. Se incorpora al repositorio el 20 de octubre de 2026; esta fecha no pretende ser la fecha de redacción del prompt original.

**El nombre final de la aplicación es MisCosas**, confirmado por Rafael el 20 de octubre de 2026. La transcripción conserva «Nombre provisional: Mío» para respetar el texto histórico; ese nombre provisional queda sustituido por MisCosas.

Este documento conserva la visión completa del producto y las preferencias iniciales de arquitectura y aprendizaje. No acredita funcionalidades implementadas ni fija por sí solo el alcance de la primera versión. Los requisitos se concretarán mediante el mapa global del producto y las especificaciones de cada funcionalidad.

Las peticiones de actuación dentro de la transcripción, especialmente «RESULTADO QUE QUIERO DE TI AHORA», pertenecen al inicio del proyecto. No son una orden de reiniciar el desarrollo ni de generar de nuevo todos los modelos. Las instrucciones actuales de Rafael y [AGENTS.md](../AGENTS.md) rigen la forma de trabajo; [DEVELOPMENT_STATUS.md](DEVELOPMENT_STATUS.md), contrastado con código, Git y evidencias, describe el punto de reanudación.

## Transcripción íntegra del prompt original

```text
Quiero que actúes como arquitecto senior de Kotlin Multiplatform y mentor técnico dentro de este proyecto.

NO quiero que implementes toda la aplicación de golpe.

Tu función principal será:
- analizar el estado actual del proyecto;
- proponer una arquitectura adecuada;
- dividir el desarrollo en pasos pequeños;
- explicarme qué voy a construir en cada paso;
- indicarme qué archivos, modelos e interfaces necesito;
- esperar a que yo complete o apruebe cada paso antes de continuar.

Cuando te pida ayuda con un paso concreto, sí puedes generar código completo y listo para copiar, pero NO adelantes fases ni crees decenas de archivos sin que te lo pida.

## CONTEXTO DEL PRODUCTO

La aplicación es un "pasaporte digital de todo lo que posees".

Nombre provisional: Mío.

Es una app personal/familiar donde los usuarios podrán registrar objetos valiosos y mantener toda su información organizada.

Ejemplos:
- móviles;
- ordenadores;
- auriculares;
- televisores;
- electrodomésticos;
- aire acondicionado;
- cámaras;
- bicicletas;
- herramientas;
- muebles;
- etc.

Cada objeto podrá contener:
- fotografías;
- nombre;
- categoría;
- marca;
- modelo;
- número de serie;
- precio de compra;
- fecha de compra;
- tienda;
- garantía;
- periodo de devolución;
- ticket;
- factura;
- manual;
- otros documentos;
- mantenimiento;
- historial;
- estado actual;
- favorito.

La aplicación será inicialmente para España y en español, pero debe quedar preparada para internacionalización.

## PLATAFORMAS

Kotlin Multiplatform.

Quiero compartir toda la lógica posible entre Android e iOS.

La interfaz NO será idéntica entre plataformas.

Android e iOS deben sentirse nativos.

La lógica, modelos, repositorios, casos de uso, validaciones y persistencia deberían compartirse siempre que tenga sentido.

No quiero forzar código compartido cuando una implementación específica de plataforma sea claramente mejor.

## ARQUITECTURA

Mi preferencia habitual es MVVM.

Quiero una arquitectura mantenible, profesional y suficientemente sólida para portfolio, pero SIN caer en sobreingeniería.

Quiero una separación clara entre:

- presentation;
- domain;
- data.

Puedes proponer ajustes si consideras que otra división funciona mejor en Kotlin Multiplatform, pero explícame por qué.

Evita crear interfaces, use cases o abstracciones que no aporten valor real.

## MODELO LOCAL-FIRST

La aplicación debe diseñarse como local-first.

Los datos deben poder consultarse y modificarse sin conexión.

La sincronización remota servirá para:
- compartir datos entre miembros de un hogar;
- sincronizar varios dispositivos;
- recuperar información al cambiar de móvil.

No quiero que las pantallas dependan directamente de Firebase o de una API remota.

La base de datos local debe ser la fuente principal para la UI.

Más adelante existirá sincronización en segundo plano.

## AUTENTICACIÓN Y HOGARES

Habrá registro obligatorio porque varios miembros podrán pertenecer al mismo hogar.

Firebase Authentication es una opción prevista.

Un usuario podrá:
- crear un hogar;
- pertenecer a un hogar;
- invitar miembros;
- aceptar invitaciones;
- abandonar un hogar;
- administrar miembros;
- transferir administración.

Los objetos pertenecen al hogar, no exclusivamente al usuario que los creó.

Debemos poder saber quién creó o modificó ciertos registros cuando sea útil.

## FUNCIONALIDADES PRINCIPALES

### Objetos
Alta, consulta, modificación, búsqueda, favoritos y archivado.

Estados posibles:
- ACTIVE
- SOLD
- GIFTED
- BROKEN
- LOST
- RECYCLED
- ARCHIVED

### Categorías
Categorías predefinidas inicialmente:
- Tecnología
- Electrodomésticos
- Imagen y sonido
- Hogar
- Cocina
- Movilidad
- Herramientas
- Deporte
- Moda y accesorios
- Otros

Diseña el modelo de forma que en el futuro puedan existir categorías personalizadas si no complica innecesariamente V1.

### Compra
Información como:
- fecha;
- precio;
- moneda;
- tienda.

Solo almacenamos precio histórico de compra.

NO calcularemos valor actual del objeto.

### Garantía
Debe permitir:
- inicio;
- fin;
- duración si es útil;
- vendedor/fabricante;
- tipo;
- documento asociado;
- notas;
- recordatorios.

Estados derivados:
- activa;
- próxima a vencer;
- vencida;
- desconocida.

Evita almacenar información que sea mejor calcular.

### Devolución
Debe poder guardar:
- fecha límite;
- vendedor;
- estado.

Queremos poder calcular mensajes como:
"Te quedan 12 días para devolverlo".

### Documentos
Tipos:
- RECEIPT
- INVOICE
- WARRANTY
- MANUAL
- OTHER

Pueden existir PDF, imágenes u otros archivos.

Los archivos grandes NO deberían guardarse directamente como blobs dentro de la base de datos si existe una estrategia mejor.

Diseña metadatos adecuados para identificarlos local y remotamente.

### Mantenimiento
Un objeto puede tener múltiples tareas de mantenimiento.

Ejemplos:
- limpiar filtros cada 3 meses;
- descalcificar cada 2 meses;
- revisar frenos cada 6 meses.

Necesitamos:
- nombre;
- descripción;
- periodicidad;
- última realización;
- próxima fecha;
- recordatorio;
- notas;
- historial de ejecuciones.

Piensa cuidadosamente cómo representar periodicidades.

No quiero un String como "cada 3 meses" si existe un modelo de dominio más robusto.

### Historial del objeto
Registrar eventos relevantes como:
- creación;
- compra;
- garantía;
- documento añadido;
- mantenimiento completado;
- reparación;
- cambio de estado;
- venta;
- archivado.

No necesitamos registrar absolutamente cualquier modificación; diseña un historial útil para el usuario.

### Alertas
Inicialmente:
- WARRANTY_EXPIRING;
- RETURN_PERIOD_ENDING;
- MAINTENANCE_DUE;
- MAINTENANCE_OVERDUE.

Analiza si las alertas deben almacenarse como entidades o derivarse de datos existentes.

Prefiero evitar duplicar información.

### Favoritos
Un objeto puede estar marcado como favorito.

### Búsqueda y filtros
Buscar por:
- nombre;
- marca;
- modelo;
- número de serie;
- categoría.

Filtrar por:
- categoría;
- marca;
- fecha;
- precio;
- garantía;
- devolución;
- favoritos;
- archivados;
- mantenimiento pendiente.

### Estadísticas
Queremos poder calcular:
- cantidad de objetos;
- valor histórico total;
- gasto por categoría;
- gasto por año;
- marcas más frecuentes;
- categorías más frecuentes;
- garantías activas/vencidas.

No crear modelos persistentes para estadísticas si pueden calcularse eficientemente.

### Exportación
Más adelante:
- PDF;
- CSV.

No es prioritario para la primera fase de arquitectura, pero evita decisiones que lo compliquen.

## SINCRONIZACIÓN

Todavía NO implementes el sistema de sincronización completo.

Sin embargo, diseña los modelos de forma que puedan sincronizarse posteriormente.

Considera si las entidades necesitan campos como:
- id;
- householdId;
- createdAt;
- updatedAt;
- createdBy;
- updatedBy;
- syncState;
- deletedAt/version.

No añadas todos automáticamente.

Analiza cuáles son realmente necesarios y explícame sus ventajas e inconvenientes.

Necesitamos poder soportar eventualmente:
- cambios offline;
- varios dispositivos;
- varios miembros;
- conflictos simples;
- borrado sincronizado.

## IDENTIFICADORES Y FECHAS

Quiero una estrategia multiplataforma clara para:
- UUID/IDs;
- Instant;
- LocalDate;
- zona horaria.

No uses tipos específicos de Android dentro de commonMain.

Explícame cuándo debemos almacenar:
- una fecha sin hora;
- un instante UTC;
- una hora local.

## DINERO

Diseña correctamente el modelo monetario.

NO quiero almacenar precios mediante Float o Double si puede provocar errores.

La app comienza usando EUR pero debe ser técnicamente posible soportar otras monedas posteriormente.

## RESULTADO QUE QUIERO DE TI AHORA

NO escribas todavía toda la implementación.

Primero analiza este proyecto y dame un PLAN DE DESARROLLO POR FASES.

Quiero algo parecido a:

Fase 0 — revisar/configurar proyecto KMP
Fase 1 — módulos y paquetes
Fase 2 — modelos de dominio
Fase 3 — persistencia local
Fase 4 — repositorios
Fase 5 — primeros casos de uso
Fase 6 — ViewModels
Fase 7 — primera feature vertical
...
Firebase/sincronización más adelante.

Pero tú debes decidir el orden adecuado.

Después del plan, identifica específicamente los MODELOS DE DOMINIO que necesitaremos.

Para cada modelo indica:
- propósito;
- campos propuestos;
- tipos;
- campos obligatorios/opcionales;
- qué información debe calcularse en lugar de almacenarse;
- relaciones con otros modelos.

Analiza especialmente:

User
Household
HouseholdMember
Product/Object
Category
Purchase
Warranty
ReturnPeriod
Document
MaintenanceTask
MaintenanceRecord
ProductHistoryEvent

y cualquier otro modelo que consideres realmente necesario.

También dime qué modelos de esa lista NO crearías y por qué.

## FORMA DE TRABAJAR

Quiero ir paso a paso.

Después de darme:
1. plan general;
2. arquitectura propuesta;
3. modelos propuestos;

DETENTE.

No generes todavía todos los archivos.

Termina preguntándome con qué primer paso empezamos.

A partir de ahí trabajaremos una única fase cada vez.

En cada fase:

1. explícame qué vamos a conseguir;
2. indícame exactamente qué archivos crear/modificar;
3. explica las decisiones importantes;
4. dame código solo cuando corresponda;
5. después espera mi resultado antes de continuar.

Si detectas que el proyecto actual ya tiene algo implementado, inspecciónalo antes de recomendar cambios.

Prioriza soluciones idiomáticas y actuales de Kotlin Multiplatform.

No introduzcas dependencias simplemente porque son populares. Justifica cada dependencia propuesta.

El objetivo es acabar con una aplicación suficientemente sólida como para enseñar en portfolio y discutir su arquitectura durante una entrevista técnica, pero cuyo código siga siendo entendible y mantenible por una sola persona.
```
