package mx.itson.controldegastos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import mx.itson.controldegastos.database.DatabaseHelper
import java.util.ArrayList

class GraphActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var barChart: BarChart
    private lateinit var pieChart: PieChart
    private lateinit var tvPorcentajeGastosHormiga: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        dbHelper = DatabaseHelper(this)

        // Configurar barra de estado para que coincida con el toolbar
        window.statusBarColor = androidx.core.content.ContextCompat.getColor(this, R.color.primary)

        // Configurar toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        barChart = findViewById(R.id.barChart)
        pieChart = findViewById(R.id.pieChart)
        tvPorcentajeGastosHormiga = findViewById(R.id.tvPorcentajeGastosHormiga)

        configurarGraficoBarras()
        configurarGraficoCircular()
        cargarDatos()
        calcularPorcentajeGastosHormiga()
    }

    private fun configurarGraficoBarras() {
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

    private fun configurarGraficoCircular() {
        pieChart.description.isEnabled = false
        pieChart.setUsePercentValues(true)
        pieChart.setDrawEntryLabels(true)
        pieChart.setEntryLabelTextSize(12f)
        pieChart.setEntryLabelColor(resources.getColor(R.color.on_surface, theme))
        pieChart.setDrawCenterText(false)
        pieChart.isRotationEnabled = true
        pieChart.setHighlightPerTapEnabled(true)
        pieChart.animateY(1000)
        
        pieChart.legend.isEnabled = true
        pieChart.legend.textSize = 12f
        pieChart.legend.textColor = resources.getColor(R.color.on_surface, theme)
    }

    private fun cargarDatos() {
        cargarGraficoBarras()
        cargarGraficoCircular()
    }

    private fun cargarGraficoBarras() {
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

    private fun cargarGraficoCircular() {
        val gastosPorCategoria = dbHelper.obtenerGastosPorCategoria()
        
        if (gastosPorCategoria.isEmpty()) {
            pieChart.visibility = android.view.View.GONE
            return
        }

        pieChart.visibility = android.view.View.VISIBLE
        
        val entries = ArrayList<PieEntry>()
        val colores = ArrayList<Int>()
        
        // Paleta de colores vibrantes y contrastantes para la gráfica de pastel
        val coloresCategoria = mapOf(
            "Comida" to android.graphics.Color.parseColor("#4CAF50"), // Verde claro
            "Transporte" to android.graphics.Color.parseColor("#2196F3"), // Azul claro
            "Entretenimiento" to android.graphics.Color.parseColor("#9C27B0"), // Púrpura
            "Salud" to android.graphics.Color.parseColor("#F44336"), // Rojo
            "Membresías" to android.graphics.Color.parseColor("#FF9800"), // Naranja
            "Snacks" to android.graphics.Color.parseColor("#FFC107"), // Amarillo/Ámbar
            "Educación" to android.graphics.Color.parseColor("#00BCD4"), // Cian
            "Otros" to android.graphics.Color.parseColor("#9E9E9E") // Gris medio
        )

        gastosPorCategoria.forEach { (categoria, monto) ->
            entries.add(PieEntry(monto.toFloat(), categoria))
            colores.add(coloresCategoria[categoria] ?: resources.getColor(R.color.primary, theme))
        }

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = colores
        dataSet.valueTextSize = 12f
        dataSet.valueTextColor = resources.getColor(R.color.on_surface, theme)
        dataSet.valueFormatter = PercentFormatter(pieChart)

        val pieData = PieData(dataSet)
        pieChart.data = pieData
        pieChart.invalidate()
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
