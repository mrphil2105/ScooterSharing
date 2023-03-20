package dk.itu.moapd.scootersharing.phimo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.scootersharing.phimo.databinding.ScooterListItemBinding
import dk.itu.moapd.scootersharing.phimo.models.Scooter

class ScooterArrayAdapter(private val data: List<Scooter>) :
    RecyclerView.Adapter<ScooterArrayAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ScooterListItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val scooter = data[position]
        holder.bind(scooter)
    }

    class ViewHolder(private val binding: ScooterListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(scooter: Scooter) {
            binding.scooterName.text = scooter.name
            binding.scooterLocation.text = scooter.location
            binding.scooterTimestamp.text = scooter.getTime()
        }
    }
}
