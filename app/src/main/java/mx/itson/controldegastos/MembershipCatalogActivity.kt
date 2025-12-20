package mx.itson.controldegastos

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import mx.itson.controldegastos.adapter.MembershipCatalogAdapter
import mx.itson.controldegastos.data.MembershipCatalog
import mx.itson.controldegastos.database.DatabaseHelper
import mx.itson.controldegastos.model.Membership
import mx.itson.controldegastos.util.MembershipHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MembershipCatalogActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recyclerViewCatalogo: RecyclerView
    private lateinit var adapter: MembershipCatalogAdapter
    private lateinit var edtBuscar: TextInputEditText
    private lateinit var fabAgregarPersonalizada: FloatingActionButton
    private var todasLasMembresias: List<MembershipCatalog.MembershipPlan> = emptyList()
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    companion object {
        private const val TAG = "MembershipCatalog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_membership_catalog)

        dbHelper = DatabaseHelper(this)

        // Configurar barra de estado para que coincida con el toolbar
        window.statusBarColor = androidx.core.content.ContextCompat.getColor(this, R.color.primary)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        recyclerViewCatalogo = findViewById(R.id.recyclerViewCatalogo)
        edtBuscar = findViewById(R.id.edtBuscar)
        fabAgregarPersonalizada = findViewById(R.id.fabAgregarPersonalizada)

        val servicios = MembershipCatalog.obtenerServicios()
        Log.d(TAG, "Servicios encontrados: ${servicios.size}")

        todasLasMembresias = servicios
            .flatMap { servicio ->
                val planes = MembershipCatalog.obtenerPlanes(servicio)
                Log.d(TAG, "Servicio: $servicio tiene ${planes.size} planes")
                planes
            }
            .sortedWith(compareBy(
                { it.tipo },
                { it.nombre },
                {
                    when (it.frecuencia) {
                        "Mensual" -> 0
                        "Trimestral" -> 1
                        "Semestral" -> 2
                        "Anual" -> 3
                        else -> 4
                    }
                }
            ))

        Log.d(TAG, "Total de membresías cargadas: ${todasLasMembresias.size}")

        adapter = MembershipCatalogAdapter(
            todasLasMembresias,
            onItemClick = { plan ->
                seleccionarFechaParaPlan(plan)
            }
        )
        recyclerViewCatalogo.layoutManager = LinearLayoutManager(this)
        recyclerViewCatalogo.adapter = adapter

        Log.d(TAG, "Adapter configurado con ${adapter.itemCount} items")

        edtBuscar.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                imm.showSoftInput(edtBuscar, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
            } else {
                filtrarMembresias(edtBuscar.text?.toString() ?: "")
            }
        }

        edtBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filtrarMembresias(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {
                filtrarMembresias(s?.toString() ?: "")
            }
        })

        edtBuscar.setOnClickListener {
            edtBuscar.requestFocus()
            val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.showSoftInput(edtBuscar, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
        }

        fabAgregarPersonalizada.setOnClickListener {
            mostrarDialogoAgregarMembresiaPersonalizada()
        }

        if (todasLasMembresias.isEmpty()) {
            Toast.makeText(this, "No se encontraron membresías en el catálogo", Toast.LENGTH_LONG).show()
            Log.e(TAG, "ERROR: No hay membresías en el catálogo")
        } else {
            Log.d(TAG, "Catálogo cargado exitosamente con ${todasLasMembresias.size} membresías")
        }
    }

    private fun filtrarMembresias(texto: String) {
        val textoBusqueda = texto.lowercase().trim()
        Log.d(TAG, "Filtrando con texto: '$textoBusqueda'")

        val membresiasFiltradas = if (textoBusqueda.isEmpty()) {
            todasLasMembresias
        } else {
            todasLasMembresias.filter {
                val tipoMatch = it.tipo.lowercase().contains(textoBusqueda)
                val nombreMatch = it.nombre.lowercase().contains(textoBusqueda)
                val frecuenciaMatch = it.frecuencia.lowercase().contains(textoBusqueda)
                tipoMatch || nombreMatch || frecuenciaMatch
            }
        }

        Log.d(TAG, "Resultados filtrados: ${membresiasFiltradas.size} de ${todasLasMembresias.size}")
        adapter.updateMembresias(membresiasFiltradas)
    }

    private fun mostrarDialogoAgregarMembresiaPersonalizada() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_membership, null)
        val edtNombre = dialogView.findViewById<EditText>(R.id.edtNombre)
        val edtMonto = dialogView.findViewById<EditText>(R.id.edtMonto)
        val edtFecha = dialogView.findViewById<EditText>(R.id.edtFecha)
        val spinnerFrecuencia = dialogView.findViewById<Spinner>(R.id.spinnerFrecuencia)
        val edtDescripcion = dialogView.findViewById<EditText>(R.id.edtDescripcion)

        val frecuencias = arrayOf("Mensual", "Trimestral", "Semestral", "Anual")
        val adapterFrecuencia = android.widget.ArrayAdapter(this, android.R.layout.simple_spinner_item, frecuencias)
        adapterFrecuencia.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFrecuencia.adapter = adapterFrecuencia

        var fechaSeleccionada = normalizarFecha(System.currentTimeMillis())
        edtFecha.setText(dateFormatter.format(Date(fechaSeleccionada)))
        edtFecha.setOnClickListener {
            mostrarSelectorFecha(fechaSeleccionada) { nuevaFecha ->
                fechaSeleccionada = nuevaFecha
                edtFecha.setText(dateFormatter.format(Date(fechaSeleccionada)))
            }
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.agregar_membresia)
            .setView(dialogView)
            .setPositiveButton(R.string.guardar) { _, _ ->
                val nombre = edtNombre.text.toString().trim()
                val montoStr = edtMonto.text.toString().trim()
                val frecuencia = spinnerFrecuencia.selectedItem.toString()
                val descripcion = edtDescripcion.text.toString().trim()

                if (nombre.isEmpty() || montoStr.isEmpty()) {
                    Toast.makeText(this, R.string.campos_vacios, Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                try {
                    val monto = montoStr.toDouble()
                    val proximoVencimiento = MembershipHelper.calcularProximoVencimiento(fechaSeleccionada, frecuencia)

                    val membresia = Membership(
                        nombre = nombre,
                        tipo = "Personalizada",
                        monto = monto,
                        frecuencia = frecuencia,
                        fechaPago = fechaSeleccionada,
                        proximoVencimiento = proximoVencimiento,
                        descripcion = descripcion,
                        activa = true
                    )

                    guardarMembresiaConAdvertencia(membresia)
                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "Ingresa un monto válido", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.cancelar, null)
            .create()

        dialog.show()
    }

    private fun seleccionarFechaParaPlan(plan: MembershipCatalog.MembershipPlan) {
        val fechaInicial = normalizarFecha(System.currentTimeMillis())
        mostrarSelectorFecha(fechaInicial) { fechaSeleccionada ->
            val proximoVencimiento = MembershipHelper.calcularProximoVencimiento(fechaSeleccionada, plan.frecuencia)
            val membresia = Membership(
                nombre = plan.nombre,
                tipo = plan.tipo,
                monto = plan.monto,
                frecuencia = plan.frecuencia,
                fechaPago = fechaSeleccionada,
                proximoVencimiento = proximoVencimiento,
                descripcion = "Plan ${plan.tipo} - ${plan.frecuencia}",
                activa = true,
                renovacionAutomatica = false,
                recibirNotificaciones = true
            )
            guardarMembresiaConAdvertencia(membresia)
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

    private fun normalizarFecha(millis: Long): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = millis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    private fun existeMembresiaActivaSimilar(membresia: Membership): Boolean {
        val existentes = dbHelper.obtenerMembresias()
        return existentes.any { existente ->
            if (!existente.activa) return@any false
            val mismoTipo = existente.tipo.equals(membresia.tipo, ignoreCase = true)
            if (membresia.tipo.equals("Personalizada", ignoreCase = true)) {
                mismoTipo && existente.nombre.equals(membresia.nombre, ignoreCase = true)
            } else {
                mismoTipo
            }
        }
    }

    private fun guardarMembresiaConAdvertencia(membresia: Membership) {
        if (existeMembresiaActivaSimilar(membresia)) {
            AlertDialog.Builder(this)
                .setTitle(R.string.advertencia)
                .setMessage(R.string.advertencia_membresia_duplicada)
                .setPositiveButton(R.string.si_agregar) { _, _ -> guardarMembresia(membresia) }
                .setNegativeButton(R.string.no_cancelar, null)
                .show()
        } else {
            guardarMembresia(membresia)
        }
    }

    private fun guardarMembresia(membresia: Membership) {
        val id = dbHelper.insertarMembresia(membresia)
        if (id > 0) {
            Toast.makeText(this, R.string.membresia_guardada, Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        } else {
            Toast.makeText(this, "Error al guardar la membresía", Toast.LENGTH_SHORT).show()
        }
    }
}
