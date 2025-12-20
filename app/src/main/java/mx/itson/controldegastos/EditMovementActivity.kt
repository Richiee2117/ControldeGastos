package mx.itson.controldegastos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import mx.itson.controldegastos.database.DatabaseHelper
import mx.itson.controldegastos.model.Movimiento

class EditMovementActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var edtMonto: TextInputEditText
    private lateinit var edtDescripcion: TextInputEditText
    private lateinit var spinnerTipo: Spinner
    private lateinit var spinnerCategoria: Spinner
    private lateinit var btnGuardar: Button
    private var movimientoId: Long = -1
    private var fechaOriginal: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_movement)

        dbHelper = DatabaseHelper(this)
        movimientoId = intent.getLongExtra("movimiento_id", -1)

        if (movimientoId == -1L) {
            finish()
            return
        }

        // Configurar barra de estado para que coincida con el toolbar
        window.statusBarColor = androidx.core.content.ContextCompat.getColor(this, R.color.primary)

        // Configurar toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.title = getString(R.string.editar_movimiento)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // Referencias a las vistas
        edtMonto = findViewById(R.id.edtMonto)
        edtDescripcion = findViewById(R.id.edtDescripcion)
        spinnerTipo = findViewById(R.id.spinnerTipo)
        spinnerCategoria = findViewById(R.id.spinnerCategoria)
        btnGuardar = findViewById(R.id.btnGuardar)

        // Configurar opciones del Spinner Tipo
        val tipos = arrayOf(getString(R.string.ingreso), getString(R.string.gasto))
        val adaptadorTipo = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipos)
        adaptadorTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipo.adapter = adaptadorTipo

        // Configurar opciones del Spinner Categoría
        val categorias = dbHelper.obtenerCategorias()
        val adaptadorCategoria = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias)
        adaptadorCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategoria.adapter = adaptadorCategoria

        // Cargar datos existentes
        cargarDatosMovimiento()

        btnGuardar.setOnClickListener {
            actualizarMovimiento()
        }
    }

    private fun cargarDatosMovimiento() {
        val movimiento = dbHelper.obtenerMovimientoPorId(movimientoId)
        val categorias = dbHelper.obtenerCategorias()
        if (movimiento != null) {
            fechaOriginal = movimiento.fecha
            edtMonto.setText(movimiento.monto.toString())
            edtDescripcion.setText(movimiento.descripcion)
            
            val posicionTipo = if (movimiento.tipo == getString(R.string.ingreso)) 0 else 1
            spinnerTipo.setSelection(posicionTipo)

            val posicionCategoria = categorias.indexOf(movimiento.categoria)
            if (posicionCategoria >= 0) {
                spinnerCategoria.setSelection(posicionCategoria)
            }
        }
    }

    private fun actualizarMovimiento() {
        val montoStr = edtMonto.text.toString().trim()
        val descripcion = edtDescripcion.text.toString().trim()
        val tipo = spinnerTipo.selectedItem.toString()
        val categoria = spinnerCategoria.selectedItem.toString()

        if (montoStr.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, R.string.campos_vacios, Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val monto = montoStr.toDouble()
            if (monto <= 0) {
                Toast.makeText(this, "El monto debe ser mayor a 0", Toast.LENGTH_SHORT).show()
                return
            }

            val movimiento = Movimiento(
                id = movimientoId,
                monto = monto,
                descripcion = descripcion,
                tipo = tipo,
                categoria = categoria,
                fecha = fechaOriginal // Mantener fecha original
            )

            val exito = dbHelper.actualizarMovimiento(movimiento)
            if (exito) {
                Toast.makeText(this, R.string.movimiento_actualizado, Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Error al actualizar el movimiento", Toast.LENGTH_SHORT).show()
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Ingresa un monto válido", Toast.LENGTH_SHORT).show()
        }
    }
}

