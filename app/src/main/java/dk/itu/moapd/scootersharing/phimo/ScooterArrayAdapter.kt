package dk.itu.moapd.scootersharing.phimo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ScooterArrayAdapter(context: Context, private var resource: Int, data: List<Scooter>) :
    ArrayAdapter<Scooter>(context, R.layout.scooter_list_item, data) {
    private class ViewHolder(view: View) {
        val name: TextView = view.findViewById(R.id.scooter_name)
        val location: TextView = view.findViewById(R.id.scooter_location)
        val timestamp: TextView = view.findViewById(R.id.scooter_timestamp)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val viewHolder: ViewHolder

        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(resource, parent, false)
            viewHolder = ViewHolder(view)
        } else {
            viewHolder = view.tag as ViewHolder
        }

        val scooter = getItem(position)
        viewHolder.name.text = scooter?.name
        viewHolder.location.text = scooter?.location
        viewHolder.timestamp.text = scooter?.getTime()

        view?.tag = viewHolder
        return view!!
    }
}
