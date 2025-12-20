# Sprint 3 - Reporte de Desarrollo

## Información General del Sprint

- **Número de Sprint:** 3
- **Duración:** [Fecha de inicio] - [Fecha de fin]
- **Objetivo del Sprint:** Implementar funcionalidades avanzadas de gestión de movimientos financieros, incluyendo edición, eliminación, categorización, búsqueda y análisis detallado de gastos.

---

## Sprint Backlog

### Total de Historias de Usuario: 6
### Total de Puntos de Historia: [Estimación total]

---

## Historias de Usuario Implementadas

### HU-09: Edición de Movimientos

**Prioridad:** Alta  
**Puntos de Historia:** 5  
**Estado:** ✅ Completada

#### Descripción
Como usuario, quiero poder editar los movimientos que ya he registrado para corregir errores o actualizar información.

#### Criterios de Aceptación
- ✅ Se agregó botón de editar en cada item del RecyclerView (`item_movement.xml`)
- ✅ Se creó `EditMovementActivity.kt` para editar movimientos
- ✅ Los datos existentes se cargan correctamente en el formulario
- ✅ Se implementó método `actualizarMovimiento()` en `DatabaseHelper`
- ✅ El RecyclerView se actualiza automáticamente después de editar
- ✅ La fecha original del movimiento se mantiene al editar

#### Tareas Realizadas
1. Agregar botón de editar en `item_movement.xml`
2. Crear `EditMovementActivity.kt`
3. Implementar método `obtenerMovimientoPorId()` en `DatabaseHelper`
4. Implementar método `actualizarMovimiento()` en `DatabaseHelper`
5. Actualizar `MovimientoAdapter` para manejar clicks de edición
6. Actualizar `MainActivity` para iniciar `EditMovementActivity`
7. Registrar `EditMovementActivity` en `AndroidManifest.xml`

#### Archivos Modificados/Creados
- `app/src/main/res/layout/item_movement.xml` - Agregado botón editar
- `app/src/main/java/mx/itson/controldegastos/EditMovementActivity.kt` - Nueva actividad
- `app/src/main/java/mx/itson/controldegastos/adapter/MovimientoAdapter.kt` - Callbacks de edición
- `app/src/main/java/mx/itson/controldegastos/MainActivity.kt` - Manejo de edición
- `app/src/main/java/mx/itson/controldegastos/database/DatabaseHelper.kt` - Métodos de actualización
- `app/src/main/AndroidManifest.xml` - Registro de actividad

---

### HU-10: Eliminación de Movimientos

**Prioridad:** Alta  
**Puntos de Historia:** 5  
**Estado:** ✅ Completada

#### Descripción
Como usuario, quiero poder eliminar movimientos que ya no necesito para mantener mi registro limpio y organizado.

#### Criterios de Aceptación
- ✅ Se implementó swipe to delete en RecyclerView usando `ItemTouchHelper`
- ✅ Se agregó diálogo de confirmación antes de eliminar
- ✅ Se implementó método `eliminarMovimiento()` en `DatabaseHelper`
- ✅ Los totales se actualizan automáticamente después de eliminar
- ✅ Los gráficos se actualizan correctamente después de eliminar

#### Tareas Realizadas
1. Implementar `ItemTouchHelper` en `MainActivity`
2. Crear diálogo de confirmación de eliminación
3. Implementar método `eliminarMovimiento()` en `DatabaseHelper`
4. Actualizar `MovimientoAdapter` para soportar eliminación
5. Integrar actualización automática de totales y gráficos

#### Archivos Modificados/Creados
- `app/src/main/java/mx/itson/controldegastos/MainActivity.kt` - Swipe to delete y diálogo
- `app/src/main/java/mx/itson/controldegastos/adapter/MovimientoAdapter.kt` - Callbacks de eliminación
- `app/src/main/java/mx/itson/controldegastos/database/DatabaseHelper.kt` - Método eliminarMovimiento()
- `app/src/main/res/values/strings.xml` - Strings para confirmación

---

### HU-11: Categorías de Gastos

**Prioridad:** Media  
**Puntos de Historia:** 8  
**Estado:** ✅ Completada

#### Descripción
Como usuario, quiero poder categorizar mis gastos para tener un mejor control y análisis de mis finanzas personales.

#### Criterios de Aceptación
- ✅ Se creó tabla `categorias` en SQLite
- ✅ Se agregó columna `categoria` a la tabla `movimientos`
- ✅ Se insertaron categorías iniciales: Comida, Transporte, Entretenimiento, Salud, Otros
- ✅ Se agregó spinner de categorías en formularios de agregar/editar
- ✅ Se actualizó modelo `Movimiento` para incluir categoría
- ✅ Se implementó filtrado por categoría en lista de movimientos
- ✅ Migración de base de datos (versión 1 → 2) implementada correctamente

