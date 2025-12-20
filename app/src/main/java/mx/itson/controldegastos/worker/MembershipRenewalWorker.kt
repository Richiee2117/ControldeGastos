package mx.itson.controldegastos.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import mx.itson.controldegastos.database.DatabaseHelper

class MembershipRenewalWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val dbHelper = DatabaseHelper(applicationContext)
        val membresiasVencidas = dbHelper.obtenerMembresiasVencidas()

        if (membresiasVencidas.isNotEmpty()) {
            var renovacionesExitosas = 0
            var renovacionesFallidas = 0

            membresiasVencidas.forEach { membresia ->
                val exito = dbHelper.renovarMembresiaAutomaticamente(membresia)
                if (exito) {
                    renovacionesExitosas++
                } else {
                    renovacionesFallidas++
                }
            }

            // Retornar éxito si al menos una renovación fue exitosa
            return if (renovacionesExitosas > 0) {
                Result.success()
            } else {
                Result.retry()
            }
        }

        return Result.success()
    }
}

