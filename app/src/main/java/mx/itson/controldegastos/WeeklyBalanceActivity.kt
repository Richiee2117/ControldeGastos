package mx.itson.controldegastos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import mx.itson.controldegastos.database.DatabaseHelper
import java.text.NumberFormat
import java.util.*

class WeeklyBalanceActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var lineChart: LineChart
    private lateinit var tvTotalSemanaActual: TextView
    private lateinit var tvTotalSemanaAnterior: TextView
    private lateinit var tvDiferencia: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weekly_balance)

        dbHelper = DatabaseHelper(this)

        // Configurar barra de estado para que coincida con el toolbar
        window.statusBarColor = androidx.core.content.ContextCompat.getColor(this, R.color.primary)

        // Configurar toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        lineChart = findViewById(R.id.lineChart)
        tvTotalSemanaActual = findViewById(R.id.tvTotalSemanaActual)
        tvTotalSemanaAnterior = findViewById(R.id.tvTotalSemanaAnterior)
        tvDiferencia = findViewById(R.id.tvDiferencia)

        configurarGrafico()
        cargarDatos()
    }

    private fun configurarGrafico() {
        lineChart.description.isEnabled = false
        lineChart.setDrawGridBackground(false)
        lineChart.setTouchEnabled(true)
        lineChart.setDragEnabled(true)
        lineChart.setScaleEnabled(true)
        lineChart.setPinchZoom(true)

        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true

        val leftAxis = lineChart.axisLeft
        leftAxis.setDrawGridLines(true)
        leftAxis.axisMinimum = 0f

        lineChart.axisRight.isEnabled = false
        lineChart.legend.isEnabled = true

        lineChart.animateX(1000)
    }

    private fun cargarDatos() {
        val calendar = Calendar.getInstance()
        val ahora = calendar.timeInMillis

        // Obtener fecha del primer registro (movimiento o membresía)
        val fechaPrimerRegistro = dbHelper.obtenerFechaPrimerRegistro()
        
        // Si no hay registros, usar la fecha actual menos 7 días
        val fechaInicioBase = fechaPrimerRegistro ?: (ahora - (7 * 24 * 60 * 60 * 1000L))
        
        // Obtener el día de la semana del primer registro
        calendar.timeInMillis = fechaInicioBase
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        
        val diaSemanaPrimerRegistro = calendar.get(Calendar.DAY_OF_WEEK)
        val diasDesdeLunes = when (diaSemanaPrimerRegistro) {
            Calendar.SUNDAY -> 6
            else -> diaSemanaPrimerRegistro - Calendar.MONDAY
        }
        
        // Ajustar al inicio del día del primer registro
        calendar.add(Calendar.DAY_OF_YEAR, -diasDesdeLunes)
        val inicioSemanaActual = calendar.timeInMillis

        // Obtener fin de semana actual (7 días después del inicio)
        calendar.add(Calendar.DAY_OF_YEAR, 6)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val finSemanaActual = calendar.timeInMillis

        // Obtener inicio de semana anterior (7 días antes del inicio de la semana actual)
        calendar.setTimeInMillis(inicioSemanaActual)
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val inicioSemanaAnterior = calendar.timeInMillis

        // Obtener fin de semana anterior
        calendar.add(Calendar.DAY_OF_YEAR, 6)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val finSemanaAnterior = calendar.timeInMillis

        // Generar labels dinámicos basados en el día de inicio
        val diaInicio = Calendar.getInstance().apply { timeInMillis = inicioSemanaActual }.get(Calendar.DAY_OF_WEEK)
        val diasSemana = arrayOf("Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb")
        val labels = mutableListOf<String>()
        for (i in 0..6) {
            val indiceDia = (diaInicio + i - 1) % 7
            labels.add(diasSemana[indiceDia])
        }

        // Calcular totales por día de la semana actual
        val datosSemanaActual = mutableListOf<Double>()
        calendar.setTimeInMillis(inicioSemanaActual)
        for (i in 0..6) {
            val inicioDia = calendar.timeInMillis
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            val finDia = calendar.timeInMillis
            
            val ingresos = dbHelper.obtenerTotalPorDia(inicioDia, finDia, "Ingreso")
            val gastos = dbHelper.obtenerTotalPorDia(inicioDia, finDia, "Gasto")
            // No incluir membresías en el balance diario, ya que son mensuales
            val balance = ingresos - gastos
            datosSemanaActual.add(balance)
            
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
        }

        // Calcular totales por día de la semana anterior
        val datosSemanaAnterior = mutableListOf<Double>()
        calendar.setTimeInMillis(inicioSemanaAnterior)
        for (i in 0..6) {
            val inicioDia = calendar.timeInMillis
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            val finDia = calendar.timeInMillis
            
            val ingresos = dbHelper.obtenerTotalPorDia(inicioDia, finDia, "Ingreso")
            val gastos = dbHelper.obtenerTotalPorDia(inicioDia, finDia, "Gasto")
            // No incluir membresías en el balance diario, ya que son mensuales
            val balance = ingresos - gastos
            datosSemanaAnterior.add(balance)
            
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
        }

        // Calcular totales (incluyendo membresías completas de la semana)
        val totalIngresosSemanaActual = dbHelper.obtenerTotalPorDia(inicioSemanaActual, finSemanaActual, "Ingreso")
        val totalGastosSemanaActual = dbHelper.obtenerTotalPorDia(inicioSemanaActual, finSemanaActual, "Gasto")
        val totalMembresiasSemanaActual = dbHelper.obtenerTotalMembresiasMensualPorRango(inicioSemanaActual, finSemanaActual)
        val totalSemanaActual = totalIngresosSemanaActual - totalGastosSemanaActual - totalMembresiasSemanaActual
        
        val totalIngresosSemanaAnterior = dbHelper.obtenerTotalPorDia(inicioSemanaAnterior, finSemanaAnterior, "Ingreso")
        val totalGastosSemanaAnterior = dbHelper.obtenerTotalPorDia(inicioSemanaAnterior, finSemanaAnterior, "Gasto")
        val totalMembresiasSemanaAnterior = dbHelper.obtenerTotalMembresiasMensualPorRango(inicioSemanaAnterior, finSemanaAnterior)
        val totalSemanaAnterior = totalIngresosSemanaAnterior - totalGastosSemanaAnterior - totalMembresiasSemanaAnterior
        
        val diferencia = totalSemanaActual - totalSemanaAnterior

        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
        tvTotalSemanaActual.text = "Semana Actual: ${formatter.format(totalSemanaActual)}"
        tvTotalSemanaAnterior.text = "Semana Anterior: ${formatter.format(totalSemanaAnterior)}"
        
        val textoDiferencia = when {
            diferencia > 0 -> "Diferencia: +${formatter.format(diferencia)}"
            diferencia < 0 -> "Diferencia: ${formatter.format(diferencia)}"
            else -> "Diferencia: ${formatter.format(0.0)}"
        }
        tvDiferencia.text = textoDiferencia

        // Configurar gráfico
        val entriesActual = ArrayList<Entry>()
        val entriesAnterior = ArrayList<Entry>()

        datosSemanaActual.forEachIndexed { index, valor ->
            entriesActual.add(Entry(index.toFloat(), valor.toFloat()))
        }

        datosSemanaAnterior.forEachIndexed { index, valor ->
            entriesAnterior.add(Entry(index.toFloat(), valor.toFloat()))
        }

        val dataSetActual = LineDataSet(entriesActual, "Semana Actual")
        dataSetActual.color = resources.getColor(R.color.primary, theme)
        dataSetActual.lineWidth = 3f
        dataSetActual.setCircleColor(resources.getColor(R.color.primary, theme))
        dataSetActual.setDrawValues(true)
        dataSetActual.valueTextSize = 10f

        val dataSetAnterior = LineDataSet(entriesAnterior, "Semana Anterior")
        dataSetAnterior.color = resources.getColor(R.color.on_surface_variant, theme)
        dataSetAnterior.lineWidth = 2f
        dataSetAnterior.setCircleColor(resources.getColor(R.color.on_surface_variant, theme))
        dataSetAnterior.setDrawValues(true)
        dataSetAnterior.valueTextSize = 10f
        dataSetAnterior.enableDashedLine(10f, 5f, 0f)

        val lineData = LineData(dataSetActual, dataSetAnterior)
        lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels.toTypedArray())
        lineChart.data = lineData
        lineChart.invalidate()
    }
}

