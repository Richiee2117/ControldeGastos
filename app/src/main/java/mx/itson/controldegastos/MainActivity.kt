package mx.itson.controldegastos

import android.content.Intent
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mx.itson.controldegastos.adapter.MovimientoAdapter
import mx.itson.controldegastos.database.DatabaseHelper
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var tvTotalIngresos: TextView
    private lateinit var tvTotalGastos: TextView
    private lateinit var tvBalance: TextView
    private lateinit var recyclerViewMovimientos: RecyclerView
    private lateinit var adapter: MovimientoAdapter
    private lateinit var spinnerFiltro: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)

        // Configurar toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Referencias a las vistas
        tvTotalIngresos = findViewById(R.id.tvTotalIngresos)
        tvTotalGastos = findViewById(R.id.tvTotalGastos)
        tvBalance = findViewById(R.id.tvBalance)
        recyclerViewMovimientos = findViewById(R.id.recyclerViewMovimientos)
        spinnerFiltro = findViewById(R.id.spinnerFiltro)

        val btnAgregar = findViewById<Button>(R.id.btnAgregar)
        val btnGraficos = findViewById<Button>(R.id.btnGraficos)

        // Configurar RecyclerView
        adapter = MovimientoAdapter(emptyList())
        recyclerViewMovimientos.layoutManager = LinearLayoutManager(this)
        recyclerViewMovimientos.adapter = adapter

        // Configurar filtro
        val filtros = arrayOf("Todos", "Ingreso", "Gasto")
        val adapterFiltro = android.widget.ArrayAdapter(this, android.R.layout.simple_spinner_item, filtros)
        adapterFiltro.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFiltro.adapter = adapterFiltro

        spinnerFiltro.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                cargarMovimientos()
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }

        btnAgregar.setOnClickListener {
            val intent = Intent(this, AddMovementActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_MOVEMENT)
        }

        btnGraficos.setOnClickListener {
            val intent = Intent(this, GraphActivity::class.java)
            startActivity(intent)
        }

        // Cargar datos iniciales
        actualizarTotales()
        cargarMovimientos()
        
        // Verificar gastos hormiga al iniciar
        verificarGastosHormiga()
    }

    override fun onResume() {
        super.onResume()
        // Actualizar cuando se vuelve a la actividad
        actualizarTotales()
        cargarMovimientos()
        // Verificar gastos hormiga cada vez que se muestra la pantalla
        verificarGastosHormiga()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_MOVEMENT && resultCode == RESULT_OK) {
            // Actualizar después de agregar un movimiento
            actualizarTotales()
            cargarMovimientos()
            // Verificar gastos hormiga después de agregar un movimiento
            verificarGastosHormiga()
        }
    }

    private fun actualizarTotales() {
        val totalIngresos = dbHelper.obtenerTotalIngresos()
        val totalGastos = dbHelper.obtenerTotalGastos()
        val balance = totalIngresos - totalGastos

        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

        tvTotalIngresos.text = formatter.format(totalIngresos)
        tvTotalGastos.text = formatter.format(totalGastos)
        tvBalance.text = formatter.format(balance)
    }

    private fun cargarMovimientos() {
        val filtroSeleccionado = spinnerFiltro.selectedItem.toString()
        val tipoFiltro = when (filtroSeleccionado) {
            "Ingreso" -> "Ingreso"
            "Gasto" -> "Gasto"
            else -> null
        }

        val movimientos = if (tipoFiltro != null) {
            dbHelper.obtenerMovimientosFiltrados(tipoFiltro)
        } else {
            dbHelper.obtenerMovimientos()
        }

        adapter.updateMovimientos(movimientos)
    }

    private fun verificarGastosHormiga() {
        val calendar = Calendar.getInstance()
        val ahora = calendar.timeInMillis

        // Inicio del día de hoy
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val inicioDia = calendar.timeInMillis

        // Inicio de la semana (lunes)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val inicioSemana = calendar.timeInMillis

        // Obtener cantidad de gastos hormiga del día
        val gastosHormigaDia = dbHelper.obtenerGastosHormigaPorDia(inicioDia, ahora)
        
        // Obtener cantidad de gastos hormiga de la semana
        val gastosHormigaSemana = dbHelper.obtenerGastosHormigaPorDia(inicioSemana, ahora)

        // Si hay 3 o más gastos hormiga en el día, mostrar diálogo
        if (gastosHormigaDia >= 3) {
            mostrarDialogoGastosHormiga(gastosHormigaDia, true)
        }
        // Si hay 10 o más gastos hormiga en la semana, mostrar diálogo (solo si no se mostró el del día)
        else if (gastosHormigaSemana >= 10) {
            mostrarDialogoGastosHormiga(gastosHormigaSemana, false)
        }
    }

    private fun mostrarDialogoGastosHormiga(cantidad: Int, esDia: Boolean) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_gastos_hormiga, null)
        val tvMensaje = dialogView.findViewById<TextView>(R.id.tvMensajeGastosHormiga)
        val btnEntendido = dialogView.findViewById<Button>(R.id.btnEntendido)

        val mensaje = if (esDia) {
            getString(R.string.gastos_hormiga_mensaje, cantidad)
        } else {
            getString(R.string.gastos_hormiga_semana, cantidad)
        }

        tvMensaje.text = mensaje

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        btnEntendido.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    companion object {
        private const val REQUEST_CODE_ADD_MOVEMENT = 1
    }
}
