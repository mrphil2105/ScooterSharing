package dk.itu.moapd.scootersharing.phimo.fragments

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.scootersharing.phimo.R
import dk.itu.moapd.scootersharing.phimo.adapters.ScooterArrayAdapter
import dk.itu.moapd.scootersharing.phimo.databinding.FragmentMainBinding
import dk.itu.moapd.scootersharing.phimo.models.RidesDB
import kotlin.math.roundToInt

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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainBinding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return mainBinding.root
    }

    // Swipe feature inspired by article: https://medium.com/getpowerplay/understanding-swipe-and-drag-gestures-in-recyclerview-cb3136beff20
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val displayMetrics: DisplayMetrics = resources.displayMetrics
        val width = (displayMetrics.widthPixels / displayMetrics.density).toInt().dp

        val deleteIcon =
            ResourcesCompat.getDrawable(resources, R.drawable.baseline_delete_forever_24, null)
        val deleteColor = resources.getColor(android.R.color.holo_red_light, null)

        swipeHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = true

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                AlertDialog.Builder(requireContext())
                    .setMessage("Are you sure you want to delete the scooter?")
                    .setPositiveButton(R.string.accept_option) { _, _ ->
                        val pos = viewHolder.bindingAdapterPosition
                        ridesDB.removeScooter(pos)
                        adapter.notifyItemRemoved(pos)
                    }.setNegativeButton(R.string.decline_option) { _, _ ->
                        val pos = viewHolder.bindingAdapterPosition
                        // Used to get rid of the swiped state on the item.
                        adapter.notifyItemChanged(pos)
                    }.create()
                    .show()
            }

            override fun onChildDraw(
                canvas: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                canvas.clipRect(
                    viewHolder.itemView.left,
                    viewHolder.itemView.top,
                    viewHolder.itemView.right,
                    viewHolder.itemView.bottom
                )

                val hozMargin = 12.dp
                val middle = viewHolder.itemView.height / 2
                val halfHeight = deleteIcon!!.intrinsicHeight / 2

                deleteIcon.bounds = Rect(
                    width - hozMargin - deleteIcon.intrinsicWidth,
                    viewHolder.itemView.top + middle - halfHeight,
                    width - hozMargin,
                    viewHolder.itemView.top + (middle + halfHeight)
                )

                when {
                    dX > -width / 3 -> canvas.drawColor(Color.GRAY)
                    else -> canvas.drawColor(deleteColor)
                }

                deleteIcon.draw(canvas)

                super.onChildDraw(
                    canvas,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        })

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

    private val Int.dp
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            toFloat(), resources.displayMetrics
        ).roundToInt()
}
