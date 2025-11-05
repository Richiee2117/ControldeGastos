package mx.itson.controldegastos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import mx.itson.controldegastos.database.DatabaseHelper

class GraphActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var barChart: BarChart
    private lateinit var tvPorcentajeGastosHormiga: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        dbHelper = DatabaseHelper(this)

        // Configurar toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        barChart = findViewById(R.id.barChart)
        tvPorcentajeGastosHormiga = findViewById(R.id.tvPorcentajeGastosHormiga)

        configurarGrafico()
        cargarDatos()
        calcularPorcentajeGastosHormiga()
    }

    private fun configurarGrafico() {
        barChart.description.isEnabled = false
        barChart.setDrawGridBackground(false)
        barChart.setFitBars(true)

        // Configurar eje X
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true

        // Configurar eje Y izquierdo
        val leftAxis = barChart.axisLeft
        leftAxis.setDrawGridLines(true)
        leftAxis.axisMinimum = 0f

        // Deshabilitar eje Y derecho
        barChart.axisRight.isEnabled = false

        // Configurar leyenda
        barChart.legend.isEnabled = true

        // Animación
        barChart.animateY(1000)
    }

    private fun cargarDatos() {
        val totalIngresos = dbHelper.obtenerTotalIngresos().toFloat()
        val totalGastos = dbHelper.obtenerTotalGastos().toFloat()

        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(0f, totalIngresos))
        entries.add(BarEntry(1f, totalGastos))

        val dataSet = BarDataSet(entries, "Movimientos")
        dataSet.color = resources.getColor(R.color.primary, theme)
        dataSet.setValueTextColor(resources.getColor(R.color.on_surface, theme))
        dataSet.setValueTextSize(12f)

        val barData = BarData(dataSet)
        barData.barWidth = 0.5f

        val labels = arrayOf("Ingresos", "Gastos")
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)

        barChart.data = barData
        barChart.invalidate()
    }

    private fun calcularPorcentajeGastosHormiga() {
        val totalGastos = dbHelper.obtenerTotalGastos()
        val totalGastosHormiga = dbHelper.obtenerTotalGastosHormiga()

        val porcentaje = if (totalGastos > 0) {
            (totalGastosHormiga / totalGastos) * 100
        } else {
            0.0
        }

        tvPorcentajeGastosHormiga.text = getString(R.string.porcentaje_gastos_hormiga, porcentaje)
    }
}
