package mx.itson.controldegastos.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import mx.itson.controldegastos.R
import mx.itson.controldegastos.database.DatabaseHelper
import java.util.Calendar

class GastosHormigaWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    companion object {
        private const val CHANNEL_ID = "gastos_hormiga_channel"
        private const val NOTIFICATION_ID = 1
        private const val LIMITE_DIARIO = 3
        private const val LIMITE_SEMANAL = 10
        private const val MONTO_MAXIMO = 100.0
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
        
        // Obtener gastos pequeños del día
        val gastosHoy = dbHelper.obtenerGastosPorDia(inicioDia, ahora)
            .filter { it.monto < MONTO_MAXIMO }
        
        // Obtener gastos pequeños de la semana
        val gastosSemana = dbHelper.obtenerGastosPorDia(inicioSemana, ahora)
            .filter { it.monto < MONTO_MAXIMO }
        
        // Verificar condiciones de gastos hormiga
        val mensajes = mutableListOf<String>()
        
        if (gastosHoy.size >= LIMITE_DIARIO) {
            mensajes.add(
                applicationContext.getString(
                    R.string.gastos_hormiga_mensaje,
                    gastosHoy.size
                )
            )
        }
        
        if (gastosSemana.size >= LIMITE_SEMANAL) {
            mensajes.add(
                applicationContext.getString(
                    R.string.gastos_hormiga_semana,
                    gastosSemana.size
                )
            )
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

