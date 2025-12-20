# Sprint 4 - Reporte de Desarrollo

## Información General del Sprint

- **Número de Sprint:** 4
- **Duración:** [Fecha de inicio] - [Fecha de fin]
- **Objetivo del Sprint:** Mejora de la experiencia de usuario, personalización de funcionalidades avanzadas, integración completa de membresías y sistema de configuración de gastos hormiga.

---

## Sprint Backlog

### Total de Historias de Usuario: 8
### Total de Puntos de Historia: [Estimación total]

---

## Historias de Usuario Implementadas

### HU-23: Integración de Membresías en Lista de Movimientos

**Prioridad:** Alta  
**Puntos de Historia:** 5  
**Estado:** ✅ Completada

#### Descripción
Como usuario, quiero ver mis membresías en la lista de movimientos cuando selecciono "Todos", para tener una visión completa de todos mis gastos en un solo lugar.

#### Criterios de Aceptación
- ✅ Las membresías aparecen en la lista cuando se selecciona el filtro "Todos"
- ✅ Las membresías se muestran con el monto mensual convertido
- ✅ Las membresías aparecen en los resultados de búsqueda
- ✅ Las membresías mantienen su categoría "Membresías"
- ✅ Los movimientos se ordenan por fecha (más recientes primero)

#### Tareas Realizadas
1. Modificación de `MovementsActivity.kt` para incluir membresías en el filtro "Todos"
2. Implementación de conversión de membresías a objetos `Movimiento`
3. Integración de membresías en la búsqueda de movimientos
4. Actualización del método `cargarMovimientos()` para combinar movimientos y membresías

#### Archivos Modificados/Creados
- `app/src/main/java/mx/itson/controldegastos/MovementsActivity.kt`

---

### HU-24: Mejora del Balance Semanal con Membresías y Fecha Dinámica

**Prioridad:** Alta  
**Puntos de Historia:** 8  
**Estado:** ✅ Completada

#### Descripción
Como usuario, quiero que el balance semanal incluya mis membresías y comience desde el día que agregué mi primer registro, para tener un análisis más preciso y personalizado de mi situación financiera.

#### Criterios de Aceptación
- ✅ Las membresías agregadas durante la semana aparecen en el balance semanal
- ✅ La semana comienza desde el día del primer registro (movimiento o membresía)
- ✅ Los días de la semana se generan dinámicamente según la fecha de inicio
- ✅ El cálculo de totales incluye membresías mensuales
- ✅ La comparación con la semana anterior funciona correctamente

#### Tareas Realizadas
1. Creación del método `obtenerFechaPrimerRegistro()` en `DatabaseHelper`
2. Creación del método `obtenerTotalMembresiasMensualPorRango()` en `DatabaseHelper`
3. Modificación de `WeeklyBalanceActivity.kt` para calcular desde el primer registro
4. Implementación de generación dinámica de etiquetas de días
5. Inclusión de membresías en el cálculo del balance semanal total

#### Archivos Modificados/Creados
- `app/src/main/java/mx/itson/controldegastos/WeeklyBalanceActivity.kt`
- `app/src/main/java/mx/itson/controldegastos/database/DatabaseHelper.kt`

---

### HU-25: Sistema de Recomendaciones Financieras

**Prioridad:** Media  
**Puntos de Historia:** 5  
**Estado:** ✅ Completada

#### Descripción
Como usuario, quiero recibir recomendaciones sobre qué hacer con mi dinero cuando tengo un balance positivo, para tomar mejores decisiones financieras y hacer crecer mis ahorros.

#### Criterios de Aceptación
- ✅ Nueva actividad `RecommendationsActivity` creada
- ✅ Se muestra un diálogo cuando el balance es negativo o cero
- ✅ Se muestran 4 recomendaciones aleatorias cuando hay balance positivo
- ✅ Las recomendaciones incluyen consejos sobre inversión, ahorro y planificación financiera
- ✅ El balance disponible se muestra en la parte superior
- ✅ Navegación desde la pantalla principal mediante botón

