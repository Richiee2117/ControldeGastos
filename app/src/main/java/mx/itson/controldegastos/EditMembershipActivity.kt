package mx.itson.controldegastos

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import mx.itson.controldegastos.database.DatabaseHelper
import mx.itson.controldegastos.model.Membership
import mx.itson.controldegastos.util.MembershipHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditMembershipActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var edtNombre: TextInputEditText
    private lateinit var edtMonto: TextInputEditText
    private lateinit var edtFechaPago: TextInputEditText
    private lateinit var spinnerFrecuencia: Spinner
    private lateinit var edtDescripcion: TextInputEditText
    private lateinit var btnGuardar: Button
    private var membresiaId: Long = -1
    private var fechaSeleccionada: Long = System.currentTimeMillis()
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_membership)

        dbHelper = DatabaseHelper(this)
        membresiaId = intent.getLongExtra("membership_id", -1)

        if (membresiaId == -1L) {
            finish()
            return
        }

        // Configurar barra de estado para que coincida con el toolbar
        window.statusBarColor = androidx.core.content.ContextCompat.getColor(this, R.color.primary)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.title = getString(R.string.editar_membresia)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        edtNombre = findViewById(R.id.edtNombre)
        edtMonto = findViewById(R.id.edtMonto)
        edtFechaPago = findViewById(R.id.edtFechaPago)
        spinnerFrecuencia = findViewById(R.id.spinnerFrecuencia)
        edtDescripcion = findViewById(R.id.edtDescripcion)
        btnGuardar = findViewById(R.id.btnGuardar)

        val frecuencias = arrayOf("Mensual", "Trimestral", "Semestral", "Anual")
        val adapterFrecuencia = ArrayAdapter(this, android.R.layout.simple_spinner_item, frecuencias)
        adapterFrecuencia.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFrecuencia.adapter = adapterFrecuencia

        cargarDatosMembresia()

        edtFechaPago.setOnClickListener {
            mostrarSelectorFecha(fechaSeleccionada) { nuevaFecha ->
                fechaSeleccionada = nuevaFecha
                edtFechaPago.setText(dateFormatter.format(fechaSeleccionada))
            }
        }

        btnGuardar.setOnClickListener {
            actualizarMembresia()
        }
    }

    private fun cargarDatosMembresia() {
        val membresia = dbHelper.obtenerMembresiaPorId(membresiaId)
        if (membresia != null) {
            edtNombre.setText(membresia.nombre)
            edtMonto.setText(membresia.monto.toString())
            edtDescripcion.setText(membresia.descripcion)
            fechaSeleccionada = membresia.fechaPago
            edtFechaPago.setText(dateFormatter.format(membresia.fechaPago))

            val posicionFrecuencia = when (membresia.frecuencia) {
                "Mensual" -> 0
                "Trimestral" -> 1
                "Semestral" -> 2
                "Anual" -> 3
                else -> 0
            }
            spinnerFrecuencia.setSelection(posicionFrecuencia)
        }
    }

    private fun actualizarMembresia() {
        val nombre = edtNombre.text.toString().trim()
        val montoStr = edtMonto.text.toString().trim()
        val frecuencia = spinnerFrecuencia.selectedItem.toString()
        val descripcion = edtDescripcion.text.toString().trim()

        if (nombre.isEmpty() || montoStr.isEmpty()) {
            Toast.makeText(this, R.string.campos_vacios, Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val monto = montoStr.toDouble()
            val membresia = dbHelper.obtenerMembresiaPorId(membresiaId)
            if (membresia != null) {
                val proximoVencimiento = MembershipHelper.calcularProximoVencimiento(
                    fechaSeleccionada,
                    frecuencia
                )

                val membresiaActualizada = membresia.copy(
                    nombre = nombre,
                    monto = monto,
                    frecuencia = frecuencia,
                    descripcion = descripcion,
                    fechaPago = fechaSeleccionada,
                    proximoVencimiento = proximoVencimiento
                )

                val exito = dbHelper.actualizarMembresia(membresiaActualizada)
                if (exito) {
                    Toast.makeText(this, R.string.membresia_actualizada, Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Ingresa un monto válido", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarSelectorFecha(fechaInicial: Long, onFechaSeleccionada: (Long) -> Unit) {
        val calendario = Calendar.getInstance().apply { timeInMillis = fechaInicial }
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val cal = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth, 0, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                onFechaSeleccionada(cal.timeInMillis)
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}

