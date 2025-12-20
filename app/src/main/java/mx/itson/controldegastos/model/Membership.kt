package mx.itson.controldegastos.model

data class Membership(
    val id: Long = 0,
    val nombre: String,
    val tipo: String, // "Netflix", "Spotify", "Personalizada", etc.
    val monto: Double,
    val frecuencia: String, // "Mensual", "Trimestral", "Semestral", "Anual"
    val fechaPago: Long, // Fecha del último pago
    val proximoVencimiento: Long, // Fecha del próximo vencimiento
    val descripcion: String = "",
    val activa: Boolean = true,
    val renovacionAutomatica: Boolean = false, // Si se renueva automáticamente
    val recibirNotificaciones: Boolean = true // Si recibe notificaciones de vencimiento
)

