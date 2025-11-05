package mx.itson.controldegastos.model

data class Movimiento(
    val id: Long = 0,
    val monto: Double,
    val descripcion: String,
    val tipo: String, // "Ingreso" o "Gasto"
    val fecha: Long = System.currentTimeMillis()
)

