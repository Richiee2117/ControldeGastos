package mx.itson.controldegastos

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import mx.itson.controldegastos.util.MembershipReminderHelper

class ControlGastosApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        crearCanalNotificaciones()
        MembershipReminderHelper.programarRecordatorios(this)
        MembershipReminderHelper.programarRenovacionAutomatica(this)
    }
    
    private fun crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "membership_channel",
                "Recordatorios de Membresías",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones para recordar vencimientos de membresías"
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}

