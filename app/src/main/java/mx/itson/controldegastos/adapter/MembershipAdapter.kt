package mx.itson.controldegastos.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mx.itson.controldegastos.R
import mx.itson.controldegastos.model.Membership
import mx.itson.controldegastos.util.MembershipHelper
import java.text.NumberFormat
import java.util.Locale

class MembershipAdapter(
    var membresias: List<Membership>,
    private val onEditClick: (Membership) -> Unit,
    private val onDeleteClick: (Membership) -> Unit,
    private val onItemClick: (Membership) -> Unit = {}
) : RecyclerView.Adapter<MembershipAdapter.MembershipViewHolder>() {

    class MembershipViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombreMembership)
        val tvTipo: TextView = itemView.findViewById(R.id.tvTipoMembership)
        val tvFrecuencia: TextView = itemView.findViewById(R.id.tvFrecuenciaMembership)
        val tvMonto: TextView = itemView.findViewById(R.id.tvMontoMembership)
        val tvMontoMensual: TextView = itemView.findViewById(R.id.tvMontoMensualMembership)
        val tvEstado: TextView = itemView.findViewById(R.id.tvEstadoMembership)
        val btnEditar: ImageButton = itemView.findViewById(R.id.btnEditarMembership)
        val imgLogo: ImageView = itemView.findViewById(R.id.imgLogoEncabezado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MembershipViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_membership, parent, false)
        return MembershipViewHolder(view)
    }

    override fun onBindViewHolder(holder: MembershipViewHolder, position: Int) {
        val membresia = membresias[position]
        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

        holder.tvNombre.text = membresia.tipo
        holder.tvTipo.text = membresia.nombre
        holder.tvFrecuencia.text = membresia.frecuencia
        holder.tvMonto.text = formatter.format(membresia.monto)

        val montoMensual = MembershipHelper.convertirAMensual(membresia.monto, membresia.frecuencia)
        holder.tvMontoMensual.text = "(${formatter.format(montoMensual)}/mes)"

        val logoRes = MembershipHelper.obtenerLogoMembresia(membresia.tipo, membresia.nombre)
        if (logoRes != null) {
            holder.imgLogo.visibility = View.VISIBLE
            holder.imgLogo.setImageResource(logoRes)
        } else {
            holder.imgLogo.setImageDrawable(null)
            holder.imgLogo.visibility = View.GONE
        }

        // Mostrar estado "Próxima a vencer"
        if (MembershipHelper.estaProximaAVencer(membresia, 3)) {
            holder.tvEstado.visibility = View.VISIBLE
            holder.tvEstado.text = "Próxima a vencer"
        } else {
            holder.tvEstado.visibility = View.GONE
        }

        holder.btnEditar.setOnClickListener {
            onEditClick(membresia)
        }

        // Hacer clickable toda la tarjeta para ver detalles
        holder.itemView.setOnClickListener {
            onItemClick(membresia)
        }
    }

    override fun getItemCount(): Int = membresias.size

    fun updateMembresias(nuevasMembresias: List<Membership>) {
        membresias = nuevasMembresias
        notifyDataSetChanged()
    }

    fun removeMembresia(position: Int) {
        val newList = membresias.toMutableList()
        newList.removeAt(position)
        membresias = newList
        notifyItemRemoved(position)
    }
}

