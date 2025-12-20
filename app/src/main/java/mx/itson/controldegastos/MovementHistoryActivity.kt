package mx.itson.controldegastos

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mx.itson.controldegastos.adapter.MovementGroupedAdapter
import mx.itson.controldegastos.database.DatabaseHelper
import mx.itson.controldegastos.model.Movimiento

class MovementHistoryActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recyclerViewMovimientos: RecyclerView
    private lateinit var adapter: MovementGroupedAdapter
    private var movementType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movement_history)

        // Configurar barra de estado para que coincida con el toolbar
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary)

        // Obtener tipo de movimiento del intent
        movementType = intent.getStringExtra("movement_type") ?: ""

        dbHelper = DatabaseHelper(this)

        // Configurar toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // Configurar título según el tipo
        val title = when (movementType) {
            "Ingreso" -> "Historial de Ingresos"
            "Gasto" -> "Historial de Gastos"
            else -> "Historial"
        }
        supportActionBar?.title = title

        // Configurar RecyclerView
        recyclerViewMovimientos = findViewById(R.id.recyclerViewMovimientos)
        recyclerViewMovimientos.layoutManager = LinearLayoutManager(this)

        cargarMovimientos()
    }

    private fun cargarMovimientos() {
        val movimientos = if (movementType.isNotEmpty()) {
            dbHelper.obtenerMovimientosFiltrados(movementType)
        } else {
            dbHelper.obtenerMovimientosFiltrados(null)
        }

        adapter = MovementGroupedAdapter(
            movimientos,
            onEditClick = { movimiento ->
                editarMovimiento(movimiento)
            },
            onDeleteClick = { movimiento ->
                mostrarDialogoConfirmacionEliminar(movimiento)
            }
        )
        recyclerViewMovimientos.adapter = adapter
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
        androidx.appcompat.app.AlertDialog.Builder(this)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EDIT_MOVEMENT && resultCode == RESULT_OK) {
            cargarMovimientos()
        }
    }

    override fun onResume() {
        super.onResume()
        cargarMovimientos()
    }

    companion object {
        private const val REQUEST_CODE_EDIT_MOVEMENT = 1
    }
}

