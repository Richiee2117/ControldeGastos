package mx.itson.controldegastos.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import mx.itson.controldegastos.R
import mx.itson.controldegastos.database.DatabaseHelper
import mx.itson.controldegastos.util.GastosHormigaHelper
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

class GastosHormigaWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    companion object {
        private const val CHANNEL_ID = "gastos_hormiga_channel"
        private const val NOTIFICATION_ID = 1
    }

    override fun doWork(): Result {
        val dbHelper = DatabaseHelper(applicationContext)

        // Obtener fecha actual
        val calendar = Calendar.getInstance()
        val ahora = calendar.timeInMillis

        // Inicio del día de hoy
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val inicioDia = calendar.timeInMillis

        // Inicio de la semana (lunes)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val inicioSemana = calendar.timeInMillis

        // Obtener gastos hormiga del día y de la semana
        val gastosHoy = dbHelper.obtenerGastosHormiga(inicioDia, ahora)
        val gastosSemana = dbHelper.obtenerGastosHormiga(inicioSemana, ahora)
        
        // Obtener montos acumulados
        val montoHoy = dbHelper.obtenerMontoTotalGastosHormiga(inicioDia, ahora)
        val montoSemana = dbHelper.obtenerMontoTotalGastosHormiga(inicioSemana, ahora)
        
        // Obtener límites personalizados
        val limiteCantidadDiario = GastosHormigaHelper.obtenerLimiteCantidadDiario(applicationContext)
        val limiteCantidadSemanal = GastosHormigaHelper.obtenerLimiteCantidadSemanal(applicationContext)
        val limiteMontoDiario = GastosHormigaHelper.obtenerLimiteMontoDiario(applicationContext)
        val limiteMontoSemanal = GastosHormigaHelper.obtenerLimiteMontoSemanal(applicationContext)
        
        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
        
        // Verificar condiciones de gastos hormiga
        val mensajes = mutableListOf<String>()

        // Alerta diaria por cantidad
        if (gastosHoy.size >= limiteCantidadDiario) {
            mensajes.add(
                "Has realizado ${gastosHoy.size} gastos hormiga hoy (Total: ${formatter.format(montoHoy)})"
            )
        }
        // Alerta diaria por monto
        else if (montoHoy >= limiteMontoDiario) {
            mensajes.add(
                "Has acumulado ${formatter.format(montoHoy)} en gastos hormiga hoy"
            )
        }

        // Alerta semanal por cantidad
        if (gastosSemana.size >= limiteCantidadSemanal) {
            mensajes.add(
                "Has realizado ${gastosSemana.size} gastos hormiga esta semana (Total: ${formatter.format(montoSemana)})"
            )
        }
        // Alerta semanal por monto
        else if (montoSemana >= limiteMontoSemanal) {
            mensajes.add(
                "Has acumulado ${formatter.format(montoSemana)} en gastos hormiga esta semana"
            )
        }
        
        // Verificar porcentaje de ingresos
        val ingresosMensuales = dbHelper.obtenerTotalIngresos()
        val limitePorcentajeIngresos = GastosHormigaHelper.obtenerLimitePorcentajeIngresos(applicationContext)
        if (ingresosMensuales > 0) {
            val totalGastosHormiga = dbHelper.obtenerTotalGastosHormiga()
            val porcentaje = (totalGastosHormiga / ingresosMensuales) * 100
            if (porcentaje >= limitePorcentajeIngresos) {
                mensajes.add(
                    "Tus gastos hormiga representan el ${String.format("%.1f", porcentaje)}% de tus ingresos mensuales"
                )
            }
        }

        // Mostrar notificaciones si hay alertas
        if (mensajes.isNotEmpty()) {
            mostrarNotificacion(mensajes.joinToString("\n"))
        }

        return Result.success()
    }

    private fun mostrarNotificacion(mensaje: String) {
        val notificationManager = applicationContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear canal de notificación (Android 8.0+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Gastos Hormiga",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Alertas sobre gastos hormiga"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(applicationContext.getString(R.string.gastos_hormiga_title))
            .setContentText(mensaje)
            .setStyle(NotificationCompat.BigTextStyle().bigText(mensaje))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}

