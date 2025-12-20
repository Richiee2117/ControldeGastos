package mx.itson.controldegastos.util

import mx.itson.controldegastos.R
import mx.itson.controldegastos.model.Membership
import java.util.Calendar
import java.util.Locale

object MembershipHelper {
    
    /**
     * Calcula el próximo vencimiento basado en la frecuencia y fecha de pago
     */
    fun calcularProximoVencimiento(fechaPago: Long, frecuencia: String): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = fechaPago
        
        when (frecuencia) {
            "Mensual" -> calendar.add(Calendar.MONTH, 1)
            "Trimestral" -> calendar.add(Calendar.MONTH, 3)
            "Semestral" -> calendar.add(Calendar.MONTH, 6)
            "Anual" -> calendar.add(Calendar.YEAR, 1)
            else -> calendar.add(Calendar.MONTH, 1)
        }
        
        return calendar.timeInMillis
    }
    
    /**
     * Convierte el monto a mensual según la frecuencia
     */
    fun convertirAMensual(monto: Double, frecuencia: String): Double {
        return when (frecuencia) {
            "Mensual" -> monto
            "Trimestral" -> monto / 3
            "Semestral" -> monto / 6
            "Anual" -> monto / 12
            else -> monto
        }
    }
    
    /**
     * Verifica si una membresía está próxima a vencer
     */
    fun estaProximaAVencer(membresia: Membership, dias: Int = 3): Boolean {
        val ahora = System.currentTimeMillis()
        val limite = ahora + (dias * 24 * 60 * 60 * 1000L)
        return membresia.activa && membresia.proximoVencimiento >= ahora && membresia.proximoVencimiento <= limite
    }
    
    /**
     * Verifica si una membresía está vencida
     */
    fun estaVencida(membresia: Membership): Boolean {
        return membresia.proximoVencimiento < System.currentTimeMillis()
    }

    /**
     * Obtiene el recurso drawable asociado a una membresía según su tipo o nombre
     */
    fun obtenerLogoMembresia(tipo: String, nombre: String = ""): Int? {
        val clave = tipo.lowercase(Locale.getDefault())
        val nombreLower = nombre.lowercase(Locale.getDefault())

        return when {
            clave.contains("netflix") || nombreLower.contains("netflix") -> R.drawable.netflix
            clave.contains("disney") || nombreLower.contains("disney") -> R.drawable.disney
            clave.contains("hbo") -> R.drawable.hbo_max
            clave.contains("prime") || nombreLower.contains("amazon") -> R.drawable.prime_video
            clave.contains("apple tv") || nombreLower.contains("tv+") || nombreLower.contains("apple tv") -> R.drawable.appletv
            clave.contains("youtube") -> R.drawable.youtube_premium
            clave.contains("apple music") || nombreLower.contains("music") -> R.drawable.applemusic
            clave.contains("spotify") || nombreLower.contains("spotify") -> R.drawable.spotify
            clave.contains("microsoft") -> R.drawable.microsoft_365
            clave.contains("adobe") -> R.drawable.adobe_cloud
            clave.contains("playstation") -> R.drawable.playstation_plus
            clave.contains("xbox") -> R.drawable.xbox
            clave.contains("nintendo") -> R.drawable.nintendo_online
            clave.contains("google") -> R.drawable.google_one
            clave.contains("icloud") -> R.drawable.icloud
            clave.contains("uber") -> R.drawable.uber
            clave.contains("rappi") -> R.drawable.rappi
            clave.contains("didi") -> R.drawable.didi
            else -> null
        }
    }
}