#### Tareas Realizadas
1. Creación de `RecommendationsActivity.kt`
2. Diseño de `activity_recommendations.xml`
3. Creación de `item_recommendation.xml` para tarjetas de recomendaciones
4. Creación de `dialog_recommendation.xml` para diálogo informativo
5. Implementación de catálogo de 8 recomendaciones financieras
6. Sistema de selección aleatoria de recomendaciones
7. Integración del botón en `MainActivity`

#### Archivos Modificados/Creados
- `app/src/main/java/mx/itson/controldegastos/RecommendationsActivity.kt` - Nueva actividad
- `app/src/main/res/layout/activity_recommendations.xml` - Nuevo layout
- `app/src/main/res/layout/item_recommendation.xml` - Nuevo layout
- `app/src/main/res/layout/dialog_recommendation.xml` - Nuevo layout
- `app/src/main/java/mx/itson/controldegastos/MainActivity.kt` - Integración
- `app/src/main/res/layout/activity_main.xml` - Botón de navegación
- `app/src/main/res/values/strings.xml` - Strings de recomendaciones
- `app/src/main/AndroidManifest.xml` - Registro de actividad

---

### HU-26: Personalización de Logo de la Aplicación

**Prioridad:** Baja  
**Puntos de Historia:** 2  
**Estado:** ✅ Completada

#### Descripción
Como desarrollador, quiero personalizar el logo de la aplicación para que refleje la identidad visual del proyecto.

#### Criterios de Aceptación
- ✅ El logo predeterminado de Android Studio fue reemplazado
- ✅ Se utiliza la imagen `logo_app` del directorio `drawable`
- ✅ El logo se muestra correctamente en el launcher de la aplicación
- ✅ El logo funciona tanto en iconos adaptativos como redondos

#### Tareas Realizadas
1. Modificación de `ic_launcher.xml` para usar `logo_app` como foreground
2. Modificación de `ic_launcher_round.xml` para usar `logo_app` como foreground
3. Configuración de monochrome drawable para iconos adaptativos

#### Archivos Modificados/Creados
- `app/src/main/res/mipmap-anydpi/ic_launcher.xml` - Configuración de icono adaptativo
- `app/src/main/res/mipmap-anydpi/ic_launcher_round.xml` - Configuración de icono redondo

---

### HU-27: Mejora del Diseño Visual de Membresías

**Prioridad:** Media  
**Puntos de Historia:** 3  
**Estado:** ✅ Completada

#### Descripción
Como usuario, quiero que las imágenes de las membresías se muestren como iconos en la esquina superior derecha en lugar de fondo, para mejorar la estética y legibilidad.

#### Criterios de Aceptación
- ✅ Las imágenes de membresías se muestran como iconos en lugar de fondo
- ✅ Los iconos están posicionados en la esquina superior derecha
- ✅ Los iconos tienen un tamaño adecuado (100dp x 100dp)
- ✅ El diseño funciona correctamente en `item_membership.xml`
- ✅ El diseño funciona correctamente en `item_membership_catalog.xml`
- ✅ El diseño funciona correctamente en `activity_membership.xml`

#### Tareas Realizadas
1. Modificación de `item_membership.xml` para cambiar `ShapeableImageView` a icono
2. Modificación de `item_membership_catalog.xml` para cambiar `ShapeableImageView` a icono
3. Ajuste de constraints y márgenes para posicionar iconos en esquina superior derecha
4. Actualización de `MembershipAdapter.kt` para remover alpha de imagen
5. Actualización de `MembershipCatalogAdapter.kt` para remover alpha de imagen
6. Actualización de colores de texto para usar atributos de tema

#### Archivos Modificados/Creados
- `app/src/main/res/layout/item_membership.xml` - Cambio de diseño
- `app/src/main/res/layout/item_membership_catalog.xml` - Cambio de diseño
- `app/src/main/java/mx/itson/controldegastos/adapter/MembershipAdapter.kt` - Remoción de alpha
- `app/src/main/java/mx/itson/controldegastos/adapter/MembershipCatalogAdapter.kt` - Remoción de alpha