#### Tareas Realizadas
1. Actualizar `DatabaseHelper` para crear tabla categorías
2. Modificar esquema de base de datos (versión 2)
3. Implementar método `insertarCategoriasIniciales()`
4. Implementar método `obtenerCategorias()`
5. Actualizar modelo `Movimiento` con campo `categoria`
6. Actualizar métodos de inserción y actualización para incluir categoría
7. Agregar spinner de categorías en `activity_add_movement.xml`
8. Actualizar `AddMovementActivity` y `EditMovementActivity` para manejar categorías
9. Implementar migración de base de datos en `onUpgrade()`

#### Archivos Modificados/Creados
- `app/src/main/java/mx/itson/controldegastos/model/Movimiento.kt` - Campo categoria
- `app/src/main/java/mx/itson/controldegastos/database/DatabaseHelper.kt` - Tabla categorías y migración
- `app/src/main/res/layout/activity_add_movement.xml` - Spinner categorías
- `app/src/main/java/mx/itson/controldegastos/AddMovementActivity.kt` - Manejo de categorías
- `app/src/main/java/mx/itson/controldegastos/EditMovementActivity.kt` - Manejo de categorías
- `app/src/main/res/values/strings.xml` - Strings de categorías

---

### HU-12: Gráfico Circular por Categorías

**Prioridad:** Media  
**Puntos de Historia:** 5  
**Estado:** ✅ Completada

#### Descripción
Como usuario, quiero ver un gráfico circular que muestre la distribución de mis gastos por categoría para entender mejor en qué gasto mi dinero.

#### Criterios de Aceptación
- ✅ Se implementó consulta SQLite para agrupar gastos por categoría
- ✅ Se agregó `PieChart` con MPAndroidChart en `GraphActivity`
- ✅ Se implementó leyenda personalizada con colores por categoría
- ✅ Se muestran porcentajes en el gráfico circular
- ✅ El gráfico se oculta si no hay gastos para mostrar

#### Tareas Realizadas
1. Implementar método `obtenerGastosPorCategoria()` en `DatabaseHelper`
2. Agregar `PieChart` al layout `activity_graph.xml`
3. Configurar gráfico circular con colores personalizados
4. Implementar formateo de porcentajes
5. Manejar caso cuando no hay datos

#### Archivos Modificados/Creados
- `app/src/main/java/mx/itson/controldegastos/database/DatabaseHelper.kt` - Método obtenerGastosPorCategoria()
- `app/src/main/res/layout/activity_graph.xml` - PieChart agregado
- `app/src/main/java/mx/itson/controldegastos/GraphActivity.kt` - Lógica del gráfico circular

---

### HU-13: Búsqueda en Movimientos

**Prioridad:** Media  
**Puntos de Historia:** 3  
**Estado:** ✅ Completada

#### Descripción
Como usuario, quiero poder buscar movimientos por descripción para encontrar rápidamente registros específicos.

#### Criterios de Aceptación
- ✅ Se agregó `SearchView` en el action bar de `MainActivity`
- ✅ Se implementó filtrado por texto en descripción usando LIKE en SQLite
- ✅ La búsqueda se combina con filtros existentes (tipo)
- ✅ La búsqueda se actualiza automáticamente mientras se escribe
- ✅ Se puede limpiar la búsqueda fácilmente

#### Tareas Realizadas
1. Crear archivo `menu_main.xml` con SearchView
2. Implementar método `buscarMovimientos()` en `DatabaseHelper`
3. Integrar SearchView en `MainActivity`
4. Actualizar método `cargarMovimientos()` para manejar búsqueda

#### Archivos Modificados/Creados
- `app/src/main/res/menu/menu_main.xml` - Nuevo archivo con SearchView
- `app/src/main/java/mx/itson/controldegastos/MainActivity.kt` - Integración de búsqueda
- `app/src/main/java/mx/itson/controldegastos/database/DatabaseHelper.kt` - Método buscarMovimientos()
- `app/src/main/res/values/strings.xml` - String de búsqueda

---

### HU-14: Balance Semanal Detallado

**Prioridad:** Baja  
**Puntos de Historia:** 8  
**Estado:** ✅ Completada

#### Descripción
Como usuario, quiero ver un análisis detallado de mi balance semanal con comparación con la semana anterior para identificar tendencias en mis finanzas.

#### Criterios de Aceptación
- ✅ Se creó `WeeklyBalanceActivity.kt`
- ✅ Se calculan totales por día de la semana actual
- ✅ Se implementa gráfico de tendencia semanal (LineChart)
- ✅ Se muestra comparativa con semana anterior
- ✅ Se muestra resumen con totales y diferencia entre semanas

