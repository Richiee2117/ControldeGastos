package mx.itson.controldegastos

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import mx.itson.controldegastos.database.DatabaseHelper
import mx.itson.controldegastos.model.Movimiento

class AddMovementActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var edtMonto: TextInputEditText
    private lateinit var edtDescripcion: TextInputEditText
    private lateinit var spinnerTipo: Spinner
    private lateinit var spinnerCategoria: Spinner
    private lateinit var btnGuardar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_movement)

        dbHelper = DatabaseHelper(this)

        // Configurar barra de estado para que coincida con el toolbar
        window.statusBarColor = androidx.core.content.ContextCompat.getColor(this, R.color.primary)

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
        spinnerCategoria = findViewById(R.id.spinnerCategoria)
        btnGuardar = findViewById(R.id.btnGuardar)

        // Configurar opciones del Spinner Tipo con colores del tema
        val tipos = arrayOf(getString(R.string.ingreso), getString(R.string.gasto))
        
        // Obtener color del texto del tema (cambia automáticamente con modo oscuro)
        val textColor = ContextCompat.getColor(this, R.color.on_surface)
        
        val adaptadorTipo = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tipos.toList()) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView?.setTextColor(textColor)
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView?.setTextColor(textColor)
                return view
            }
        }
        adaptadorTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipo.adapter = adaptadorTipo

        // Configurar opciones del Spinner Categoría con colores del tema
        val categorias = dbHelper.obtenerCategorias()
        val adaptadorCategoria = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categorias) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView?.setTextColor(textColor)
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView?.setTextColor(textColor)
                return view
            }
        }
        adaptadorCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategoria.adapter = adaptadorCategoria

        btnGuardar.setOnClickListener {
            guardarMovimiento()
        }
    }
    

    private fun guardarMovimiento() {
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
                monto = monto,
                descripcion = descripcion,
                tipo = tipo,
                categoria = categoria
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