---

### HU-28: Restricción de Edición de Membresías en Movimientos

**Prioridad:** Media  
**Puntos de Historia:** 5  
**Estado:** ✅ Completada

#### Descripción
Como usuario, quiero que las membresías no se puedan editar desde la lista de movimientos recientes, pero sí se puedan eliminar, y que la eliminación se refleje en todas las partes de la aplicación.

#### Criterios de Aceptación
- ✅ El botón de editar está oculto para movimientos de categoría "Membresías"
- ✅ Las membresías se pueden eliminar desde movimientos recientes
- ✅ Al eliminar una membresía, se actualiza el balance en `MainActivity`
- ✅ Al eliminar una membresía, se actualiza la sección de membresías
- ✅ Al eliminar una membresía, se actualiza la lista de movimientos recientes
- ✅ Se muestra mensaje apropiado al intentar editar una membresía

#### Tareas Realizadas
1. Modificación de `MovimientoAdapter.kt` para ocultar botón editar en membresías
2. Implementación de lógica en `MovementsActivity.kt` para prevenir edición de membresías
3. Implementación de eliminación de membresías desde movimientos
4. Actualización de `MainActivity` para usar `startActivityForResult` con `MovementsActivity`
5. Actualización de `MainActivity` para usar `startActivityForResult` con `MembershipsActivity`
6. Implementación de `onActivityResult` en `MainActivity` para actualizar totales

#### Archivos Modificados/Creados
- `app/src/main/java/mx/itson/controldegastos/adapter/MovimientoAdapter.kt` - Ocultar botón editar
- `app/src/main/java/mx/itson/controldegastos/MovementsActivity.kt` - Lógica de edición/eliminación
- `app/src/main/java/mx/itson/controldegastos/MainActivity.kt` - Manejo de resultados
- `app/src/main/java/mx/itson/controldegastos/MembershipsActivity.kt` - Setear RESULT_OK

---

### HU-29: Sistema de Configuración de Gastos Hormiga (Fase 1)

**Prioridad:** Alta  
**Puntos de Historia:** 13  
**Estado:** ✅ Completada

#### Descripción
Como usuario, quiero poder personalizar los límites de detección de gastos hormiga, configurar alertas basadas en montos acumulados y porcentaje de ingresos, para tener un control más preciso sobre mis gastos pequeños.

#### Criterios de Aceptación
- ✅ Se creó `GastosHormigaHelper.kt` para gestionar límites personalizables
- ✅ Se pueden configurar límites por categoría de gastos hormiga
- ✅ Se pueden configurar umbrales de alertas (cantidad diaria/semanal, monto diario/semanal)
- ✅ Se puede configurar el porcentaje de ingresos para alertas
- ✅ Se puede restaurar los límites predeterminados
- ✅ La detección de gastos hormiga usa los límites personalizados
- ✅ Las alertas se muestran basadas en montos acumulados
- ✅ Las alertas se muestran basadas en porcentaje de ingresos mensuales
- ✅ Se creó `SettingsActivity` con interfaz de configuración
- ✅ Las notificaciones de `GastosHormigaWorker` usan límites personalizados

#### Tareas Realizadas
1. Creación de `GastosHormigaHelper.kt` con métodos para gestionar límites
2. Implementación de almacenamiento de límites en `SharedPreferences`
3. Modificación de `DatabaseHelper.kt` para usar `GastosHormigaHelper`
4. Actualización de `MainActivity.kt` para usar límites personalizados en detección
5. Actualización de `MainActivity.kt` para mostrar alertas por monto acumulado
6. Actualización de `MainActivity.kt` para mostrar alertas por porcentaje de ingresos
7. Actualización de `GastosHormigaWorker.kt` para usar límites personalizados
8. Creación de `SettingsActivity.kt` con interfaz de configuración
9. Diseño de `activity_settings.xml` con botones de configuración
10. Implementación de diálogos para configurar límites por categoría
11. Implementación de diálogos para configurar umbrales de alertas
12. Implementación de diálogo para restaurar valores predeterminados
13. Agregado método `obtenerMontoTotalGastosHormiga()` en `DatabaseHelper`

