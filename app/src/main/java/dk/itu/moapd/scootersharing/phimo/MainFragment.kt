package dk.itu.moapd.scootersharing.phimo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dk.itu.moapd.scootersharing.phimo.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    private lateinit var mainBinding: FragmentMainBinding

    companion object {
        lateinit var ridesDB: RidesDB
        lateinit var adapter: ScooterArrayAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ridesDB = RidesDB.get(requireContext())
        val data = ridesDB.getRidesList()
        adapter = ScooterArrayAdapter(requireContext(), R.layout.scooter_list_item, data)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainBinding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return mainBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(mainBinding) {
            startRideButton.setOnClickListener {
                val intent = Intent(context, StartRideActivity::class.java)
                startActivity(intent)
            }

            updateRideButton.setOnClickListener {
                val intent = Intent(context, UpdateRideActivity::class.java)
                startActivity(intent)
            }

            listRidesButton.setOnClickListener {
                scooterList.visibility = if (scooterList.visibility == View.VISIBLE)
                    View.INVISIBLE else View.VISIBLE
            }

            scooterList.adapter = adapter
        }
    }
}
