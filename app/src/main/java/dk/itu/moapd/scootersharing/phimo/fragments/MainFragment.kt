package dk.itu.moapd.scootersharing.phimo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dk.itu.moapd.scootersharing.phimo.R
import dk.itu.moapd.scootersharing.phimo.adapters.ScooterAdapter
import dk.itu.moapd.scootersharing.phimo.databinding.FragmentMainBinding
import dk.itu.moapd.scootersharing.phimo.helpers.ScooterTouchCallback
import dk.itu.moapd.scootersharing.phimo.models.Scooter

class MainFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private lateinit var mainBinding: FragmentMainBinding
    private lateinit var swipeHelper: ItemTouchHelper

    companion object {
        lateinit var adapter: ScooterAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        auth.currentUser?.let {
            val query = database.reference.child("scooters")
                .child(it.uid)
                .orderByChild("createdAt")
            val options = FirebaseRecyclerOptions.Builder<Scooter>()
                .setQuery(query, Scooter::class.java)
                .setLifecycleOwner(this)
                .build()

            adapter = ScooterAdapter(options) {
                // Long press action here.
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mainBinding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return mainBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeHelper = ItemTouchHelper(ScooterTouchCallback(requireContext(), resources, adapter))

        with(mainBinding) {
            startRideButton.setOnClickListener {
                Navigation.findNavController(view)
                    .navigate(R.id.action_mainFragment_to_startRideFragment)
            }

            updateRideButton.setOnClickListener {
                Navigation.findNavController(view)
                    .navigate(R.id.action_mainFragment_to_updateRideFragment)
            }

            listRidesButton.setOnClickListener {
                scooterList.visibility = if (scooterList.visibility == View.VISIBLE)
                    View.INVISIBLE else View.VISIBLE
            }

            signOutButton.setOnClickListener {
                auth.signOut()
                Navigation.findNavController(view)
                    .navigate(R.id.action_mainFragment_to_loginActivity)
            }

            scooterList.layoutManager = LinearLayoutManager(context)
            scooterList.adapter = adapter
            swipeHelper.attachToRecyclerView(scooterList)
        }
    }
}
