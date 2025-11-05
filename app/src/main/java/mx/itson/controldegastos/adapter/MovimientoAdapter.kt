package mx.itson.controldegastos.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mx.itson.controldegastos.R
import mx.itson.controldegastos.model.Movimiento
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class MovimientoAdapter(private var movimientos: List<Movimiento>) : 
    RecyclerView.Adapter<MovimientoAdapter.MovimientoViewHolder>() {

    class MovimientoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        val tvMonto: TextView = itemView.findViewById(R.id.tvMonto)
        val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        val tvIcono: TextView = itemView.findViewById(R.id.tvIcono)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovimientoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movement, parent, false)
        return MovimientoViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovimientoViewHolder, position: Int) {
        val movimiento = movimientos[position]
        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        holder.tvDescripcion.text = movimiento.descripcion
        holder.tvMonto.text = formatter.format(movimiento.monto)
        holder.tvFecha.text = dateFormatter.format(Date(movimiento.fecha))

        // Configurar color e icono según tipo
        if (movimiento.tipo == "Ingreso") {
            holder.tvMonto.setTextColor(holder.itemView.context.getColor(R.color.income_color))
            holder.tvIcono.text = "↑"
        } else {
            holder.tvMonto.setTextColor(holder.itemView.context.getColor(R.color.expense_color))
            holder.tvIcono.text = "↓"
        }
    }

    override fun getItemCount(): Int = movimientos.size

    fun updateMovimientos(nuevosMovimientos: List<Movimiento>) {
        movimientos = nuevosMovimientos
        notifyDataSetChanged()
    }
}

