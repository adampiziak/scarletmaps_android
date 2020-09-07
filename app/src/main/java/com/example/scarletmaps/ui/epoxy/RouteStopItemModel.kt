package com.example.scarletmaps.ui.epoxy

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.example.scarletmaps.R
import com.example.scarletmaps.data.models.stop.Stop
import java.time.Instant
import kotlin.math.ceil

@EpoxyModelClass(layout = R.layout.item_routestop)
abstract class RouteStopItemModel : EpoxyModelWithHolder<RouteStopItemModel.Holder>() {

    @EpoxyAttribute
    lateinit var stop: Stop
    @EpoxyAttribute
    lateinit var arrivals: List<Long>
    @EpoxyAttribute
    var stopIndex: Int = 0
    @EpoxyAttribute
    var excludeDivider = false

    override fun bind(holder: Holder) {
        super.bind(holder)
        with(stop) {
            holder.name.text = name
            holder.times.text = createArrivalMessage(arrivals)
            holder.root.setOnClickListener {
                it.findNavController().navigate(R.id.fragmentOpenStop, bundleOf("id" to stop.id))

            }


        }
    }

    class Holder : EpoxyHolder() {
        lateinit var root: ConstraintLayout
        lateinit var name: TextView
        lateinit var times: TextView

        override fun bindView(itemView: View) {
            name = itemView.findViewById(R.id.route_stop_name)
            times = itemView.findViewById(R.id.route_stop_times)
            root = itemView.findViewById(R.id.route_stop_root)
        }
    }

    private fun createArrivalMessage(arrival: List<Long>): String {
        var message = ""
        var validTimes = 0
        arrival.forEachIndexed { i, a ->
            val now: Long = Instant.now().toEpochMilli()
            val difference = a - now
            if (difference < 0 && i == arrivals.size - 1) {
                return "No arrivals"
            }
            if (difference < 0) {
                return@forEachIndexed
            }
            val timeTo = ceil((difference.toDouble() / 1000) / 60).toInt()
            if (i == arrival.size - 1) {
                message += "$timeTo min"
            } else {
                message += "$timeTo min,  "
            }
            validTimes++
        }
        if (validTimes > 0)
            return message
        else
            return "No arrivals"
    }
}