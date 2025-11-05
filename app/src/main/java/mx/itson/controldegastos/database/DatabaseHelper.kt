package mx.itson.controldegastos.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import mx.itson.controldegastos.model.Movimiento

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ControlGastos.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_MOVIMIENTOS = "movimientos"
        private const val COLUMN_ID = "id"
        private const val COLUMN_MONTO = "monto"
        private const val COLUMN_DESCRIPCION = "descripcion"
        private const val COLUMN_TIPO = "tipo"
        private const val COLUMN_FECHA = "fecha"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_MOVIMIENTOS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_MONTO REAL NOT NULL,
                $COLUMN_DESCRIPCION TEXT NOT NULL,
                $COLUMN_TIPO TEXT NOT NULL,
                $COLUMN_FECHA INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MOVIMIENTOS")
        onCreate(db)
    }

    fun insertarMovimiento(movimiento: Movimiento): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_MONTO, movimiento.monto)
            put(COLUMN_DESCRIPCION, movimiento.descripcion)
            put(COLUMN_TIPO, movimiento.tipo)
            put(COLUMN_FECHA, movimiento.fecha)
        }
        val id = db.insert(TABLE_MOVIMIENTOS, null, values)
        db.close()
        return id
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
        return total
    }

    fun obtenerMovimientos(): List<Movimiento> {
        return obtenerMovimientosFiltrados(null)
    }

    fun obtenerMovimientosFiltrados(tipo: String?): List<Movimiento> {
        val movimientos = mutableListOf<Movimiento>()
        val db = readableDatabase
        
        val selection = if (tipo != null) "$COLUMN_TIPO = ?" else null
        val selectionArgs = if (tipo != null) arrayOf(tipo) else null
        
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
                fecha = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_FECHA))
            )
            movimientos.add(movimiento)
        }

        cursor.close()
        db.close()
        return movimientos
    }

    fun obtenerGastosHormigaPorDia(diaInicio: Long, diaFin: Long): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            """
            SELECT COUNT(*) FROM $TABLE_MOVIMIENTOS 
            WHERE $COLUMN_TIPO = ? 
            AND $COLUMN_MONTO < 100
            AND $COLUMN_FECHA >= ? 
            AND $COLUMN_FECHA <= ?
            """.trimIndent(),
            arrayOf("Gasto", diaInicio.toString(), diaFin.toString())
        )
        
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        
        cursor.close()
        db.close()
        return count
    }

    fun obtenerTotalGastosHormiga(): Double {
        val db = readableDatabase
        val cursor = db.rawQuery(
            """
            SELECT SUM($COLUMN_MONTO) FROM $TABLE_MOVIMIENTOS 
            WHERE $COLUMN_TIPO = ? 
            AND $COLUMN_MONTO < 100
            """.trimIndent(),
            arrayOf("Gasto")
        )
        
        var total = 0.0
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0)
        }
        
        cursor.close()
        db.close()
        return total
    }
}

