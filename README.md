# 📱 Control de Gastos

Aplicación Android para llevar un control eficiente de tus ingresos y gastos personales.

## 🎯 Características

### Sprint 1
- ✅ **Base de datos SQLite** para persistencia local de movimientos
- ✅ **Pantalla principal** con visualización de totales (Ingresos, Gastos, Balance)
- ✅ **Diseño moderno** con Material Design 3
- ✅ **Agregar movimientos** con validación de campos

### Sprint 2
- ✅ **Lista de movimientos** con RecyclerView ordenada por fecha
- ✅ **Filtrado por tipo** (Todos, Ingresos, Gastos)
- ✅ **Detección de gastos hormiga** con alertas personalizadas
- ✅ **Gráficos** con porcentaje de gastos hormiga

## 📋 Funcionalidades Detalladas

### Gestión de Movimientos
- Registrar ingresos y gastos con descripción y monto
- Visualizar historial completo de movimientos
- Filtrar movimientos por tipo
- Ver totales actualizados en tiempo real

### Gastos Hormiga
- Alerta automática cuando se detectan 3+ gastos pequeños (<$100) en el día
- Alerta cuando se detectan 10+ gastos pequeños en la semana
- Diálogo personalizado con imagen y mensaje informativo
- Porcentaje de gastos hormiga en la pantalla de gráficos

### Visualización
- Gráficos de barras comparando Ingresos vs Gastos
- Tarjetas informativas con Material Design
- Interfaz intuitiva y moderna

## 🛠️ Tecnologías Utilizadas

- **Lenguaje**: Kotlin
- **Base de Datos**: SQLite con SQLiteOpenHelper
- **UI**: Material Design Components
- **Gráficos**: MPAndroidChart
- **Arquitectura**: MVVM (Model-View-ViewModel pattern)
- **Mínimo SDK**: Android 11 (API 30)
- **Target SDK**: Android 14 (API 36)

## 📦 Estructura del Proyecto

```
app/
├── src/
│   ├── main/
│   │   ├── java/mx/itson/controldegastos/
│   │   │   ├── MainActivity.kt              # Pantalla principal
│   │   │   ├── AddMovementActivity.kt       # Formulario de movimientos
│   │   │   ├── GraphActivity.kt             # Pantalla de gráficos
│   │   │   ├── adapter/
│   │   │   │   └── MovimientoAdapter.kt     # Adapter para RecyclerView
│   │   │   ├── database/
│   │   │   │   └── DatabaseHelper.kt        # Gestión de SQLite
│   │   │   ├── model/
│   │   │   │   └── Movimiento.kt            # Modelo de datos
│   │   │   └── service/
│   │   │       └── GastosHormigaWorker.kt   # Worker para detección
│   │   └── res/
│   │       ├── layout/                       # Layouts XML
│   │       ├── values/                       # Strings, colors, themes
│   │       └── drawable/                     # Imágenes y recursos
│   └── test/                                 # Pruebas unitarias
```

## 🚀 Instalación

1. Clona el repositorio:
```bash
git clone https://github.com/Richiee2117/ControldeGastos.git
```

2. Abre el proyecto en Android Studio

3. Sincroniza las dependencias de Gradle

4. Ejecuta la aplicación en un emulador o dispositivo físico

## 📱 Requisitos

- Android Studio Hedgehog o superior
- Android SDK 30 o superior
- Gradle 8.0 o superior

## 🧪 Pruebas

Para probar las funcionalidades:

### Gastos Hormiga
1. Agrega 3+ gastos menores a $100 en el mismo día
2. Verifica que aparezca el diálogo de alerta
3. Agrega más gastos pequeños y verifica que siga apareciendo

### Filtros
1. Agrega movimientos de diferentes tipos
2. Usa el Spinner para filtrar por tipo
3. Verifica que la lista se actualice correctamente

### Gráficos
1. Agrega varios movimientos
2. Ve a la pantalla de Gráficos
3. Verifica que se muestren los totales y porcentajes

## 📊 Métricas del Proyecto

- **Sprint 1**: 4 historias de usuario completadas
- **Sprint 2**: 4 historias de usuario completadas
- **Total**: 8 historias de usuario implementadas

## 📝 Licencia

Este proyecto es de código abierto y está disponible bajo la licencia MIT.

## 👥 Autor

- **Ricardo Rodríguez** - [Richiee2117](https://github.com/Richiee2117)

## 🔮 Próximas Funcionalidades

- [ ] Edición y eliminación de movimientos
- [ ] Categorías de gastos
- [ ] Exportación de datos (CSV, PDF)
- [ ] Gráficos avanzados (líneas, pie charts)
- [ ] Modo oscuro
- [ ] Sincronización en la nube

---

⭐ Si te gusta este proyecto, no olvides darle una estrella en GitHub!

