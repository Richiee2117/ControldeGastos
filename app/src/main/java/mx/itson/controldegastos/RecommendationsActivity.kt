package mx.itson.controldegastos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import mx.itson.controldegastos.database.DatabaseHelper
import java.text.NumberFormat
import java.util.*

class RecommendationsActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private val recomendaciones = listOf(
        "💼 Inversión en Fondos de Inversión" to "Considera invertir parte de tu dinero en fondos de inversión para hacer crecer tus ahorros con bajo riesgo.",
        "🏦 Cuenta de Ahorro de Alto Rendimiento" to "Abre una cuenta de ahorro con mejor tasa de interés para que tu dinero genere más sin hacer nada.",
        "📚 Educación Financiera" to "Invierte en tu educación financiera. Libros, cursos o asesoría pueden ayudarte a tomar mejores decisiones.",
        "🚗 Fondo de Emergencia" to "Asegúrate de tener un fondo de emergencia equivalente a 3-6 meses de gastos. Es tu red de seguridad financiera.",
        "💳 Pago de Deudas" to "Si tienes deudas con altos intereses, considera pagarlas primero. Es como una inversión con retorno garantizado.",
        "🏠 Ahorro para Propiedad" to "Si planeas comprar una casa o terreno, comienza a ahorrar ahora. Los bienes raíces suelen ser buena inversión.",
        "🎯 Metas Personales" to "Define metas financieras claras: vacaciones, estudios, negocio propio. El dinero ahorrado te acerca a tus sueños.",
        "💎 Diversificación" to "No pongas todos tus huevos en una canasta. Diversifica tus inversiones para reducir riesgos."
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommendations)

        dbHelper = DatabaseHelper(this)

        // Configurar barra de estado para que coincida con el toolbar
        window.statusBarColor = androidx.core.content.ContextCompat.getColor(this, R.color.primary)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val balance = calcularBalance()
        
        if (balance <= 0) {
            mostrarMensajeSinBalance()
        } else {
            mostrarRecomendaciones(balance)
        }
    }

    private fun calcularBalance(): Double {
        val totalIngresos = dbHelper.obtenerTotalIngresos()
        val totalGastos = dbHelper.obtenerTotalGastos() // Ya incluye membresías
        return totalIngresos - totalGastos
    }

    private fun mostrarMensajeSinBalance() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_recommendation, null)
        val tvTitulo = dialogView.findViewById<TextView>(R.id.tvTituloRecomendacion)
        val tvMensaje = dialogView.findViewById<TextView>(R.id.tvMensajeRecomendacion)
        val btnEntendido = dialogView.findViewById<Button>(R.id.btnEntendidoRecomendacion)

        tvTitulo.text = "Sin Balance Positivo"
        tvMensaje.text = "Para recibir recomendaciones financieras, primero necesitas tener un balance positivo. \n\nTe recomendamos:\n\n• Reducir gastos innecesarios\n• Revisar tus membresías\n• Buscar formas de aumentar tus ingresos\n• Crear un presupuesto mensual"

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        btnEntendido.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        dialog.show()
    }

    private fun mostrarRecomendaciones(balance: Double) {
        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
        val balanceFormateado = formatter.format(balance)
        
        // Obtener referencia al TextView ANTES de limpiar el contenedor
        val tvBalance = findViewById<TextView>(R.id.tvBalanceRecomendaciones)
        tvBalance.text = "Balance disponible: $balanceFormateado"
        
        // Seleccionar recomendaciones aleatorias (4)
        val recomendacionesAleatorias = recomendaciones.shuffled().take(4)
        
        val container = findViewById<ConstraintLayout>(R.id.containerRecomendaciones)
        
        // Eliminar solo las tarjetas de recomendaciones (no el TextView del balance)
        val viewsToRemove = mutableListOf<View>()
        for (i in 0 until container.childCount) {
            val child = container.getChildAt(i)
            if (child.id != R.id.tvBalanceRecomendaciones) {
                viewsToRemove.add(child)
            }
        }
        viewsToRemove.forEach { container.removeView(it) }
        
        var prevViewId = R.id.tvBalanceRecomendaciones
        
        recomendacionesAleatorias.forEachIndexed { index, (titulo, mensaje) ->
            val cardView = LayoutInflater.from(this).inflate(R.layout.item_recommendation, container, false)
            val tvTitulo = cardView.findViewById<TextView>(R.id.tvTituloRecomendacion)
            val tvMensaje = cardView.findViewById<TextView>(R.id.tvMensajeRecomendacion)
            
            tvTitulo.text = titulo
            tvMensaje.text = mensaje
            
            val newViewId = View.generateViewId()
            cardView.id = newViewId
            
            val params = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topToBottom = prevViewId
                topMargin = 16
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                marginStart = 0
                marginEnd = 0
            }
            
            cardView.layoutParams = params
            container.addView(cardView)
            
            prevViewId = newViewId
        }
    }
}

