package dk.itu.moapd.scootersharing.phimo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.scootersharing.phimo.R
import dk.itu.moapd.scootersharing.phimo.adapters.ScooterArrayAdapter
import dk.itu.moapd.scootersharing.phimo.databinding.FragmentMainBinding
import dk.itu.moapd.scootersharing.phimo.helpers.ScooterTouchCallback
import dk.itu.moapd.scootersharing.phimo.models.RidesDB

class MainFragment : Fragment() {
    private lateinit var auth: FirebaseAuth

    private lateinit var mainBinding: FragmentMainBinding
    private lateinit var swipeHelper: ItemTouchHelper

    companion object {
        lateinit var ridesDB: RidesDB
        lateinit var adapter: ScooterArrayAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        ridesDB = RidesDB.get(requireContext())
        val data = ridesDB.getRidesList()
        adapter = ScooterArrayAdapter(data)
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
