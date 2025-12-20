package mx.itson.controldegastos.util

import android.content.Context
import androidx.work.*
import mx.itson.controldegastos.worker.MembershipReminderWorker
import mx.itson.controldegastos.worker.MembershipRenewalWorker
import java.util.concurrent.TimeUnit

object MembershipReminderHelper {
    
    private const val WORK_NAME_REMINDER = "membership_reminder_work"
    private const val WORK_NAME_RENEWAL = "membership_renewal_work"
    
    fun programarRecordatorios(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()
        
        val request = PeriodicWorkRequestBuilder<MembershipReminderWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME_REMINDER,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
    
    fun programarRenovacionAutomatica(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()
        
        val request = PeriodicWorkRequestBuilder<MembershipRenewalWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME_RENEWAL,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
    
    fun cancelarRecordatorios(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME_REMINDER)
    }
    
    fun cancelarRenovacionAutomatica(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME_RENEWAL)
    }
}

