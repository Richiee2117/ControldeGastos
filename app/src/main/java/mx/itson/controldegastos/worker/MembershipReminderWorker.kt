package mx.itson.controldegastos.worker

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import mx.itson.controldegastos.database.DatabaseHelper
import mx.itson.controldegastos.util.MembershipHelper

class MembershipReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val dbHelper = DatabaseHelper(applicationContext)
        val membresiasProximasAVencer = dbHelper.obtenerMembresiasProximasAVencer(3)

        if (membresiasProximasAVencer.isNotEmpty()) {
            val notificationManager = NotificationManagerCompat.from(applicationContext)
            
            membresiasProximasAVencer.forEach { membresia ->
                val montoMensual = MembershipHelper.convertirAMensual(membresia.monto, membresia.frecuencia)
                val mensaje = "La membresía ${membresia.nombre} vence pronto. Monto mensual: $${String.format("%.2f", montoMensual)}"
                
                val notification = NotificationCompat.Builder(applicationContext, "membership_channel")
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("Recordatorio de Membresía")
                    .setContentText(mensaje)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .build()
                
                notificationManager.notify(membresia.id.toInt(), notification)
            }
        }

        return Result.success()
    }
}

