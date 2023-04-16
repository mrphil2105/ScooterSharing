package dk.itu.moapd.scootersharing.phimo.helpers

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.scootersharing.phimo.R
import dk.itu.moapd.scootersharing.phimo.adapters.ScooterAdapter
import kotlin.math.roundToInt

// Swipe feature inspired by article: https://medium.com/getpowerplay/understanding-swipe-and-drag-gestures-in-recyclerview-cb3136beff20
class ScooterTouchCallback(
    private val context: Context,
    private val resources: Resources,
    private val adapter: ScooterAdapter
) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
    private val deleteIcon =
        ResourcesCompat.getDrawable(resources, R.drawable.baseline_delete_forever_24, null)!!
    private val deleteColor = resources.getColor(android.R.color.holo_red_light, null)

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ) = true

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        AlertDialog.Builder(context)
            .setMessage("Are you sure you want to delete the scooter?")
            .setPositiveButton(R.string.accept_option) { _, _ ->
                val pos = viewHolder.bindingAdapterPosition
                adapter.getRef(pos)
                    .removeValue()
                adapter.notifyItemRemoved(pos)
            }.setNegativeButton(R.string.decline_option) { _, _ ->
                val pos = viewHolder.bindingAdapterPosition
                // Used to get rid of the swiped state on the item.
                adapter.notifyItemChanged(pos)
            }.create().show()
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
        val displayMetrics: DisplayMetrics = resources.displayMetrics
        val width = (displayMetrics.widthPixels / displayMetrics.density).toInt().dp

        canvas.clipRect(
            viewHolder.itemView.left,
            viewHolder.itemView.top,
            viewHolder.itemView.right,
            viewHolder.itemView.bottom
        )

        val hozMargin = 12.dp
        val middle = viewHolder.itemView.height / 2
        val halfHeight = deleteIcon.intrinsicHeight / 2

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
            canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
        )
    }

    private val Int.dp
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, toFloat(), resources.displayMetrics
        ).roundToInt()
}
