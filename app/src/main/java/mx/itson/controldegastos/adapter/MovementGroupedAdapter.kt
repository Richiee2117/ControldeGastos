package mx.itson.controldegastos.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mx.itson.controldegastos.R
import mx.itson.controldegastos.model.Movimiento
import java.text.SimpleDateFormat
import java.util.*

class MovementGroupedAdapter(
    private var movimientos: List<Movimiento>,
    private val onEditClick: (Movimiento) -> Unit,
    private val onDeleteClick: (Movimiento) -> Unit
) : RecyclerView.Adapter<MovementGroupedAdapter.DateGroupViewHolder>() {

    private var groupedMovements: Map<String, List<Movimiento>> = groupByDate(movimientos)
    private var dateKeys: List<String> = groupedMovements.keys.sortedDescending()

    class DateGroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFechaHeader: TextView = itemView.findViewById(R.id.tvFechaHeader)
        val recyclerViewMovimientos: RecyclerView = itemView.findViewById(R.id.recyclerViewMovimientosFecha)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateGroupViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movement_grouped, parent, false)
        return DateGroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: DateGroupViewHolder, position: Int) {
        val fecha = dateKeys[position]
        val movimientosFecha = groupedMovements[fecha] ?: emptyList()

        // Formatear fecha para mostrar
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateFormatterDisplay = SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy", Locale("es", "MX"))
        
        try {
            val date = dateFormatter.parse(fecha)
            if (date != null) {
                holder.tvFechaHeader.text = dateFormatterDisplay.format(date).replaceFirstChar { 
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() 
                }
            } else {
                holder.tvFechaHeader.text = fecha
            }
        } catch (e: Exception) {
            holder.tvFechaHeader.text = fecha
        }

        // Configurar RecyclerView interno con los movimientos de esta fecha
        holder.recyclerViewMovimientos.layoutManager = LinearLayoutManager(holder.itemView.context)
        val adapter = MovimientoAdapter(
            movimientosFecha,
            onEditClick = { movimiento ->
                onEditClick(movimiento)
            },
            onDeleteClick = { movimiento ->
                onDeleteClick(movimiento)
            }
        )
        holder.recyclerViewMovimientos.adapter = adapter
        holder.recyclerViewMovimientos.isNestedScrollingEnabled = false
    }

    override fun getItemCount(): Int = dateKeys.size

    private fun groupByDate(movimientos: List<Movimiento>): Map<String, List<Movimiento>> {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return movimientos.groupBy { movimiento ->
            dateFormatter.format(Date(movimiento.fecha))
        }
    }

    fun updateMovimientos(nuevosMovimientos: List<Movimiento>) {
        movimientos = nuevosMovimientos
        groupedMovements = groupByDate(movimientos)
        dateKeys = groupedMovements.keys.sortedDescending()
        notifyDataSetChanged()
    }
}