#### Tareas Realizadas
1. Crear `WeeklyBalanceActivity.kt`
2. Crear layout `activity_weekly_balance.xml`
3. Implementar método `obtenerTotalPorDia()` en `DatabaseHelper`
4. Implementar cálculo de días de semana (lunes a domingo)
5. Configurar gráfico de líneas con dos series (semana actual y anterior)
6. Calcular totales y diferencias
7. Agregar botón de navegación en `MainActivity`
8. Registrar actividad en `AndroidManifest.xml`

#### Archivos Modificados/Creados
- `app/src/main/java/mx/itson/controldegastos/WeeklyBalanceActivity.kt` - Nueva actividad
- `app/src/main/res/layout/activity_weekly_balance.xml` - Nuevo layout
- `app/src/main/java/mx/itson/controldegastos/database/DatabaseHelper.kt` - Método obtenerTotalPorDia()
- `app/src/main/java/mx/itson/controldegastos/MainActivity.kt` - Botón de navegación
- `app/src/main/res/layout/activity_main.xml` - Botón balance semanal
- `app/src/main/AndroidManifest.xml` - Registro de actividad

---

## Resumen de Cambios Técnicos

### Base de Datos
- **Versión actualizada:** 1 → 2
- **Nueva tabla:** `categorias`
- **Modificación de tabla:** `movimientos` (agregada columna `categoria`)
- **Métodos nuevos agregados:**
  - `obtenerMovimientoPorId()`
  - `actualizarMovimiento()`
  - `eliminarMovimiento()`
  - `obtenerCategorias()`
  - `obtenerGastosPorCategoria()`
  - `buscarMovimientos()`
  - `obtenerTotalPorDia()`

### Nuevas Actividades
- `EditMovementActivity`
- `WeeklyBalanceActivity`

### Nuevos Layouts
- `activity_weekly_balance.xml`
- `menu_main.xml` (menú con SearchView)

### Dependencias Utilizadas
- `androidx.recyclerview:recyclerview` - Para RecyclerView y ItemTouchHelper
- `com.github.PhilJay:MPAndroidChart` - Para gráficos (ya existente)

---

## Métricas del Sprint

### Velocidad
- **Historias completadas:** 6/6 (100%)
- **Puntos de historia completados:** [Total estimado]

### Calidad
- **Errores de compilación:** 0
- **Errores de lint:** 0
- **Cobertura de pruebas:** [Si aplica]

### Técnicas
- **Archivos modificados:** ~15
- **Archivos nuevos:** ~5
- **Líneas de código agregadas:** ~800+

---

## Impedimentos y Soluciones

### Impedimentos Encontrados
1. **Migración de base de datos:** Necesidad de actualizar esquema sin perder datos existentes
   - **Solución:** Implementación cuidadosa de `onUpgrade()` con ALTER TABLE

2. **Cálculo de días de semana:** Manejo correcto de Calendar para obtener lunes a domingo
   - **Solución:** Cálculo de días desde lunes usando `DAY_OF_YEAR` en lugar de `DAY_OF_WEEK`

### Lecciones Aprendidas
- Las migraciones de base de datos requieren cuidado especial para mantener compatibilidad
- El uso de `ItemTouchHelper` facilita la implementación de gestos como swipe to delete
- Los gráficos de MPAndroidChart son potentes pero requieren configuración adecuada

---

## Retrospectiva del Sprint

### ¿Qué salió bien?
- ✅ Implementación completa de todas las historias de usuario planificadas
- ✅ Código limpio y bien estructurado
- ✅ Sin errores de compilación o lint
- ✅ Interfaz intuitiva y consistente con Material Design

### ¿Qué se puede mejorar?
- Considerar agregar validaciones adicionales en formularios
- Implementar pruebas unitarias para métodos críticos
- Documentar mejor los métodos complejos en DatabaseHelper

### Próximos Pasos
- Revisión de código por pares
- Pruebas de integración
- Preparación para Sprint 4 (si aplica)

---

## Definición de "Done"

Todas las historias de usuario cumplen con:
- ✅ Código implementado y funcionando
- ✅ Sin errores de compilación
- ✅ Sin errores de lint
- ✅ Criterios de aceptación cumplidos
- ✅ Integración correcta con funcionalidades existentes
- ✅ Registro de actividades en AndroidManifest.xml
- ✅ Strings externalizados en resources

---

## Notas Adicionales

- Todas las funcionalidades están integradas y funcionando correctamente
- La aplicación mantiene compatibilidad con datos existentes gracias a la migración de base de datos
- Se mantiene el diseño Material Design consistente en toda la aplicación
- Los gráficos proporcionan visualizaciones claras y útiles para el análisis financiero

---

**Fecha de creación:** [Fecha actual]  
**Equipo de Desarrollo:** [Nombre del equipo]  
**Scrum Master:** [Nombre]  
**Product Owner:** [Nombre]

