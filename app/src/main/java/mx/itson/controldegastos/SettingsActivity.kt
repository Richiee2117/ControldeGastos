package mx.itson.controldegastos

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceManager
import com.google.android.material.button.MaterialButton
import mx.itson.controldegastos.util.GastosHormigaHelper
import java.text.NumberFormat
import java.util.Locale

class SettingsActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var switchModoOscuro: Switch
    private lateinit var btnConfigurarLimites: MaterialButton
    private lateinit var btnConfigurarAlertas: MaterialButton
    private lateinit var btnRestaurarLimites: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        // Configurar barra de estado para que coincida con el toolbar
        window.statusBarColor = androidx.core.content.ContextCompat.getColor(this, R.color.primary)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        switchModoOscuro = findViewById(R.id.switchModoOscuro)
        btnConfigurarLimites = findViewById(R.id.btnConfigurarLimites)
        btnConfigurarAlertas = findViewById(R.id.btnConfigurarAlertas)
        btnRestaurarLimites = findViewById(R.id.btnRestaurarLimites)

        // Cargar estado actual
        val modoOscuroActivado = sharedPreferences.getBoolean("modo_oscuro", false)
        switchModoOscuro.isChecked = modoOscuroActivado

        switchModoOscuro.setOnCheckedChangeListener { _, isChecked ->
            cambiarModoOscuro(isChecked)
        }

        btnConfigurarLimites.setOnClickListener {
            mostrarDialogoConfigurarLimites()
        }

        btnConfigurarAlertas.setOnClickListener {
            mostrarDialogoConfigurarAlertas()
        }

        btnRestaurarLimites.setOnClickListener {
            mostrarDialogoRestaurarLimites()
        }
    }

    private fun cambiarModoOscuro(activado: Boolean) {
        sharedPreferences.edit().putBoolean("modo_oscuro", activado).apply()

        if (activado) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        Toast.makeText(this, if (activado) "Modo oscuro activado" else "Modo oscuro desactivado", Toast.LENGTH_SHORT).show()
    }

    private fun mostrarDialogoConfigurarLimites() {
        val categorias = GastosHormigaHelper.obtenerCategoriasConLimites()
        val categoriasArray = categorias.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Configurar Límites por Categoría")
            .setItems(categoriasArray) { _, which ->
                val categoria = categorias[which]
                mostrarDialogoEditarLimite(categoria)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoEditarLimite(categoria: String) {
        val dialogView = LayoutInflater.from(this).inflate(android.R.layout.simple_list_item_1, null)
        val container = LayoutInflater.from(this).inflate(android.R.layout.simple_list_item_2, null)
        
        val dialogViewCustom = LayoutInflater.from(this).inflate(android.R.layout.simple_list_item_1, null)
        val edtLimite = EditText(this)
        edtLimite.hint = "Límite en pesos"
        edtLimite.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        
        val limiteActual = GastosHormigaHelper.obtenerLimiteCategoria(this, categoria)
        edtLimite.setText(limiteActual.toString())

        val containerLayout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 40, 50, 40)
            addView(TextView(this@SettingsActivity).apply {
                text = "Categoría: $categoria"
                textSize = 16f
                setPadding(0, 0, 0, 20)
            })
            addView(edtLimite)
        }

        AlertDialog.Builder(this)
            .setTitle("Editar Límite")
            .setView(containerLayout)
            .setPositiveButton("Guardar") { _, _ ->
                try {
                    val nuevoLimite = edtLimite.text.toString().toDouble()
                    if (nuevoLimite > 0) {
                        GastosHormigaHelper.guardarLimiteCategoria(this, categoria, nuevoLimite)
                        Toast.makeText(this, "Límite actualizado para $categoria", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "El límite debe ser mayor a 0", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "Ingresa un valor válido", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoConfigurarAlertas() {
        val dialogView = LayoutInflater.from(this).inflate(android.R.layout.simple_list_item_1, null)
        val container = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 40, 50, 40)
        }

        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

        // Cantidad diaria
        val tvCantidadDiaria = TextView(this).apply {
            text = "Cantidad diaria (actual: ${GastosHormigaHelper.obtenerLimiteCantidadDiario(this@SettingsActivity)})"
            textSize = 14f
            setPadding(0, 0, 0, 10)
        }
        val edtCantidadDiaria = EditText(this).apply {
            hint = "Ej: 3"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setText(GastosHormigaHelper.obtenerLimiteCantidadDiario(this@SettingsActivity).toString())
        }

        // Cantidad semanal
        val tvCantidadSemanal = TextView(this).apply {
            text = "Cantidad semanal (actual: ${GastosHormigaHelper.obtenerLimiteCantidadSemanal(this@SettingsActivity)})"
            textSize = 14f
            setPadding(0, 20, 0, 10)
        }
        val edtCantidadSemanal = EditText(this).apply {
            hint = "Ej: 10"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setText(GastosHormigaHelper.obtenerLimiteCantidadSemanal(this@SettingsActivity).toString())
        }

        // Monto diario
        val tvMontoDiario = TextView(this).apply {
            text = "Monto diario (actual: ${formatter.format(GastosHormigaHelper.obtenerLimiteMontoDiario(this@SettingsActivity))})"
            textSize = 14f
            setPadding(0, 20, 0, 10)
        }
        val edtMontoDiario = EditText(this).apply {
            hint = "Ej: 500"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(GastosHormigaHelper.obtenerLimiteMontoDiario(this@SettingsActivity).toString())
        }

        // Monto semanal
        val tvMontoSemanal = TextView(this).apply {
            text = "Monto semanal (actual: ${formatter.format(GastosHormigaHelper.obtenerLimiteMontoSemanal(this@SettingsActivity))})"
            textSize = 14f
            setPadding(0, 20, 0, 10)
        }
        val edtMontoSemanal = EditText(this).apply {
            hint = "Ej: 2000"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(GastosHormigaHelper.obtenerLimiteMontoSemanal(this@SettingsActivity).toString())
        }

        // Porcentaje de ingresos
        val tvPorcentajeIngresos = TextView(this).apply {
            text = "Porcentaje de ingresos (actual: ${GastosHormigaHelper.obtenerLimitePorcentajeIngresos(this@SettingsActivity)}%)"
            textSize = 14f
            setPadding(0, 20, 0, 10)
        }
        val edtPorcentajeIngresos = EditText(this).apply {
            hint = "Ej: 15"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(GastosHormigaHelper.obtenerLimitePorcentajeIngresos(this@SettingsActivity).toString())
        }

        container.addView(tvCantidadDiaria)
        container.addView(edtCantidadDiaria)
        container.addView(tvCantidadSemanal)
        container.addView(edtCantidadSemanal)
        container.addView(tvMontoDiario)
        container.addView(edtMontoDiario)
        container.addView(tvMontoSemanal)
        container.addView(edtMontoSemanal)
        container.addView(tvPorcentajeIngresos)
        container.addView(edtPorcentajeIngresos)

        AlertDialog.Builder(this)
            .setTitle("Configurar Umbrales de Alertas")
            .setView(container)
            .setPositiveButton("Guardar") { _, _ ->
                try {
                    val cantidadDiaria = edtCantidadDiaria.text.toString().toIntOrNull() ?: 3
                    val cantidadSemanal = edtCantidadSemanal.text.toString().toIntOrNull() ?: 10
                    val montoDiario = edtMontoDiario.text.toString().toDoubleOrNull() ?: 500.0
                    val montoSemanal = edtMontoSemanal.text.toString().toDoubleOrNull() ?: 2000.0
                    val porcentajeIngresos = edtPorcentajeIngresos.text.toString().toDoubleOrNull() ?: 15.0

                    GastosHormigaHelper.guardarLimiteCantidadDiario(this, cantidadDiaria)
                    GastosHormigaHelper.guardarLimiteCantidadSemanal(this, cantidadSemanal)
                    GastosHormigaHelper.guardarLimiteMontoDiario(this, montoDiario)
                    GastosHormigaHelper.guardarLimiteMontoSemanal(this, montoSemanal)
                    GastosHormigaHelper.guardarLimitePorcentajeIngresos(this, porcentajeIngresos)

                    Toast.makeText(this, "Umbrales actualizados", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoRestaurarLimites() {
        AlertDialog.Builder(this)
            .setTitle("Restaurar Valores Predeterminados")
            .setMessage("¿Estás seguro de restaurar todos los límites a sus valores predeterminados?")
            .setPositiveButton("Restaurar") { _, _ ->
                GastosHormigaHelper.restaurarLimitesPredeterminados(this)
                Toast.makeText(this, "Límites restaurados", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}

