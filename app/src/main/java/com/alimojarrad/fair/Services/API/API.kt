package com.alimojarrad.fair.Services.API

import com.alimojarrad.fair.Services.API.Interfaces.ResultInterface

/**
 * Created by amojarrad on 1/29/18.
 */
object API {

    const val endpointUrl = "https://api.sandbox.amadeus.com/v1.2/"

    lateinit var result : ResultInterface


    fun init(apiManager: ApiManager){
        result = apiManager.createAPI(ResultInterface::class.java)

    }
}

