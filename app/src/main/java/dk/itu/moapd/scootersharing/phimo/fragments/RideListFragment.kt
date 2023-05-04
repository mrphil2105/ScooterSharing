package dk.itu.moapd.scootersharing.phimo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dk.itu.moapd.scootersharing.phimo.R
import dk.itu.moapd.scootersharing.phimo.adapters.ScooterAdapter
import dk.itu.moapd.scootersharing.phimo.databinding.FragmentRideListBinding
import dk.itu.moapd.scootersharing.phimo.helpers.ScooterTouchCallback
import dk.itu.moapd.scootersharing.phimo.models.Scooter

class RideListFragment : Fragment() {
    private lateinit var binding: FragmentRideListBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private lateinit var adapter: ScooterAdapter
    private lateinit var swipeHelper: ItemTouchHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val query = database.reference.child("scooters")
            .orderByChild("createdAt")
        val options = FirebaseRecyclerOptions.Builder<Scooter>()
            .setQuery(query, Scooter::class.java)
            .setLifecycleOwner(this)
            .build()

        adapter = ScooterAdapter(options, requireContext()) { scooterId ->
            val bundle = bundleOf("scooter_id" to scooterId)
            Navigation.findNavController(requireView())
                .navigate(R.id.action_rideListFragment_to_startRideFragment, bundle)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRideListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeHelper = ItemTouchHelper(ScooterTouchCallback(requireContext(), resources, adapter))

        with(binding) {
            scooterList.layoutManager = LinearLayoutManager(context)
            scooterList.adapter = adapter
            swipeHelper.attachToRecyclerView(scooterList)
        }
    }
}
