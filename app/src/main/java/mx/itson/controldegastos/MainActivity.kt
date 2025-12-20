package mx.itson.controldegastos

import android.content.Intent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import mx.itson.controldegastos.database.DatabaseHelper
import mx.itson.controldegastos.util.GastosHormigaHelper
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var tvTotalIngresos: TextView
    private lateinit var tvTotalGastos: TextView
    private lateinit var tvBalance: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)

        // Configurar barra de estado para que coincida con el toolbar
        window.statusBarColor = androidx.core.content.ContextCompat.getColor(this, R.color.primary)

        // Configurar toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Referencias a las vistas
        tvTotalIngresos = findViewById(R.id.tvTotalIngresos)
        tvTotalGastos = findViewById(R.id.tvTotalGastos)
        tvBalance = findViewById(R.id.tvBalance)

        val btnAgregar = findViewById<Button>(R.id.btnAgregar)
        val btnGraficos = findViewById<Button>(R.id.btnGraficos)
        val btnMembresias = findViewById<Button>(R.id.btnMembresias)
        val btnBalanceSemanal = findViewById<Button>(R.id.btnBalanceSemanal)
        val btnMovimientos = findViewById<Button>(R.id.btnMovimientos)
        val btnRecomendaciones = findViewById<Button>(R.id.btnRecomendaciones)
        val btnConfiguracion = findViewById<Button>(R.id.btnConfiguracion)
        val cardIngresos = findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardIngresos)
        val cardGastos = findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardGastos)

        cardIngresos.setOnClickListener {
            val intent = Intent(this, MovementHistoryActivity::class.java)
            intent.putExtra("movement_type", "Ingreso")
            startActivity(intent)
        }

        cardGastos.setOnClickListener {
            val intent = Intent(this, MovementHistoryActivity::class.java)
            intent.putExtra("movement_type", "Gasto")
            startActivity(intent)
        }

        btnAgregar.setOnClickListener {
            val intent = Intent(this, AddMovementActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_MOVEMENT)
        }

        btnGraficos.setOnClickListener {
            val intent = Intent(this, GraphActivity::class.java)
            startActivity(intent)
        }

        btnMembresias.setOnClickListener {
            val intent = Intent(this, MembershipsActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_MEMBERSHIPS)
        }

        btnBalanceSemanal.setOnClickListener {
            val intent = Intent(this, WeeklyBalanceActivity::class.java)
            startActivity(intent)
        }

        btnMovimientos.setOnClickListener {
            val intent = Intent(this, MovementsActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_MOVEMENTS)
        }

        btnRecomendaciones.setOnClickListener {
            val intent = Intent(this, RecommendationsActivity::class.java)
            startActivity(intent)
        }

        btnConfiguracion.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        // Cargar datos iniciales
        actualizarTotales()
        
        // Verificar gastos hormiga al iniciar
        verificarGastosHormiga()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        // Actualizar cuando se vuelve a la actividad
        actualizarTotales()
        // Verificar gastos hormiga cada vez que se muestra la pantalla
        verificarGastosHormiga()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_MOVEMENT && resultCode == RESULT_OK) {
            // Actualizar después de agregar un movimiento
            actualizarTotales()
            // Verificar gastos hormiga después de agregar un movimiento
            verificarGastosHormiga()
        } else if (requestCode == REQUEST_CODE_MOVEMENTS && resultCode == RESULT_OK) {
            // Actualizar después de eliminar una membresía desde MovementsActivity
            actualizarTotales()
        } else if (requestCode == REQUEST_CODE_MEMBERSHIPS && resultCode == RESULT_OK) {
            // Actualizar después de cambios en membresías
            actualizarTotales()
        }
    }

    private fun actualizarTotales() {
        val totalIngresos = dbHelper.obtenerTotalIngresos()
        val totalGastos = dbHelper.obtenerTotalGastos() // Ya incluye membresías
        val balance = totalIngresos - totalGastos

        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

        tvTotalIngresos.text = formatter.format(totalIngresos)
        tvTotalGastos.text = formatter.format(totalGastos)
        tvBalance.text = formatter.format(balance)
        
        // Color del balance según si es positivo o negativo
        if (balance >= 0) {
            tvBalance.setTextColor(getColor(R.color.secondary))
        } else {
            tvBalance.setTextColor(getColor(R.color.tertiary))
        }
    }

    private fun verificarGastosHormiga() {
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

        // Obtener cantidad de gastos hormiga del día y semana
        val cantidadGastosHormigaDia = dbHelper.obtenerGastosHormigaPorDia(inicioDia, ahora)
        val cantidadGastosHormigaSemana = dbHelper.obtenerGastosHormigaPorDia(inicioSemana, ahora)
        
        // Obtener monto acumulado de gastos hormiga
        val montoGastosHormigaDia = dbHelper.obtenerMontoTotalGastosHormiga(inicioDia, ahora)
        val montoGastosHormigaSemana = dbHelper.obtenerMontoTotalGastosHormiga(inicioSemana, ahora)
        
        // Obtener límites personalizados
        val limiteCantidadDiario = GastosHormigaHelper.obtenerLimiteCantidadDiario(this)
        val limiteCantidadSemanal = GastosHormigaHelper.obtenerLimiteCantidadSemanal(this)
        val limiteMontoDiario = GastosHormigaHelper.obtenerLimiteMontoDiario(this)
        val limiteMontoSemanal = GastosHormigaHelper.obtenerLimiteMontoSemanal(this)
        
        // Verificar porcentaje de ingresos
        val ingresosMensuales = dbHelper.obtenerTotalIngresos()
        val limitePorcentajeIngresos = GastosHormigaHelper.obtenerLimitePorcentajeIngresos(this)
        val porcentajeGastosHormiga = if (ingresosMensuales > 0) {
            (dbHelper.obtenerTotalGastosHormiga() / ingresosMensuales) * 100
        } else {
            0.0
        }

        // Prioridad de alertas: día primero, luego semana
        // Alerta por cantidad diaria
        if (cantidadGastosHormigaDia >= limiteCantidadDiario) {
            mostrarDialogoGastosHormiga(
                cantidad = cantidadGastosHormigaDia,
                monto = montoGastosHormigaDia,
                esDia = true,
                porcentajeIngresos = null
            )
        }
        // Alerta por monto diario (si no se mostró la de cantidad)
        else if (montoGastosHormigaDia >= limiteMontoDiario) {
            mostrarDialogoGastosHormiga(
                cantidad = cantidadGastosHormigaDia,
                monto = montoGastosHormigaDia,
                esDia = true,
                porcentajeIngresos = null
            )
        }
        // Alerta por cantidad semanal
        else if (cantidadGastosHormigaSemana >= limiteCantidadSemanal) {
            mostrarDialogoGastosHormiga(
                cantidad = cantidadGastosHormigaSemana,
                monto = montoGastosHormigaSemana,
                esDia = false,
                porcentajeIngresos = null
            )
        }
        // Alerta por monto semanal
        else if (montoGastosHormigaSemana >= limiteMontoSemanal) {
            mostrarDialogoGastosHormiga(
                cantidad = cantidadGastosHormigaSemana,
                monto = montoGastosHormigaSemana,
                esDia = false,
                porcentajeIngresos = null
            )
        }
        // Alerta por porcentaje de ingresos (siempre se verifica)
        if (porcentajeGastosHormiga >= limitePorcentajeIngresos && ingresosMensuales > 0) {
            mostrarDialogoGastosHormiga(
                cantidad = null,
                monto = dbHelper.obtenerTotalGastosHormiga(),
                esDia = null,
                porcentajeIngresos = porcentajeGastosHormiga
            )
        }
    }

    private fun mostrarDialogoGastosHormiga(
        cantidad: Int?,
        monto: Double,
        esDia: Boolean?,
        porcentajeIngresos: Double?
    ) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_gastos_hormiga, null)
        val tvMensaje = dialogView.findViewById<TextView>(R.id.tvMensajeGastosHormiga)
        val btnEntendido = dialogView.findViewById<Button>(R.id.btnEntendido)

        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
        val mensaje = StringBuilder()

        when {
            // Alerta por porcentaje de ingresos
            porcentajeIngresos != null -> {
                mensaje.append("⚠️ Alerta de Gastos Hormiga\n\n")
                mensaje.append("Tus gastos hormiga representan el ${String.format("%.1f", porcentajeIngresos)}% de tus ingresos mensuales.\n\n")
                mensaje.append("Total acumulado: ${formatter.format(monto)}\n")
                mensaje.append("Esto supera el límite recomendado del ${GastosHormigaHelper.obtenerLimitePorcentajeIngresos(this)}%.\n\n")
                mensaje.append("💡 Considera revisar tus gastos pequeños para mantener un mejor control financiero.")
            }
            // Alerta diaria
            esDia == true && cantidad != null -> {
                mensaje.append("⚠️ Alerta Diaria de Gastos Hormiga\n\n")
                mensaje.append("Has realizado $cantidad gastos hormiga hoy.\n")
                mensaje.append("Total acumulado: ${formatter.format(monto)}\n\n")
                mensaje.append("💡 Intenta reducir los gastos pequeños para mejorar tu balance.")
            }
            // Alerta semanal
            esDia == false && cantidad != null -> {
                mensaje.append("⚠️ Alerta Semanal de Gastos Hormiga\n\n")
                mensaje.append("Has realizado $cantidad gastos hormiga esta semana.\n")
                mensaje.append("Total acumulado: ${formatter.format(monto)}\n\n")
                mensaje.append("💡 Revisa tus hábitos de gasto para mantener un mejor control.")
            }
            // Alerta solo por monto (sin cantidad específica)
            else -> {
                mensaje.append("⚠️ Alerta de Gastos Hormiga\n\n")
                mensaje.append("Has acumulado ${formatter.format(monto)} en gastos hormiga.\n\n")
                mensaje.append("💡 Considera establecer un presupuesto para estos gastos.")
            }
        }

        tvMensaje.text = mensaje.toString()

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        btnEntendido.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    companion object {
        private const val REQUEST_CODE_ADD_MOVEMENT = 1
        private const val REQUEST_CODE_EDIT_MOVEMENT = 2
        private const val REQUEST_CODE_MOVEMENTS = 3
        private const val REQUEST_CODE_MEMBERSHIPS = 4
    }
}