#### Archivos Modificados/Creados
- `app/src/main/java/mx/itson/controldegastos/util/GastosHormigaHelper.kt` - Nueva clase helper
- `app/src/main/java/mx/itson/controldegastos/SettingsActivity.kt` - Nueva actividad
- `app/src/main/res/layout/activity_settings.xml` - Nuevo layout
- `app/src/main/java/mx/itson/controldegastos/MainActivity.kt` - Lógica de detección mejorada
- `app/src/main/java/mx/itson/controldegastos/database/DatabaseHelper.kt` - Integración con helper
- `app/src/main/java/mx/itson/controldegastos/service/GastosHormigaWorker.kt` - Uso de límites personalizados
- `app/src/main/AndroidManifest.xml` - Registro de SettingsActivity

---

### HU-30: Mejoras en Modo Oscuro y Accesibilidad

**Prioridad:** Media  
**Puntos de Historia:** 8  
**Estado:** ✅ Completada

#### Descripción
Como usuario, quiero que todos los textos y elementos de la interfaz sean legibles tanto en modo claro como en modo oscuro, para tener una experiencia visual consistente.

#### Criterios de Aceptación
- ✅ Los textos en `AddMovementActivity` son legibles en modo oscuro
- ✅ Los textos en spinners de `AddMovementActivity` son legibles en ambos modos
- ✅ Los textos en el filtro de `MovementsActivity` son legibles en ambos modos
- ✅ Los textos en el `SearchView` de `MovementsActivity` son legibles en ambos modos
- ✅ Los textos en `SearchView` tienen buen contraste en modo claro
- ✅ Se utilizan atributos de tema (`?attr/colorOnSurface`) donde es posible
- ✅ Se utilizan colores de recursos (`R.color.on_surface`) donde es necesario
- ✅ Los colores se adaptan dinámicamente según el modo activo

#### Tareas Realizadas
1. Actualización de `activity_add_movement.xml` para usar atributos de tema
2. Modificación de `AddMovementActivity.kt` para crear adapters personalizados de spinners
3. Modificación de `AddMovementActivity.kt` para usar `R.color.on_surface` en adapters
4. Modificación de `MovementsActivity.kt` para crear adapter personalizado del filtro
5. Configuración extensa de `SearchView` en `MovementsActivity.kt` para colores dinámicos
6. Actualización de layouts para usar `?attr/colorOnSurface` en TextViews
7. Configuración de colores de texto, hint e iconos del SearchView según modo

#### Archivos Modificados/Creados
- `app/src/main/res/layout/activity_add_movement.xml` - Atributos de tema
- `app/src/main/java/mx/itson/controldegastos/AddMovementActivity.kt` - Adapters personalizados
- `app/src/main/java/mx/itson/controldegastos/MovementsActivity.kt` - Configuración de SearchView
- `app/src/main/res/layout/item_membership.xml` - Atributos de tema
- `app/src/main/res/layout/item_membership_catalog.xml` - Atributos de tema

---

### HU-31: Ajuste del Toolbar para Compatibilidad con Barra de Estado

**Prioridad:** Baja  
**Puntos de Historia:** 5  
**Estado:** ✅ Completada

#### Descripción
Como usuario, quiero que el toolbar se extienda hasta la parte superior de la pantalla pero que su contenido esté posicionado correctamente para no interferir con la barra de estado del sistema.

#### Criterios de Aceptación
- ✅ El color del toolbar se extiende hasta la parte superior de la pantalla
- ✅ El contenido del toolbar (texto e iconos) está posicionado correctamente
- ✅ No hay solapamiento entre el toolbar y la barra de estado del sistema
- ✅ La configuración se aplica a todas las actividades con toolbar
- ✅ El color de la barra de estado coincide con el toolbar

