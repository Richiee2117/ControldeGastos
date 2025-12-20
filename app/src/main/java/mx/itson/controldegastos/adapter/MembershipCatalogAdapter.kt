package mx.itson.controldegastos.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mx.itson.controldegastos.R
import mx.itson.controldegastos.data.MembershipCatalog
import mx.itson.controldegastos.util.MembershipHelper
import java.text.NumberFormat
import java.util.Locale

class MembershipCatalogAdapter(
    var membresias: List<MembershipCatalog.MembershipPlan>,
    private val onItemClick: (MembershipCatalog.MembershipPlan) -> Unit
) : RecyclerView.Adapter<MembershipCatalogAdapter.MembershipCatalogViewHolder>() {

    companion object {
        private const val TAG = "MembershipCatalogAdapter"
    }

    class MembershipCatalogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvServicio: TextView = itemView.findViewById(R.id.tvServicio)
        val tvPlan: TextView = itemView.findViewById(R.id.tvPlan)
        val tvFrecuencia: TextView = itemView.findViewById(R.id.tvFrecuencia)
        val tvMonto: TextView = itemView.findViewById(R.id.tvMonto)
        val tvMontoMensual: TextView = itemView.findViewById(R.id.tvMontoMensual)
        val imgLogo: ImageView = itemView.findViewById(R.id.imgLogoCatalog)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MembershipCatalogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_membership_catalog, parent, false)
        Log.d(TAG, "Creando ViewHolder")
        return MembershipCatalogViewHolder(view)
    }

    override fun onBindViewHolder(holder: MembershipCatalogViewHolder, position: Int) {
        if (position >= membresias.size) {
            Log.e(TAG, "Error: posición $position fuera de rango. Total: ${membresias.size}")
            return
        }

        val membresia = membresias[position]
        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

        holder.tvServicio.text = membresia.tipo
        holder.tvPlan.text = membresia.nombre
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

        holder.itemView.setOnClickListener {
            onItemClick(membresia)
        }

        Log.d(TAG, "Binding posición $position: ${membresia.tipo} - ${membresia.nombre}")
    }

    override fun getItemCount(): Int {
        val count = membresias.size
        Log.d(TAG, "getItemCount: $count")
        return count
    }

    fun updateMembresias(nuevasMembresias: List<MembershipCatalog.MembershipPlan>) {
        Log.d(TAG, "Actualizando membresías: ${nuevasMembresias.size}")
        membresias = nuevasMembresias
        notifyDataSetChanged()
    }
}

