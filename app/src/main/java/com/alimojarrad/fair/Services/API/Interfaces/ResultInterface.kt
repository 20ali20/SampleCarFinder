package com.alimojarrad.fair.Services.API.Interfaces

import com.alimojarrad.fair.Models.JsonDOM
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface ResultInterface {

    @GET("cars/search-circle")
    fun getCarResults(@QueryMap queryMap : Map<String,String>) : Single<Response<JsonDOM>>


}

enum class CarResultQueryParam(val q : String) {
    Latitude("latitude"),
    Longitutde("longitude"),
    Radius("radius"),
    PickupTime("pick_up"),
    DropOff("drop_off"),
}