#### Tareas Realizadas
1. Actualización de `themes.xml` para configurar `android:statusBarColor`
2. Actualización de `themes.xml` para configurar `android:windowLightStatusBar`
3. Agregado de `android:fitsSystemWindows="true"` en todos los `AppBarLayout`
4. Agregado de `android:minHeight="?attr/actionBarSize"` en todos los `MaterialToolbar`
5. Configuración de `window.statusBarColor` en todas las actividades
6. Implementación de `WindowInsets` en `MovementsActivity` para manejo avanzado

#### Archivos Modificados/Creados
- `app/src/main/res/values/themes.xml` - Configuración de status bar
- `app/src/main/res/layout/activity_main.xml` - Ajustes de toolbar
- `app/src/main/res/layout/activity_settings.xml` - Ajustes de toolbar
- `app/src/main/res/layout/activity_add_movement.xml` - Ajustes de toolbar
- `app/src/main/res/layout/activity_movements.xml` - Ajustes de toolbar
- `app/src/main/res/layout/activity_memberships.xml` - Ajustes de toolbar
- `app/src/main/res/layout/activity_graph.xml` - Ajustes de toolbar
- `app/src/main/res/layout/activity_weekly_balance.xml` - Ajustes de toolbar
- `app/src/main/res/layout/activity_recommendations.xml` - Ajustes de toolbar
- `app/src/main/res/layout/activity_membership_catalog.xml` - Ajustes de toolbar
- `app/src/main/res/layout/activity_add_membership.xml` - Ajustes de toolbar
- `app/src/main/java/mx/itson/controldegastos/MainActivity.kt` - Configuración de status bar
- `app/src/main/java/mx/itson/controldegastos/SettingsActivity.kt` - Configuración de status bar
- `app/src/main/java/mx/itson/controldegastos/AddMovementActivity.kt` - Configuración de status bar
- `app/src/main/java/mx/itson/controldegastos/MovementsActivity.kt` - Configuración de status bar y WindowInsets
- `app/src/main/java/mx/itson/controldegastos/MembershipsActivity.kt` - Configuración de status bar
- `app/src/main/java/mx/itson/controldegastos/GraphActivity.kt` - Configuración de status bar
- `app/src/main/java/mx/itson/controldegastos/WeeklyBalanceActivity.kt` - Configuración de status bar
- `app/src/main/java/mx/itson/controldegastos/RecommendationsActivity.kt` - Configuración de status bar
- `app/src/main/java/mx/itson/controldegastos/MembershipCatalogActivity.kt` - Configuración de status bar
- `app/src/main/java/mx/itson/controldegastos/EditMembershipActivity.kt` - Configuración de status bar
- `app/src/main/java/mx/itson/controldegastos/EditMovementActivity.kt` - Configuración de status bar

---

## Resumen de Cambios Técnicos

### Base de Datos
- **Nuevos métodos agregados:**
  - `obtenerFechaPrimerRegistro()` - Obtiene la fecha más antigua de movimientos o membresías
  - `obtenerTotalMembresiasMensualPorRango()` - Calcula el total de membresías mensuales en un rango
  - `obtenerMontoTotalGastosHormiga()` - Obtiene el monto total acumulado de gastos hormiga

### Nuevas Clases y Utilidades
- `GastosHormigaHelper` - Clase helper para gestionar límites personalizables de gastos hormiga
- `SettingsActivity` - Actividad para configuración de la aplicación
- `RecommendationsActivity` - Actividad para mostrar recomendaciones financieras

### Nuevos Layouts
- `activity_settings.xml` - Layout de configuración
- `activity_recommendations.xml` - Layout de recomendaciones
- `item_recommendation.xml` - Layout de tarjeta de recomendación
- `dialog_recommendation.xml` - Layout de diálogo informativo

### Mejoras en UI/UX
- Iconos de membresías reposicionados como elementos visuales en lugar de fondo
- Modo oscuro completamente funcional con textos legibles
- Toolbar ajustado para compatibilidad con barra de estado del sistema
- Personalización completa de límites de gastos hormiga
- Sistema de recomendaciones financieras

