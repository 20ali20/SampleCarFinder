package com.alimojarrad.fair.Modules.Main

import android.content.Context
import android.location.Location
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.alimojarrad.fair.Models.Car
import com.alimojarrad.fair.R

/**
 * Created by amojarrad on 4/3/18.
 */

class CarAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class SortType {
        PROVIDER,
        PRICE,
        DISTANCE
    }

    var currentLocation: Location? = null
    private var list = ArrayList<Car>()
    private val OtherView = 1
    private var isLoading = false

    fun updateCars(CarList: ArrayList<Car>) {
        isLoading = false
        list.clear()
        list.addAll(CarList)
        notifyDataSetChanged()
    }

    fun addCars(CarList: ArrayList<Car>) {
        isLoading = false
        list.addAll(CarList)
    }

    fun provideList(): ArrayList<Car> {
        return list
    }

    fun showLoading() {
        isLoading = true
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == OtherView) {
            return if (isLoading) {
                var view = LayoutInflater.from(parent?.context).inflate(R.layout.item_loading_view, parent, false)
                LoadingCarViewHolder(view)
            } else { // Empty View
                var view = LayoutInflater.from(parent?.context).inflate(R.layout.item_empty_view, parent, false)
                EmptyCarViewHolder(view)
            }
        }
        var view: View? = null
        if (view == null) {
            view = LayoutInflater.from(parent?.context).inflate(R.layout.item_car, parent, false)
        }
        return CarListViewHolder(view!!, context, currentLocation)
    }

    override fun getItemCount(): Int {
        return if (list.size > 0 && !isLoading) {
            list.size
        } else {
            OtherView
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (itemCount == 1 && list.isEmpty()) {
            OtherView
        } else {
            super.getItemViewType(position)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        (holder as? CarListViewHolder)?.bind(list[position])

    }
}

private class CarListViewHolder(itemView: View, val context: Context, val currentLocation: Location?) : RecyclerView.ViewHolder(itemView) {

    private var type = itemView.findViewById<TextView>(R.id.ic_type)
    private var features = itemView.findViewById<TextView>(R.id.ic_features)
    private var estimate = itemView.findViewById<TextView>(R.id.ic_estimate)
    private var dailyrate = itemView.findViewById<TextView>(R.id.ic_dailyrate)



    fun bind(car: Car) {
        car.vehicle_info?.type?.let {
            type.text = "$it"
        }
        var features = ""
        car.vehicle_info?.transmission?.let {
            features += "$it transmission\n"
        }
        car.vehicle_info?.air_conditioning?.let {
            if (it) {
                features += "Air Conditioning"
            }
        }
        this.features.text = features
        car.estimated_total?.let {
            estimate.text = "$${it.amount}"
        }
        car.rates?.first()?.let {
            dailyrate.text = "$${it.price?.amount}/day"
        }


    }
}

private class EmptyCarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
private class LoadingCarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)




