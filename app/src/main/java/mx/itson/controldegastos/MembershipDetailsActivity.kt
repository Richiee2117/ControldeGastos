package mx.itson.controldegastos

import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import mx.itson.controldegastos.database.DatabaseHelper
import mx.itson.controldegastos.model.Membership
import mx.itson.controldegastos.util.MembershipHelper
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class MembershipDetailsActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var membresiaId: Long = -1
    private lateinit var membresia: Membership

    private lateinit var tvNombreDetalle: TextView
    private lateinit var tvTipoDetalle: TextView
    private lateinit var tvMontoDetalle: TextView
    private lateinit var tvFrecuenciaDetalle: TextView
    private lateinit var tvFechaPagoDetalle: TextView
    private lateinit var tvProximoVencimientoDetalle: TextView
    private lateinit var switchNotificaciones: Switch
    private lateinit var switchRenovacionAutomatica: Switch
    private lateinit var btnGuardar: MaterialButton

    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val formatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_membership_details)

        dbHelper = DatabaseHelper(this)
        membresiaId = intent.getLongExtra("membership_id", -1)

        if (membresiaId == -1L) {
            Toast.makeText(this, "Error: Membresía no encontrada", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Configurar barra de estado
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // Referencias a las vistas
        tvNombreDetalle = findViewById(R.id.tvNombreDetalle)
        tvTipoDetalle = findViewById(R.id.tvTipoDetalle)
        tvMontoDetalle = findViewById(R.id.tvMontoDetalle)
        tvFrecuenciaDetalle = findViewById(R.id.tvFrecuenciaDetalle)
        tvFechaPagoDetalle = findViewById(R.id.tvFechaPagoDetalle)
        tvProximoVencimientoDetalle = findViewById(R.id.tvProximoVencimientoDetalle)
        switchNotificaciones = findViewById(R.id.switchNotificaciones)
        switchRenovacionAutomatica = findViewById(R.id.switchRenovacionAutomatica)
        btnGuardar = findViewById(R.id.btnGuardarConfiguracion)

        cargarMembresia()
        configurarListeners()
    }

    private fun cargarMembresia() {
        val membresiaCargada = dbHelper.obtenerMembresiaPorId(membresiaId)
        if (membresiaCargada == null) {
            Toast.makeText(this, "Error: Membresía no encontrada", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        membresia = membresiaCargada

        // Mostrar información
        tvNombreDetalle.text = membresia.nombre
        tvTipoDetalle.text = membresia.tipo
        tvMontoDetalle.text = formatter.format(membresia.monto)
        tvFrecuenciaDetalle.text = membresia.frecuencia
        tvFechaPagoDetalle.text = dateFormatter.format(membresia.fechaPago)
        tvProximoVencimientoDetalle.text = dateFormatter.format(membresia.proximoVencimiento)

        // Configurar switches
        switchNotificaciones.isChecked = membresia.recibirNotificaciones
        switchRenovacionAutomatica.isChecked = membresia.renovacionAutomatica
    }

    private fun configurarListeners() {
        btnGuardar.setOnClickListener {
            guardarConfiguracion()
        }
    }

    private fun guardarConfiguracion() {
        val membresiaActualizada = membresia.copy(
            recibirNotificaciones = switchNotificaciones.isChecked,
            renovacionAutomatica = switchRenovacionAutomatica.isChecked
        )

        val exito = dbHelper.actualizarMembresia(membresiaActualizada)
        if (exito) {
            Toast.makeText(this, "Configuración guardada exitosamente", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        } else {
            Toast.makeText(this, "Error al guardar la configuración", Toast.LENGTH_SHORT).show()
        }
    }
}