### Dependencias Utilizadas
- `androidx.core:core-ktx` - Para WindowInsets y compatibilidad
- `androidx.preference:preference-ktx` - Para SharedPreferences (ya existente)
- `androidx.work:work-runtime-ktx` - Para WorkManager (ya existente)

---

## Métricas del Sprint

### Velocidad
- **Historias completadas:** 8/8 (100%)
- **Puntos de historia completados:** [Total estimado]

### Calidad
- **Errores de compilación:** 0
- **Errores de lint:** 0
- **Cobertura de pruebas:** [Si aplica]

### Técnicas
- **Archivos modificados:** ~35
- **Archivos nuevos:** ~8
- **Líneas de código agregadas:** ~1500+

---

## Impedimentos y Soluciones

### Impedimentos Encontrados
1. **Error de compilación: "Unresolved reference 'context'" en DatabaseHelper.kt**
   - **Problema:** El parámetro `context` del constructor no estaba disponible en el método `esGastoHormiga()`
   - **Solución:** Se almacenó `context.applicationContext` como propiedad privada `appContext` y se utilizó en el método

2. **Error: Activity class SettingsActivity does not exist**
   - **Problema:** La actividad no estaba registrada en `AndroidManifest.xml`
   - **Solución:** Se agregó la declaración de `<activity>` en el manifest

3. **Error: resource attr/colorBackground not found**
   - **Problema:** Se intentó usar `?attr/colorBackground` que no existe
   - **Solución:** Se reemplazó con `@color/background` que se maneja automáticamente por el sistema de temas

4. **Textos negros en modo oscuro**
   - **Problema:** Varios componentes no se adaptaban al modo oscuro
   - **Solución:** Implementación de adapters personalizados y configuración dinámica de colores basada en el modo activo

5. **Toolbar solapándose con barra de estado**
   - **Problema:** El contenido del toolbar interfería con la barra de estado del sistema
   - **Solución:** Configuración de `fitsSystemWindows`, `statusBarColor` y manejo de `WindowInsets`

### Lecciones Aprendidas
- El uso de `SharedPreferences` con helpers facilita la gestión de configuraciones personalizables
- Los adapters personalizados son necesarios para componentes como Spinner y SearchView en modo oscuro
- La configuración de `WindowInsets` y `fitsSystemWindows` es crucial para una UI moderna
- La separación de responsabilidades con helpers mejora la mantenibilidad del código

---

## Retrospectiva del Sprint

### ¿Qué salió bien?
- ✅ Implementación completa de todas las historias de usuario planificadas
- ✅ Sistema de configuración de gastos hormiga completamente funcional
- ✅ Mejoras significativas en la experiencia de usuario (modo oscuro, toolbar)
- ✅ Integración exitosa de membresías en diferentes secciones
- ✅ Código limpio y bien estructurado con helpers y utilidades

### ¿Qué se puede mejorar?
- Considerar agregar validaciones adicionales en los diálogos de configuración
- Implementar pruebas unitarias para `GastosHormigaHelper`
- Documentar mejor los métodos complejos en las actividades
- Considerar agregar animaciones en transiciones entre pantallas

### Próximos Pasos
- Revisión de código por pares
- Pruebas de integración
- Considerar Fase 2 del sistema de gastos hormiga (análisis avanzado, reportes)
- Preparación para Sprint 5 (si aplica)

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
- ✅ Compatibilidad con modo claro y oscuro
- ✅ UI consistente con Material Design

---

## Notas Adicionales

- Todas las funcionalidades están integradas y funcionando correctamente
- El sistema de configuración de gastos hormiga permite personalización completa
- La aplicación mantiene compatibilidad con datos existentes
- Se mantiene el diseño Material Design consistente en toda la aplicación
- El modo oscuro está completamente funcional en todas las pantallas
- El toolbar está correctamente configurado en todas las actividades

---

**Fecha de creación:** [Fecha actual]  
**Equipo de Desarrollo:** [Nombre del equipo]  
**Scrum Master:** [Nombre]  
**Product Owner:** [Nombre]

