package mx.itson.controldegastos

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import mx.itson.controldegastos.adapter.MembershipAdapter
import mx.itson.controldegastos.database.DatabaseHelper
import mx.itson.controldegastos.model.Membership
import mx.itson.controldegastos.util.MembershipHelper
import mx.itson.controldegastos.util.MembershipReminderHelper
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MembershipsActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recyclerViewMembresias: RecyclerView
    private lateinit var adapter: MembershipAdapter
    private lateinit var tvTotalMensual: TextView
    private lateinit var tvTotalMembresias: TextView
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memberships)

        dbHelper = DatabaseHelper(this)

        // Configurar barra de estado para que coincida con el toolbar
        window.statusBarColor = androidx.core.content.ContextCompat.getColor(this, R.color.primary)

        // Configurar toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        recyclerViewMembresias = findViewById(R.id.recyclerViewMembresias)
        tvTotalMensual = findViewById(R.id.tvTotalMensual)
        tvTotalMembresias = findViewById(R.id.tvTotalMembresias)

        val fabAgregar = findViewById<FloatingActionButton>(R.id.fabAgregar)

        // Configurar RecyclerView
        adapter = MembershipAdapter(
            emptyList(),
            onEditClick = { membresia ->
                editarMembresia(membresia)
            },
            onDeleteClick = { membresia ->
                mostrarDialogoConfirmacionEliminar(membresia)
            },
            onItemClick = { membresia ->
                verDetallesMembresia(membresia)
            }
        )
        recyclerViewMembresias.layoutManager = LinearLayoutManager(this)
        recyclerViewMembresias.adapter = adapter

        // Configurar swipe to delete
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val membresia = adapter.membresias[position]
                mostrarDialogoConfirmacionEliminar(membresia)
                adapter.notifyItemChanged(position)
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerViewMembresias)

        fabAgregar.setOnClickListener {
            mostrarCatalogoMembresias()
        }

        // Programar recordatorios
        MembershipReminderHelper.programarRecordatorios(this)

        cargarMembresias()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_memberships, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_catalog -> {
                mostrarCatalogoMembresias()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun cargarMembresias() {
        val membresias = dbHelper.obtenerMembresias()
        adapter.updateMembresias(membresias)

        val totalMensual = dbHelper.obtenerTotalMembresiasMensual()
        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
        tvTotalMensual.text = "Total Mensual: ${formatter.format(totalMensual)}"
        tvTotalMembresias.text = "Membresías Activas: ${membresias.count { it.activa }}"
    }

    private fun mostrarDialogoAgregarMembresia() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_membership, null)
        val edtNombre = dialogView.findViewById<android.widget.EditText>(R.id.edtNombre)
        val edtMonto = dialogView.findViewById<android.widget.EditText>(R.id.edtMonto)
        val edtFecha = dialogView.findViewById<android.widget.EditText>(R.id.edtFecha)
        val spinnerFrecuencia = dialogView.findViewById<android.widget.Spinner>(R.id.spinnerFrecuencia)
        val edtDescripcion = dialogView.findViewById<android.widget.EditText>(R.id.edtDescripcion)

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
                        renovacionAutomatica = false,
                        recibirNotificaciones = true,
                        descripcion = descripcion,
                        activa = true
                    )

                    guardarMembresiaConAdvertencia(membresia) {
                        Toast.makeText(this, R.string.membresia_guardada, Toast.LENGTH_SHORT).show()
                        cargarMembresias()
                    }
                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "Ingresa un monto válido", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.cancelar, null)
            .create()

        dialog.show()
    }

    private fun mostrarCatalogoMembresias() {
        val intent = Intent(this, MembershipCatalogActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE_CATALOG)
    }

    private fun editarMembresia(membresia: Membership) {
        val intent = Intent(this, EditMembershipActivity::class.java)
        intent.putExtra("membership_id", membresia.id)
        startActivityForResult(intent, REQUEST_CODE_EDIT_MEMBERSHIP)
    }

    private fun mostrarDialogoConfirmacionEliminar(membresia: Membership) {
        AlertDialog.Builder(this)
            .setTitle(R.string.eliminar)
            .setMessage("¿Estás seguro de eliminar la membresía ${membresia.nombre}?")
            .setPositiveButton(R.string.eliminar) { _, _ ->
                eliminarMembresia(membresia)
            }
            .setNegativeButton(R.string.cancelar, null)
            .show()
    }

    private fun eliminarMembresia(membresia: Membership) {
        val exito = dbHelper.eliminarMembresia(membresia.id)
        if (exito) {
            Toast.makeText(this, R.string.membresia_eliminada, Toast.LENGTH_SHORT).show()
            cargarMembresias()
            // Notificar a MainActivity que se actualice
            setResult(RESULT_OK)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            cargarMembresias()
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

    private fun guardarMembresiaConAdvertencia(membresia: Membership, onSuccess: () -> Unit) {
        if (existeMembresiaActivaSimilar(membresia)) {
            AlertDialog.Builder(this)
                .setTitle(R.string.advertencia)
                .setMessage(R.string.advertencia_membresia_duplicada)
                .setPositiveButton(R.string.si_agregar) { _, _ ->
                    guardarMembresia(membresia, onSuccess)
                }
                .setNegativeButton(R.string.no_cancelar, null)
                .show()
        } else {
            guardarMembresia(membresia, onSuccess)
        }
    }

    private fun guardarMembresia(membresia: Membership, onSuccess: () -> Unit) {
        val id = dbHelper.insertarMembresia(membresia)
        if (id > 0) {
            onSuccess()
        } else {
            Toast.makeText(this, "Error al guardar la membresía", Toast.LENGTH_SHORT).show()
        }
    }

    private fun verDetallesMembresia(membresia: Membership) {
        val intent = Intent(this, MembershipDetailsActivity::class.java)
        intent.putExtra("membership_id", membresia.id)
        startActivityForResult(intent, REQUEST_CODE_MEMBERSHIP_DETAILS)
    }

    companion object {
        private const val REQUEST_CODE_CATALOG = 1
        private const val REQUEST_CODE_EDIT_MEMBERSHIP = 2
        private const val REQUEST_CODE_MEMBERSHIP_DETAILS = 3
    }
}

