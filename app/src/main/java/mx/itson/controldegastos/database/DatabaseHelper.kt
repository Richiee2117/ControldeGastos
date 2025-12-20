package mx.itson.controldegastos.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import mx.itson.controldegastos.model.Movimiento
import mx.itson.controldegastos.model.Membership
import mx.itson.controldegastos.util.GastosHormigaHelper
import mx.itson.controldegastos.util.MembershipHelper
import java.util.Calendar

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val appContext: Context = context.applicationContext

    companion object {
        private const val DATABASE_NAME = "ControlGastos.db"
        private const val DATABASE_VERSION = 4
        private const val TABLE_MOVIMIENTOS = "movimientos"
        private const val TABLE_CATEGORIAS = "categorias"
        private const val TABLE_MEMBRESIAS = "membresias"
        private const val COLUMN_ID = "id"
        private const val COLUMN_MONTO = "monto"
        private const val COLUMN_DESCRIPCION = "descripcion"
        private const val COLUMN_TIPO = "tipo"
        private const val COLUMN_CATEGORIA = "categoria"
        private const val COLUMN_FECHA = "fecha"
        private const val COLUMN_NOMBRE = "nombre"
        private const val COLUMN_FRECUENCIA = "frecuencia"
        private const val COLUMN_FECHA_PAGO = "fecha_pago"
        private const val COLUMN_PROXIMO_VENCIMIENTO = "proximo_vencimiento"
        private const val COLUMN_ACTIVA = "activa"
        private const val COLUMN_RENOVACION_AUTOMATICA = "renovacion_automatica"
        private const val COLUMN_RECIBIR_NOTIFICACIONES = "recibir_notificaciones"

        private val CATEGORIAS_PREDETERMINADAS = listOf(
            "Comida",
            "Transporte",
            "Entretenimiento",
            "Salud",
            "Membresías",
            "Snacks",
            "Educación",
            "Otros"
        )

        // Los límites ahora se obtienen de GastosHormigaHelper con soporte para personalización
        // Mantenemos esta constante solo para referencia, pero no se usa directamente
        @Deprecated("Usar GastosHormigaHelper.obtenerLimiteCategoria() en su lugar")
        private val LIMITES_GASTOS_HORMIGA = mapOf(
            "Comida" to 120.0,
            "Transporte" to 80.0,
            "Entretenimiento" to 150.0,
            "Salud" to 120.0,
            "Snacks" to 70.0,
            "Educación" to 150.0
        )
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Crear tabla movimientos
        val createTableMovimientos = """
            CREATE TABLE $TABLE_MOVIMIENTOS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_MONTO REAL NOT NULL,
                $COLUMN_DESCRIPCION TEXT NOT NULL,
                $COLUMN_TIPO TEXT NOT NULL,
                $COLUMN_CATEGORIA TEXT NOT NULL DEFAULT 'Otros',
                $COLUMN_FECHA INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTableMovimientos)

        // Crear tabla categorías
        val createTableCategorias = """
            CREATE TABLE $TABLE_CATEGORIAS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOMBRE TEXT NOT NULL UNIQUE
            )
        """.trimIndent()
        db.execSQL(createTableCategorias)

        // Crear tabla membresías
        val createTableMembresias = """
            CREATE TABLE $TABLE_MEMBRESIAS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOMBRE TEXT NOT NULL,
                $COLUMN_TIPO TEXT NOT NULL,
                $COLUMN_MONTO REAL NOT NULL,
                $COLUMN_FRECUENCIA TEXT NOT NULL,
                $COLUMN_FECHA_PAGO INTEGER NOT NULL,
                $COLUMN_PROXIMO_VENCIMIENTO INTEGER NOT NULL,
                $COLUMN_DESCRIPCION TEXT,
                $COLUMN_ACTIVA INTEGER NOT NULL DEFAULT 1,
                $COLUMN_RENOVACION_AUTOMATICA INTEGER NOT NULL DEFAULT 0,
                $COLUMN_RECIBIR_NOTIFICACIONES INTEGER NOT NULL DEFAULT 1
            )
        """.trimIndent()
        db.execSQL(createTableMembresias)

        // Insertar categorías iniciales
        insertarCategoriasIniciales(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        when (oldVersion) {
            1 -> {
                // Agregar columna categoria si no existe
                try {
                    db.execSQL("ALTER TABLE $TABLE_MOVIMIENTOS ADD COLUMN $COLUMN_CATEGORIA TEXT NOT NULL DEFAULT 'Otros'")
                } catch (e: Exception) {
                    // Columna ya existe o error
                }

                // Crear tabla categorías
                val createTableCategorias = """
                    CREATE TABLE IF NOT EXISTS $TABLE_CATEGORIAS (
                        $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                        $COLUMN_NOMBRE TEXT NOT NULL UNIQUE
                    )
                """.trimIndent()
                db.execSQL(createTableCategorias)

                // Insertar categorías iniciales
                insertarCategoriasIniciales(db)

                // Continuar con migración a versión 2
                if (newVersion > 2) {
                    upgradeVersion2(db)
                }
                // Continuar con migración a versión 3
                if (newVersion > 3) {
                    upgradeVersion3(db)
                }
                // Continuar con migración a versión 4
                if (newVersion >= 4) {
                    upgradeVersion4(db)
                }
            }
            2 -> {
                upgradeVersion2(db)
                // Continuar con migración a versión 3
                if (newVersion >= 3) {
                    upgradeVersion3(db)
                }
                // Continuar con migración a versión 4
                if (newVersion >= 4) {
                    upgradeVersion4(db)
                }
            }
            3 -> {
                // Migrar directamente a versión 4
                upgradeVersion4(db)
            }
        }
    }

    private fun upgradeVersion2(db: SQLiteDatabase) {
        // Crear tabla membresías
        val createTableMembresias = """
            CREATE TABLE IF NOT EXISTS $TABLE_MEMBRESIAS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOMBRE TEXT NOT NULL,
                $COLUMN_TIPO TEXT NOT NULL,
                $COLUMN_MONTO REAL NOT NULL,
                $COLUMN_FRECUENCIA TEXT NOT NULL,
                $COLUMN_FECHA_PAGO INTEGER NOT NULL,
                $COLUMN_PROXIMO_VENCIMIENTO INTEGER NOT NULL,
                $COLUMN_DESCRIPCION TEXT,
                $COLUMN_ACTIVA INTEGER NOT NULL DEFAULT 1
            )
        """.trimIndent()
        db.execSQL(createTableMembresias)

        insertarCategoriasIniciales(db)
    }
    
    private fun upgradeVersion3(db: SQLiteDatabase) {
        // Versión 3: No hay cambios específicos, solo mantener compatibilidad
        // Esta función está aquí para mantener la estructura de migración
    }
    
    private fun upgradeVersion4(db: SQLiteDatabase) {
        // Agregar columnas para renovación automática y notificaciones
        // Primero verificar si las columnas ya existen
        val cursor = db.rawQuery("PRAGMA table_info($TABLE_MEMBRESIAS)", null)
        val columnNames = mutableListOf<String>()
        while (cursor.moveToNext()) {
            val columnName = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            columnNames.add(columnName)
        }
        cursor.close()
        
        // Agregar renovacion_automatica si no existe
        if (!columnNames.contains(COLUMN_RENOVACION_AUTOMATICA)) {
            try {
                db.execSQL("ALTER TABLE $TABLE_MEMBRESIAS ADD COLUMN $COLUMN_RENOVACION_AUTOMATICA INTEGER NOT NULL DEFAULT 0")
            } catch (e: Exception) {
                android.util.Log.e("DatabaseHelper", "Error agregando columna renovacion_automatica: ${e.message}")
            }
        }
        
        // Agregar recibir_notificaciones si no existe
        if (!columnNames.contains(COLUMN_RECIBIR_NOTIFICACIONES)) {
            try {
                db.execSQL("ALTER TABLE $TABLE_MEMBRESIAS ADD COLUMN $COLUMN_RECIBIR_NOTIFICACIONES INTEGER NOT NULL DEFAULT 1")
            } catch (e: Exception) {
                android.util.Log.e("DatabaseHelper", "Error agregando columna recibir_notificaciones: ${e.message}")
            }
        }
    }

    private fun insertarCategoriasIniciales(db: SQLiteDatabase) {
        CATEGORIAS_PREDETERMINADAS.forEach { categoria ->
            val values = ContentValues().apply {
                put(COLUMN_NOMBRE, categoria)
            }
            db.insertWithOnConflict(TABLE_CATEGORIAS, null, values, SQLiteDatabase.CONFLICT_IGNORE)
        }
    }

    private fun asegurarCategoriasPredeterminadas() {
        val db = writableDatabase
        insertarCategoriasIniciales(db)
        db.close()
    }
    
    /**
     * Asegura que las columnas nuevas existan en la tabla membresías
     * Esto se ejecuta si la migración no se ejecutó correctamente
     */
    private fun asegurarColumnasMembresias() {
        val db = writableDatabase
        try {
            val cursor = db.rawQuery("PRAGMA table_info($TABLE_MEMBRESIAS)", null)
            val columnNames = mutableListOf<String>()
            while (cursor.moveToNext()) {
                val columnName = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                columnNames.add(columnName)
            }
            cursor.close()
            
            // Agregar renovacion_automatica si no existe
            if (!columnNames.contains(COLUMN_RENOVACION_AUTOMATICA)) {
                db.execSQL("ALTER TABLE $TABLE_MEMBRESIAS ADD COLUMN $COLUMN_RENOVACION_AUTOMATICA INTEGER NOT NULL DEFAULT 0")
            }
            
            // Agregar recibir_notificaciones si no existe
            if (!columnNames.contains(COLUMN_RECIBIR_NOTIFICACIONES)) {
                db.execSQL("ALTER TABLE $TABLE_MEMBRESIAS ADD COLUMN $COLUMN_RECIBIR_NOTIFICACIONES INTEGER NOT NULL DEFAULT 1")
            }
        } catch (e: Exception) {
            android.util.Log.e("DatabaseHelper", "Error asegurando columnas: ${e.message}", e)
        } finally {
            db.close()
        }
    }
    
    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        // Asegurar que las columnas nuevas existan cada vez que se abre la base de datos
        try {
            val cursor = db.rawQuery("PRAGMA table_info($TABLE_MEMBRESIAS)", null)
            val columnNames = mutableListOf<String>()
            while (cursor.moveToNext()) {
                val columnName = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                columnNames.add(columnName)
            }
            cursor.close()
            
            // Agregar renovacion_automatica si no existe
            if (!columnNames.contains(COLUMN_RENOVACION_AUTOMATICA)) {
                db.execSQL("ALTER TABLE $TABLE_MEMBRESIAS ADD COLUMN $COLUMN_RENOVACION_AUTOMATICA INTEGER NOT NULL DEFAULT 0")
            }
            
            // Agregar recibir_notificaciones si no existe
            if (!columnNames.contains(COLUMN_RECIBIR_NOTIFICACIONES)) {
                db.execSQL("ALTER TABLE $TABLE_MEMBRESIAS ADD COLUMN $COLUMN_RECIBIR_NOTIFICACIONES INTEGER NOT NULL DEFAULT 1")
            }
        } catch (e: Exception) {
            android.util.Log.e("DatabaseHelper", "Error asegurando columnas en onOpen: ${e.message}", e)
        }
    }

    fun esGastoHormiga(movimiento: Movimiento): Boolean {
        val limite = GastosHormigaHelper.obtenerLimiteCategoria(appContext, movimiento.categoria)
        if (limite < 0) return false // No hay límite definido para esta categoría
        return movimiento.monto <= limite
    }

    fun insertarMovimiento(movimiento: Movimiento): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_MONTO, movimiento.monto)
            put(COLUMN_DESCRIPCION, movimiento.descripcion)
            put(COLUMN_TIPO, movimiento.tipo)
            put(COLUMN_CATEGORIA, movimiento.categoria)
            put(COLUMN_FECHA, movimiento.fecha)
        }
        val id = db.insert(TABLE_MOVIMIENTOS, null, values)
        db.close()
        return id
    }

    fun obtenerCategorias(): List<String> {
        asegurarCategoriasPredeterminadas()
        val categorias = mutableListOf<String>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_CATEGORIAS,
            arrayOf(COLUMN_NOMBRE),
            null,
            null,
            null,
            null,
            COLUMN_NOMBRE
        )

        while (cursor.moveToNext()) {
            categorias.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)))
        }

        cursor.close()
        db.close()
        return categorias
    }

    fun obtenerTotalIngresos(): Double {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT SUM($COLUMN_MONTO) FROM $TABLE_MOVIMIENTOS WHERE $COLUMN_TIPO = ?",
            arrayOf("Ingreso")
        )
        var total = 0.0
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0)
        }
        cursor.close()
        db.close()
        return total
    }

    fun obtenerTotalGastos(): Double {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT SUM($COLUMN_MONTO) FROM $TABLE_MOVIMIENTOS WHERE $COLUMN_TIPO = ?",
            arrayOf("Gasto")
        )
        var total = 0.0
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0)
        }
        cursor.close()
        db.close()
        
        // Agregar membresías al total de gastos
        val totalMembresias = obtenerTotalMembresiasMensual()
        return total + totalMembresias
    }

    fun obtenerMovimientos(): List<Movimiento> {
        return obtenerMovimientosFiltrados(null)
    }

    fun obtenerMovimientosFiltrados(tipo: String?, categoria: String? = null): List<Movimiento> {
        val movimientos = mutableListOf<Movimiento>()
        val db = readableDatabase
        
        val condiciones = mutableListOf<String>()
        val argumentos = mutableListOf<String>()
        
        if (tipo != null) {
            condiciones.add("$COLUMN_TIPO = ?")
            argumentos.add(tipo)
        }
        if (categoria != null) {
            condiciones.add("$COLUMN_CATEGORIA = ?")
            argumentos.add(categoria)
        }
        
        val selection = if (condiciones.isNotEmpty()) condiciones.joinToString(" AND ") else null
        val selectionArgs = if (argumentos.isNotEmpty()) argumentos.toTypedArray() else null
        
        val cursor = db.query(
            TABLE_MOVIMIENTOS,
            null,
            selection,
            selectionArgs,
            null,
            null,
            "$COLUMN_FECHA DESC"
        )

        while (cursor.moveToNext()) {
            val movimiento = Movimiento(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                monto = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_MONTO)),
                descripcion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPCION)),
                tipo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIPO)),
                categoria = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORIA)),
                fecha = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_FECHA))
            )
            movimientos.add(movimiento)
        }

        cursor.close()
        db.close()
        return movimientos
    }

    fun obtenerGastosPorDia(diaInicio: Long, diaFin: Long): List<Movimiento> {
        val movimientos = mutableListOf<Movimiento>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            """
            SELECT * FROM $TABLE_MOVIMIENTOS 
            WHERE $COLUMN_TIPO = ? 
            AND $COLUMN_FECHA >= ? 
            AND $COLUMN_FECHA <= ?
            ORDER BY $COLUMN_FECHA DESC
            """.trimIndent(),
            arrayOf("Gasto", diaInicio.toString(), diaFin.toString())
        )

        while (cursor.moveToNext()) {
            val movimiento = Movimiento(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                monto = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_MONTO)),
                descripcion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPCION)),
                tipo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIPO)),
                categoria = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORIA)),
                fecha = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_FECHA))
            )
            movimientos.add(movimiento)
        }

        cursor.close()
        db.close()
        return movimientos
    }

    fun obtenerGastosHormigaPorDia(diaInicio: Long, diaFin: Long): Int {
        return obtenerGastosPorDia(diaInicio, diaFin).count { esGastoHormiga(it) }
    }

    fun obtenerTotalGastosHormiga(): Double {
        val gastos = obtenerMovimientosFiltrados("Gasto")
        return gastos.filter { esGastoHormiga(it) }.sumOf { it.monto }
    }

    fun obtenerGastosHormiga(inicio: Long, fin: Long): List<Movimiento> {
        return obtenerGastosPorDia(inicio, fin).filter { esGastoHormiga(it) }
    }
    
    /**
     * Obtiene el monto total acumulado de gastos hormiga en un rango de fechas
     */
    fun obtenerMontoTotalGastosHormiga(inicio: Long, fin: Long): Double {
        return obtenerGastosHormiga(inicio, fin).sumOf { it.monto }
    }

    fun obtenerMovimientoPorId(id: Long): Movimiento? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_MOVIMIENTOS,
            null,
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        var movimiento: Movimiento? = null
        if (cursor.moveToFirst()) {
            movimiento = Movimiento(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                monto = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_MONTO)),
                descripcion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPCION)),
                tipo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIPO)),
                categoria = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORIA)),
                fecha = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_FECHA))
            )
        }

        cursor.close()
        db.close()
        return movimiento
    }

    fun actualizarMovimiento(movimiento: Movimiento): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_MONTO, movimiento.monto)
            put(COLUMN_DESCRIPCION, movimiento.descripcion)
            put(COLUMN_TIPO, movimiento.tipo)
            put(COLUMN_CATEGORIA, movimiento.categoria)
            put(COLUMN_FECHA, movimiento.fecha)
        }
        val filasActualizadas = db.update(
            TABLE_MOVIMIENTOS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(movimiento.id.toString())
        )
        db.close()
        return filasActualizadas > 0
    }

    fun eliminarMovimiento(id: Long): Boolean {
        val db = writableDatabase
        val filasEliminadas = db.delete(
            TABLE_MOVIMIENTOS,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
        db.close()
        return filasEliminadas > 0
    }

    fun obtenerGastosPorCategoria(): Map<String, Double> {
        val gastosPorCategoria = mutableMapOf<String, Double>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            """
            SELECT $COLUMN_CATEGORIA, SUM($COLUMN_MONTO) as total 
            FROM $TABLE_MOVIMIENTOS 
            WHERE $COLUMN_TIPO = ?
            GROUP BY $COLUMN_CATEGORIA
            """.trimIndent(),
            arrayOf("Gasto")
        )

        while (cursor.moveToNext()) {
            val categoria = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORIA))
            val total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))
            gastosPorCategoria[categoria] = total
        }

        cursor.close()
        
        // Agregar membresías a la categoría correspondiente
        val membresias = obtenerMembresias().filter { it.activa }
        var totalMembresias = 0.0
        membresias.forEach { membresia ->
            val montoMensual = when (membresia.frecuencia) {
                "Mensual" -> membresia.monto
                "Trimestral" -> membresia.monto / 3
                "Semestral" -> membresia.monto / 6
                "Anual" -> membresia.monto / 12
                else -> membresia.monto
            }
            totalMembresias += montoMensual
        }
        
        if (totalMembresias > 0) {
            gastosPorCategoria["Membresías"] = (gastosPorCategoria["Membresías"] ?: 0.0) + totalMembresias
        }
        
        db.close()
        return gastosPorCategoria
    }

    fun buscarMovimientos(texto: String, tipo: String? = null): List<Movimiento> {
        val movimientos = mutableListOf<Movimiento>()
        val db = readableDatabase
        
        val condiciones = mutableListOf<String>()
        val argumentos = mutableListOf<String>()
        
        condiciones.add("$COLUMN_DESCRIPCION LIKE ?")
        argumentos.add("%$texto%")
        
        if (tipo != null) {
            condiciones.add("$COLUMN_TIPO = ?")
            argumentos.add(tipo)
        }
        
        val selection = condiciones.joinToString(" AND ")
        val selectionArgs = argumentos.toTypedArray()
        
        val cursor = db.query(
            TABLE_MOVIMIENTOS,
            null,
            selection,
            selectionArgs,
            null,
            null,
            "$COLUMN_FECHA DESC"
        )

        while (cursor.moveToNext()) {
            val movimiento = Movimiento(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                monto = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_MONTO)),
                descripcion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPCION)),
                tipo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIPO)),
                categoria = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORIA)),
                fecha = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_FECHA))
            )
            movimientos.add(movimiento)
        }

        cursor.close()
        db.close()
        return movimientos
    }

    fun obtenerTotalPorDia(diaInicio: Long, diaFin: Long, tipo: String): Double {
        val db = readableDatabase
        val cursor = db.rawQuery(
            """
            SELECT SUM($COLUMN_MONTO) FROM $TABLE_MOVIMIENTOS 
            WHERE $COLUMN_TIPO = ? 
            AND $COLUMN_FECHA >= ? 
            AND $COLUMN_FECHA <= ?
            """.trimIndent(),
            arrayOf(tipo, diaInicio.toString(), diaFin.toString())
        )
        
        var total = 0.0
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0)
        }
        
        cursor.close()
        db.close()
        return total
    }

    // ==================== MÉTODOS DE MEMBRESÍAS ====================

    fun insertarMembresia(membresia: Membership): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOMBRE, membresia.nombre)
            put(COLUMN_TIPO, membresia.tipo)
            put(COLUMN_MONTO, membresia.monto)
            put(COLUMN_FRECUENCIA, membresia.frecuencia)
            put(COLUMN_FECHA_PAGO, membresia.fechaPago)
            put(COLUMN_PROXIMO_VENCIMIENTO, membresia.proximoVencimiento)
            put(COLUMN_DESCRIPCION, membresia.descripcion)
            put(COLUMN_ACTIVA, if (membresia.activa) 1 else 0)
            put(COLUMN_RENOVACION_AUTOMATICA, if (membresia.renovacionAutomatica) 1 else 0)
            put(COLUMN_RECIBIR_NOTIFICACIONES, if (membresia.recibirNotificaciones) 1 else 0)
        }
        val id = db.insert(TABLE_MEMBRESIAS, null, values)
        db.close()
        return id
    }

    fun obtenerMembresias(): List<Membership> {
        val membresias = mutableListOf<Membership>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_MEMBRESIAS,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_PROXIMO_VENCIMIENTO ASC"
        )

        while (cursor.moveToNext()) {
            val membresia = Membership(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)),
                tipo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIPO)),
                monto = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_MONTO)),
                frecuencia = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FRECUENCIA)),
                fechaPago = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_FECHA_PAGO)),
                proximoVencimiento = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_PROXIMO_VENCIMIENTO)),
                descripcion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPCION)),
                activa = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ACTIVA)) == 1,
                renovacionAutomatica = try {
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RENOVACION_AUTOMATICA)) == 1
                } catch (e: Exception) {
                    false // Valor por defecto si la columna no existe
                },
                recibirNotificaciones = try {
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECIBIR_NOTIFICACIONES)) == 1
                } catch (e: Exception) {
                    true // Valor por defecto si la columna no existe
                }
            )
            membresias.add(membresia)
        }

        cursor.close()
        db.close()
        return membresias
    }

    fun obtenerMembresiaPorId(id: Long): Membership? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_MEMBRESIAS,
            null,
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        var membresia: Membership? = null
        if (cursor.moveToFirst()) {
            membresia = Membership(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)),
                tipo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIPO)),
                monto = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_MONTO)),
                frecuencia = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FRECUENCIA)),
                fechaPago = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_FECHA_PAGO)),
                proximoVencimiento = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_PROXIMO_VENCIMIENTO)),
                descripcion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPCION)),
                activa = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ACTIVA)) == 1,
                renovacionAutomatica = try {
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RENOVACION_AUTOMATICA)) == 1
                } catch (e: Exception) {
                    false
                },
                recibirNotificaciones = try {
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECIBIR_NOTIFICACIONES)) == 1
                } catch (e: Exception) {
                    true
                }
            )
        }

        cursor.close()
        db.close()
        return membresia
    }

    fun actualizarMembresia(membresia: Membership): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOMBRE, membresia.nombre)
            put(COLUMN_TIPO, membresia.tipo)
            put(COLUMN_MONTO, membresia.monto)
            put(COLUMN_FRECUENCIA, membresia.frecuencia)
            put(COLUMN_FECHA_PAGO, membresia.fechaPago)
            put(COLUMN_PROXIMO_VENCIMIENTO, membresia.proximoVencimiento)
            put(COLUMN_DESCRIPCION, membresia.descripcion)
            put(COLUMN_ACTIVA, if (membresia.activa) 1 else 0)
            put(COLUMN_RENOVACION_AUTOMATICA, if (membresia.renovacionAutomatica) 1 else 0)
            put(COLUMN_RECIBIR_NOTIFICACIONES, if (membresia.recibirNotificaciones) 1 else 0)
        }
        val filasActualizadas = db.update(
            TABLE_MEMBRESIAS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(membresia.id.toString())
        )
        db.close()
        return filasActualizadas > 0
    }

    fun eliminarMembresia(id: Long): Boolean {
        val db = writableDatabase
        val filasEliminadas = db.delete(
            TABLE_MEMBRESIAS,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
        db.close()
        return filasEliminadas > 0
    }

    fun obtenerMembresiasProximasAVencer(dias: Int = 3): List<Membership> {
        val membresias = mutableListOf<Membership>()
        val db = readableDatabase
        val ahora = System.currentTimeMillis()
        val limite = ahora + (dias * 24 * 60 * 60 * 1000L) // días en milisegundos

        val cursor = db.query(
            TABLE_MEMBRESIAS,
            null,
            "$COLUMN_PROXIMO_VENCIMIENTO >= ? AND $COLUMN_PROXIMO_VENCIMIENTO <= ? AND $COLUMN_ACTIVA = 1 AND $COLUMN_RECIBIR_NOTIFICACIONES = 1",
            arrayOf(ahora.toString(), limite.toString()),
            null,
            null,
            "$COLUMN_PROXIMO_VENCIMIENTO ASC"
        )

        while (cursor.moveToNext()) {
            val membresia = Membership(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)),
                tipo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIPO)),
                monto = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_MONTO)),
                frecuencia = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FRECUENCIA)),
                fechaPago = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_FECHA_PAGO)),
                proximoVencimiento = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_PROXIMO_VENCIMIENTO)),
                descripcion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPCION)),
                activa = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ACTIVA)) == 1,
                renovacionAutomatica = try {
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RENOVACION_AUTOMATICA)) == 1
                } catch (e: Exception) {
                    false // Valor por defecto si la columna no existe
                },
                recibirNotificaciones = try {
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECIBIR_NOTIFICACIONES)) == 1
                } catch (e: Exception) {
                    true // Valor por defecto si la columna no existe
                }
            )
            membresias.add(membresia)
        }

        cursor.close()
        db.close()
        return membresias
    }

    fun obtenerTotalMembresiasMensual(): Double {
        val membresias = obtenerMembresias().filter { it.activa }
        var total = 0.0
        
        membresias.forEach { membresia ->
            val montoMensual = when (membresia.frecuencia) {
                "Mensual" -> membresia.monto
                "Trimestral" -> membresia.monto / 3
                "Semestral" -> membresia.monto / 6
                "Anual" -> membresia.monto / 12
                else -> membresia.monto
            }
            total += montoMensual
        }
        
        return total
    }
    
    /**
     * Obtiene las membresías vencidas
     */
    fun obtenerMembresiasVencidas(): List<Membership> {
        val membresias = mutableListOf<Membership>()
        val db = readableDatabase
        val ahora = System.currentTimeMillis()

        val cursor = db.query(
            TABLE_MEMBRESIAS,
            null,
            "$COLUMN_PROXIMO_VENCIMIENTO < ? AND $COLUMN_ACTIVA = 1 AND $COLUMN_RENOVACION_AUTOMATICA = 1",
            arrayOf(ahora.toString()),
            null,
            null,
            "$COLUMN_PROXIMO_VENCIMIENTO ASC"
        )

        while (cursor.moveToNext()) {
            val membresia = Membership(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)),
                tipo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIPO)),
                monto = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_MONTO)),
                frecuencia = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FRECUENCIA)),
                fechaPago = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_FECHA_PAGO)),
                proximoVencimiento = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_PROXIMO_VENCIMIENTO)),
                descripcion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPCION)),
                activa = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ACTIVA)) == 1,
                renovacionAutomatica = try {
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RENOVACION_AUTOMATICA)) == 1
                } catch (e: Exception) {
                    false // Valor por defecto si la columna no existe
                },
                recibirNotificaciones = try {
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECIBIR_NOTIFICACIONES)) == 1
                } catch (e: Exception) {
                    true // Valor por defecto si la columna no existe
                }
            )
            membresias.add(membresia)
        }

        cursor.close()
        db.close()
        return membresias
    }
    
    /**
     * Renueva automáticamente una membresía vencida creando un gasto y actualizando fechas
     */
    fun renovarMembresiaAutomaticamente(membresia: Membership): Boolean {
        val db = writableDatabase
        
        try {
            db.beginTransaction()
            
            // Crear movimiento de gasto para la renovación
            val movimiento = Movimiento(
                monto = membresia.monto,
                descripcion = "Renovación automática: ${membresia.nombre}",
                tipo = "Gasto",
                categoria = "Membresías",
                fecha = System.currentTimeMillis()
            )
            
            val valuesMovimiento = ContentValues().apply {
                put(COLUMN_MONTO, movimiento.monto)
                put(COLUMN_DESCRIPCION, movimiento.descripcion)
                put(COLUMN_TIPO, movimiento.tipo)
                put(COLUMN_CATEGORIA, movimiento.categoria)
                put(COLUMN_FECHA, movimiento.fecha)
            }
            db.insert(TABLE_MOVIMIENTOS, null, valuesMovimiento)
            
            // Actualizar fecha de pago y próximo vencimiento
            val nuevaFechaPago = System.currentTimeMillis()
            val nuevoVencimiento = MembershipHelper.calcularProximoVencimiento(
                nuevaFechaPago,
                membresia.frecuencia
            )
            
            val valuesMembresia = ContentValues().apply {
                put(COLUMN_FECHA_PAGO, nuevaFechaPago)
                put(COLUMN_PROXIMO_VENCIMIENTO, nuevoVencimiento)
            }
            
            val filasActualizadas = db.update(
                TABLE_MEMBRESIAS,
                valuesMembresia,
                "$COLUMN_ID = ?",
                arrayOf(membresia.id.toString())
            )
            
            db.setTransactionSuccessful()
            return filasActualizadas > 0
        } catch (e: Exception) {
            return false
        } finally {
            db.endTransaction()
            db.close()
        }
    }
    
    /**
     * Obtiene la fecha del primer registro (movimiento o membresía)
     */
    fun obtenerFechaPrimerRegistro(): Long? {
        val db = readableDatabase
        
        // Obtener fecha del primer movimiento
        val cursorMovimientos = db.rawQuery(
            "SELECT MIN($COLUMN_FECHA) FROM $TABLE_MOVIMIENTOS",
            null
        )
        var fechaPrimerMovimiento: Long? = null
        if (cursorMovimientos.moveToFirst() && !cursorMovimientos.isNull(0)) {
            fechaPrimerMovimiento = cursorMovimientos.getLong(0)
        }
        cursorMovimientos.close()
        
        // Obtener fecha de la primera membresía
        val cursorMembresias = db.rawQuery(
            "SELECT MIN($COLUMN_FECHA_PAGO) FROM $TABLE_MEMBRESIAS",
            null
        )
        var fechaPrimeraMembresia: Long? = null
        if (cursorMembresias.moveToFirst() && !cursorMembresias.isNull(0)) {
            fechaPrimeraMembresia = cursorMembresias.getLong(0)
        }
        cursorMembresias.close()
        
        db.close()
        
        // Retornar la fecha más antigua
        return when {
            fechaPrimerMovimiento != null && fechaPrimeraMembresia != null -> 
                minOf(fechaPrimerMovimiento, fechaPrimeraMembresia)
            fechaPrimerMovimiento != null -> fechaPrimerMovimiento
            fechaPrimeraMembresia != null -> fechaPrimeraMembresia
            else -> null
        }
    }
    
    /**
     * Obtiene el total de membresías mensuales dentro de un rango de fechas
     */
    fun obtenerTotalMembresiasMensualPorRango(fechaInicio: Long, fechaFin: Long): Double {
        val membresias = obtenerMembresias().filter { 
            it.activa && it.fechaPago >= fechaInicio && it.fechaPago <= fechaFin 
        }
        var total = 0.0
        
        membresias.forEach { membresia ->
            val montoMensual = when (membresia.frecuencia) {
                "Mensual" -> membresia.monto
                "Trimestral" -> membresia.monto / 3
                "Semestral" -> membresia.monto / 6
                "Anual" -> membresia.monto / 12
                else -> membresia.monto
            }
            total += montoMensual
        }
        
        return total
    }
}

