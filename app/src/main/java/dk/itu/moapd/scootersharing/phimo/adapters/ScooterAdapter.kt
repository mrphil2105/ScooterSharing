package dk.itu.moapd.scootersharing.phimo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import dk.itu.moapd.scootersharing.phimo.databinding.ScooterListItemBinding
import dk.itu.moapd.scootersharing.phimo.models.Scooter

class ScooterAdapter(
    options: FirebaseRecyclerOptions<Scooter>, private val onItemClick: ((Scooter) -> Unit)
) : FirebaseRecyclerAdapter<Scooter, ScooterAdapter.ViewHolder>(options) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ScooterListItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, scooter: Scooter) {
        holder.apply {
            bind(scooter)
            itemView.setOnLongClickListener {
                onItemClick.invoke(scooter)
                true
            }
        }
    }

    class ViewHolder(val binding: ScooterListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(scooter: Scooter) {
            binding.scooterName.text = scooter.name
            binding.scooterLocation.text = scooter.location
            binding.scooterTimestamp.text = scooter.getTime()
        }
    }
}
