package mx.itson.controldegastos

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mx.itson.controldegastos.adapter.MovementGroupedAdapter
import mx.itson.controldegastos.database.DatabaseHelper
import mx.itson.controldegastos.model.Movimiento

class MovementsActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recyclerViewMovimientos: RecyclerView
    private lateinit var adapter: MovementGroupedAdapter
    private lateinit var spinnerFiltro: Spinner
    private var textoBusqueda: String = ""

    companion object {
        const val REQUEST_CODE_EDIT_MOVEMENT = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movements)

        dbHelper = DatabaseHelper(this)

        // Configurar barra de estado para que coincida con el toolbar
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary)
        
        // Configurar toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // Referencias a las vistas
        recyclerViewMovimientos = findViewById(R.id.recyclerViewMovimientos)
        spinnerFiltro = findViewById(R.id.spinnerFiltro)

        // Configurar RecyclerView
        adapter = MovementGroupedAdapter(
            emptyList(),
            onEditClick = { movimiento ->
                editarMovimiento(movimiento)
            },
            onDeleteClick = { movimiento ->
                mostrarDialogoConfirmacionEliminar(movimiento)
            }
        )
        recyclerViewMovimientos.layoutManager = LinearLayoutManager(this)
        recyclerViewMovimientos.adapter = adapter

        // Configurar filtro con colores del tema
        val filtros = arrayOf("Todos", "Ingreso", "Gasto", "Membresías")
        val textColor = ContextCompat.getColor(this, R.color.on_surface)
        
        val adapterFiltro = object : android.widget.ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, filtros.toList()) {
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
        adapterFiltro.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFiltro.adapter = adapterFiltro

        spinnerFiltro.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                cargarMovimientos()
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }

        // Cargar datos iniciales
        cargarMovimientos()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_movements, menu)
        
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        
        // Configurar colores del SearchView para que se adapte al tema
        val searchEditText = searchView.findViewById<android.widget.EditText>(androidx.appcompat.R.id.search_src_text)
        val searchPlate = searchView.findViewById<android.view.View>(androidx.appcompat.R.id.search_plate)
        val searchIcon = searchView.findViewById<android.widget.ImageView>(androidx.appcompat.R.id.search_mag_icon)
        val closeButton = searchView.findViewById<android.widget.ImageView>(androidx.appcompat.R.id.search_close_btn)
        
        // Verificar si el modo oscuro está activado
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val modoOscuroActivado = sharedPreferences.getBoolean("modo_oscuro", false)
        
        // Configurar colores según el tema
        // El SearchView está en el toolbar (fondo oscuro), así que necesita texto claro/blanco
        // Cuando se expande fuera del toolbar, usará los colores del tema
        val textColor = if (modoOscuroActivado) {
            // Modo oscuro: texto blanco/claro
            ContextCompat.getColor(this, R.color.on_surface_dark)
        } else {
            // Modo claro: texto blanco para contrastar con el toolbar oscuro
            ContextCompat.getColor(this, R.color.white)
        }
        
        val hintColor = if (modoOscuroActivado) {
            // Modo oscuro: hint claro
            ContextCompat.getColor(this, R.color.on_surface_variant_dark)
        } else {
            // Modo claro: hint claro con transparencia para contrastar con el toolbar oscuro
            val whiteColor = ContextCompat.getColor(this, R.color.white)
            android.graphics.Color.argb(180, android.graphics.Color.red(whiteColor), android.graphics.Color.green(whiteColor), android.graphics.Color.blue(whiteColor))
        }
        
        searchEditText?.setTextColor(textColor)
        searchEditText?.setHintTextColor(hintColor)
        
        // Configurar color del icono de búsqueda
        searchIcon?.setColorFilter(textColor)
        
        // Configurar color del botón de cerrar
        closeButton?.setColorFilter(textColor)
        
        // Configurar fondo del SearchView si es necesario
        searchPlate?.setBackgroundColor(androidx.core.content.ContextCompat.getColor(this, android.R.color.transparent))
        
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                textoBusqueda = newText ?: ""
                cargarMovimientos()
                return true
            }
        })
        
        return true
    }

    override fun onResume() {
        super.onResume()
        cargarMovimientos()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EDIT_MOVEMENT && resultCode == RESULT_OK) {
            cargarMovimientos()
        }
    }

    private fun editarMovimiento(movimiento: Movimiento) {
        // No permitir editar movimientos de membresía
        if (movimiento.categoria == "Membresías") {
            Toast.makeText(this, "No se puede editar una membresía desde aquí", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(this, EditMovementActivity::class.java)
        intent.putExtra("movimiento_id", movimiento.id)
        startActivityForResult(intent, REQUEST_CODE_EDIT_MOVEMENT)
    }

    private fun mostrarDialogoConfirmacionEliminar(movimiento: Movimiento) {
        AlertDialog.Builder(this)
            .setTitle(R.string.eliminar)
            .setMessage(R.string.confirmar_eliminacion)
            .setPositiveButton(R.string.eliminar) { _, _ ->
                eliminarMovimiento(movimiento)
            }
            .setNegativeButton(R.string.cancelar, null)
            .show()
    }

    private fun eliminarMovimiento(movimiento: Movimiento) {
        // Si es una membresía, eliminar de la tabla de membresías
        if (movimiento.categoria == "Membresías") {
            val exito = dbHelper.eliminarMembresia(movimiento.id)
            if (exito) {
                Toast.makeText(this, "Membresía eliminada", Toast.LENGTH_SHORT).show()
                cargarMovimientos()
                // Notificar a otras actividades que se actualicen
                setResult(RESULT_OK)
            } else {
                Toast.makeText(this, "Error al eliminar la membresía", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Eliminar movimiento normal
            val exito = dbHelper.eliminarMovimiento(movimiento.id)
            if (exito) {
                Toast.makeText(this, R.string.movimiento_eliminado, Toast.LENGTH_SHORT).show()
                cargarMovimientos()
            } else {
                Toast.makeText(this, "Error al eliminar el movimiento", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarMovimientos() {
        val filtroSeleccionado = spinnerFiltro.selectedItem.toString()
        val tipoFiltro = when (filtroSeleccionado) {
            "Ingreso" -> "Ingreso"
            "Gasto" -> "Gasto"
            "Membresías" -> "Membresías"
            else -> null
        }

        val movimientos = if (textoBusqueda.isNotEmpty()) {
            // Si hay búsqueda, usar método de búsqueda
            val movimientosNormales = dbHelper.buscarMovimientos(textoBusqueda, tipoFiltro)
            if (tipoFiltro == null || tipoFiltro == "Membresías") {
                // Incluir membresías en la búsqueda si el filtro es "Todos" o "Membresías"
                val membresias = dbHelper.obtenerMembresias().filter { it.activa }
                val membresiasComoMovimientos = membresias.map { membresia ->
                    val montoMensual = mx.itson.controldegastos.util.MembershipHelper.convertirAMensual(membresia.monto, membresia.frecuencia)
                    mx.itson.controldegastos.model.Movimiento(
                        id = membresia.id,
                        monto = montoMensual,
                        descripcion = membresia.nombre,
                        tipo = "Gasto",
                        categoria = "Membresías",
                        fecha = membresia.fechaPago
                    )
                }.filter { it.descripcion.lowercase().contains(textoBusqueda.lowercase()) }
                
                if (tipoFiltro == null) {
                    movimientosNormales + membresiasComoMovimientos
                } else {
                    membresiasComoMovimientos
                }
            } else {
                movimientosNormales
            }
        } else {
            // Si no hay búsqueda, usar filtrado normal
            if (tipoFiltro == "Membresías") {
                // Para membresías, obtener de la tabla de membresías y convertir a movimientos
                val membresias = dbHelper.obtenerMembresias().filter { it.activa }
                membresias.map { membresia ->
                    val montoMensual = mx.itson.controldegastos.util.MembershipHelper.convertirAMensual(membresia.monto, membresia.frecuencia)
                    mx.itson.controldegastos.model.Movimiento(
                        id = membresia.id,
                        monto = montoMensual,
                        descripcion = membresia.nombre,
                        tipo = "Gasto",
                        categoria = "Membresías",
                        fecha = membresia.fechaPago
                    )
                }.toList()
            } else if (tipoFiltro == null) {
                // Si es "Todos", combinar movimientos normales con membresías
                val movimientosNormales = dbHelper.obtenerMovimientosFiltrados(null)
                val membresias = dbHelper.obtenerMembresias().filter { it.activa }
                val membresiasComoMovimientos = membresias.map { membresia ->
                    val montoMensual = mx.itson.controldegastos.util.MembershipHelper.convertirAMensual(membresia.monto, membresia.frecuencia)
                    mx.itson.controldegastos.model.Movimiento(
                        id = membresia.id,
                        monto = montoMensual,
                        descripcion = membresia.nombre,
                        tipo = "Gasto",
                        categoria = "Membresías",
                        fecha = membresia.fechaPago
                    )
                }
                (movimientosNormales + membresiasComoMovimientos).sortedByDescending { it.fecha }
            } else {
                dbHelper.obtenerMovimientosFiltrados(tipoFiltro)
            }
        }

        adapter.updateMovimientos(movimientos)
    }
}

