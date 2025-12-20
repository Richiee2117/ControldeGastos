package mx.itson.controldegastos.util

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

object GastosHormigaHelper {
    
    // Límites predeterminados por categoría
    private val LIMITES_PREDETERMINADOS = mapOf(
        "Comida" to 120.0,
        "Transporte" to 80.0,
        "Entretenimiento" to 150.0,
        "Salud" to 120.0,
        "Snacks" to 70.0,
        "Educación" to 150.0
    )
    
    // Umbrales predeterminados para alertas
    private const val LIMITE_CANTIDAD_DIARIO = 3
    private const val LIMITE_CANTIDAD_SEMANAL = 10
    private const val LIMITE_MONTO_DIARIO = 500.0
    private const val LIMITE_MONTO_SEMANAL = 2000.0
    private const val LIMITE_PORCENTAJE_INGRESOS = 15.0 // 15% de los ingresos mensuales
    
    /**
     * Obtiene el límite personalizado para una categoría, o el predeterminado si no existe
     */
    fun obtenerLimiteCategoria(context: Context, categoria: String): Double {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val key = "limite_gasto_hormiga_${categoria.lowercase()}"
        val limitePersonalizado = prefs.getFloat(key, -1f)
        
        return if (limitePersonalizado >= 0) {
            limitePersonalizado.toDouble()
        } else {
            LIMITES_PREDETERMINADOS[categoria] ?: -1.0
        }
    }
    
    /**
     * Guarda un límite personalizado para una categoría
     */
    fun guardarLimiteCategoria(context: Context, categoria: String, limite: Double) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val key = "limite_gasto_hormiga_${categoria.lowercase()}"
        prefs.edit().putFloat(key, limite.toFloat()).apply()
    }
    
    /**
     * Restaura los límites predeterminados para todas las categorías
     */
    fun restaurarLimitesPredeterminados(context: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = prefs.edit()
        
        LIMITES_PREDETERMINADOS.forEach { (categoria, limite) ->
            val key = "limite_gasto_hormiga_${categoria.lowercase()}"
            editor.putFloat(key, limite.toFloat())
        }
        
        editor.apply()
    }
    
    /**
     * Obtiene el límite de cantidad diario para alertas
     */
    fun obtenerLimiteCantidadDiario(context: Context): Int {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getInt("limite_cantidad_diario", LIMITE_CANTIDAD_DIARIO)
    }
    
    /**
     * Guarda el límite de cantidad diario
     */
    fun guardarLimiteCantidadDiario(context: Context, limite: Int) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().putInt("limite_cantidad_diario", limite).apply()
    }
    
    /**
     * Obtiene el límite de cantidad semanal para alertas
     */
    fun obtenerLimiteCantidadSemanal(context: Context): Int {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getInt("limite_cantidad_semanal", LIMITE_CANTIDAD_SEMANAL)
    }
    
    /**
     * Guarda el límite de cantidad semanal
     */
    fun guardarLimiteCantidadSemanal(context: Context, limite: Int) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().putInt("limite_cantidad_semanal", limite).apply()
    }
    
    /**
     * Obtiene el límite de monto diario para alertas
     */
    fun obtenerLimiteMontoDiario(context: Context): Double {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getFloat("limite_monto_diario", LIMITE_MONTO_DIARIO.toFloat()).toDouble()
    }
    
    /**
     * Guarda el límite de monto diario
     */
    fun guardarLimiteMontoDiario(context: Context, limite: Double) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().putFloat("limite_monto_diario", limite.toFloat()).apply()
    }
    
    /**
     * Obtiene el límite de monto semanal para alertas
     */
    fun obtenerLimiteMontoSemanal(context: Context): Double {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getFloat("limite_monto_semanal", LIMITE_MONTO_SEMANAL.toFloat()).toDouble()
    }
    
    /**
     * Guarda el límite de monto semanal
     */
    fun guardarLimiteMontoSemanal(context: Context, limite: Double) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().putFloat("limite_monto_semanal", limite.toFloat()).apply()
    }
    
    /**
     * Obtiene el límite de porcentaje de ingresos para alertas
     */
    fun obtenerLimitePorcentajeIngresos(context: Context): Double {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getFloat("limite_porcentaje_ingresos", LIMITE_PORCENTAJE_INGRESOS.toFloat()).toDouble()
    }
    
    /**
     * Guarda el límite de porcentaje de ingresos
     */
    fun guardarLimitePorcentajeIngresos(context: Context, limite: Double) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().putFloat("limite_porcentaje_ingresos", limite.toFloat()).apply()
    }
    
    /**
     * Obtiene todas las categorías que tienen límites definidos
     */
    fun obtenerCategoriasConLimites(): List<String> {
        return LIMITES_PREDETERMINADOS.keys.sorted()
    }
}

