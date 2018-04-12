package com.alimojarrad.fair.Modules.Main

import android.content.Context
import android.location.Location
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.alimojarrad.fair.Models.Result
import com.alimojarrad.fair.R
import org.greenrobot.eventbus.EventBus


/**
 * Created by amojarrad on 4/3/18.
 */
enum class ClickType {
    LOCATE,
    NAVIGATE
}

data class CarClickEvent(val clickType: ClickType,var title : String, val latitude: Double, val longitude: Double)

class ProviderAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class SortType{
        PROVIDER,
        PRICE,
        DISTANCE
    }

    var currentLocation: Location? = null
    private var list = ArrayList<Result>()
    private val EmptyView = 1

    fun updateResults(ResultList: ArrayList<Result>) {
        list.clear()
        list.addAll(ResultList)
        notifyDataSetChanged()
    }

    fun addResults(ResultList: ArrayList<Result>) {
        list.addAll(ResultList)
    }

    fun provideList(): ArrayList<Result> {
        return list
    }

    fun sortBy(sortType: SortType, isAsc : Boolean){
        when(sortType){
            SortType.DISTANCE->{
                if(isAsc){
                    list.sortBy {
                        it.distance
                    }
                }else{
                    list.sortByDescending {
                        it.distance
                    }
                }
                notifyDataSetChanged()
            }
            SortType.PRICE->{
//                if(isAsc){
//                    list.sortBy {
//                        it.
//                    }
//                }else{
//                    list.sortByDescending {
//                        it.distance
//                    }
//                }
//                notifyDataSetChanged()
            }
            SortType.PROVIDER->{
                if(isAsc){
                    list.sortBy {
                        it.provider?.company_name
                    }                }else{
                    list.sortByDescending {
                        it.provider?.company_name
                    }
                }
                notifyDataSetChanged()
            }
        }
    }



    fun removeAll(){
        list.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == EmptyView) {
                var view = LayoutInflater.from(parent?.context).inflate(R.layout.item_empty_view, parent, false)
                return EmptyViewHolder(view)
        }
        var view: View? = null
        if (view == null) {
            view = LayoutInflater.from(parent?.context).inflate(R.layout.item_provider, parent, false)
        }
        return ResultListViewHolder(view!!, context,currentLocation)
    }

    override fun getItemCount(): Int {
        return if (list.size>1) {
            list.size
        } else {
            1
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (itemCount == 1 && list.isEmpty()) {
            EmptyView
        } else {
            super.getItemViewType(position)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when(holder){
            is ResultListViewHolder->{
                (holder as? ResultListViewHolder)?.bind(list[position])
            }
            is EmptyViewHolder ->{
                //Do nothing
            }
            else->{
                //Do nothing
            }
        }


    }
}

private class ResultListViewHolder(itemView: View, val context: Context, val currentLocation: Location?) : RecyclerView.ViewHolder(itemView) {
    private var provider = itemView.findViewById<TextView>(R.id.ip_provider)
    private var radius = itemView.findViewById<TextView>(R.id.ip_radius)
    private var address = itemView.findViewById<TextView>(R.id.ip_address)
    private var recyclerView = itemView.findViewById<RecyclerView>(R.id.ip_recyclerview)
    private var navigate = itemView.findViewById<TextView>(R.id.ip_navigate)
    private var locate = itemView.findViewById<TextView>(R.id.ip_locate)


    fun bind(result: Result) {
        recyclerView.visibility = View.GONE
        result.provider?.let {
            provider.text = "${it.company_name?.toUpperCase()}"
        }
        result.address?.let {
            address.text = "${it.line1}\n${it.city}\n${it.region}"
        }

        result.location?.let {
            var destination = Location("")
            destination.latitude = it.latitude!!
            destination.longitude = it.longitude!!
            currentLocation?.let {
                val radius = it.distanceTo(destination).toInt()
                result.distance = radius
                this.radius.text = "$radius"

            }
        }

        val adapter = CarAdapter(context)
        recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        result.cars?.let {
            adapter.updateCars(it)
        }

        itemView.setOnClickListener {
            if(adapter.provideList().isNotEmpty() && recyclerView.visibility == View.GONE){
                recyclerView.visibility = View.VISIBLE

            }else{
                recyclerView.visibility = View.GONE
            }
        }


        locate.setOnClickListener {
            if(result.location !=null) {
                EventBus.getDefault().postSticky(CarClickEvent(ClickType.LOCATE,"${result.provider?.company_name}",result.location?.latitude!!, result.location?.longitude!! ))
            }else{
                Toast.makeText(context,"This feature is not available at the moment",Toast.LENGTH_SHORT).show()
            }
        }
        navigate.setOnClickListener {
            if(result.location !=null) {
                EventBus.getDefault().postSticky(CarClickEvent(ClickType.NAVIGATE,"${result.provider?.company_name}",result.location?.latitude!!, result.location?.longitude!! ))
            }else{
                Toast.makeText(context,"This feature is not available at the moment",Toast.LENGTH_SHORT).show()
            }
        }
    }
}

private class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)




