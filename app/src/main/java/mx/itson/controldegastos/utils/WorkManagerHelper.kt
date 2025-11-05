package mx.itson.controldegastos.utils

import android.content.Context
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import mx.itson.controldegastos.service.GastosHormigaWorker
import java.util.concurrent.TimeUnit

object WorkManagerHelper {

    fun programarVerificacionGastosHormiga(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<GastosHormigaWorker>(
            1, TimeUnit.DAYS
        ).build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    fun verificarGastosHormigaInmediato(context: Context) {
        val workRequest = androidx.work.OneTimeWorkRequestBuilder<GastosHormigaWorker>()
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }
}

