package mx.itson.controldegastos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import mx.itson.controldegastos.database.DatabaseHelper
import mx.itson.controldegastos.model.Movimiento

class AddMovementActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var edtMonto: TextInputEditText
    private lateinit var edtDescripcion: TextInputEditText
    private lateinit var spinnerTipo: Spinner
    private lateinit var btnGuardar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_movement)

        dbHelper = DatabaseHelper(this)

        // Configurar toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // Referencias a las vistas
        edtMonto = findViewById(R.id.edtMonto)
        edtDescripcion = findViewById(R.id.edtDescripcion)
        spinnerTipo = findViewById(R.id.spinnerTipo)
        btnGuardar = findViewById(R.id.btnGuardar)

        // Configurar opciones del Spinner
        val tipos = arrayOf(getString(R.string.ingreso), getString(R.string.gasto))
        val adaptador = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipos)
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipo.adapter = adaptador

        btnGuardar.setOnClickListener {
            guardarMovimiento()
        }
    }

    private fun guardarMovimiento() {
        val montoStr = edtMonto.text.toString().trim()
        val descripcion = edtDescripcion.text.toString().trim()
        val tipo = spinnerTipo.selectedItem.toString()

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
                monto = monto,
                descripcion = descripcion,
                tipo = tipo
            )

            val id = dbHelper.insertarMovimiento(movimiento)
            if (id > 0) {
                Toast.makeText(this, R.string.movimiento_guardado, Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Error al guardar el movimiento", Toast.LENGTH_SHORT).show()
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Ingresa un monto válido", Toast.LENGTH_SHORT).show()
        }
    }
}
