package com.alimojarrad.fair.Services.API.UseCases

import com.alimojarrad.fair.Models.JsonDOM
import com.alimojarrad.fair.Services.API.API
import io.reactivex.Single
import retrofit2.Response

/**
 * Created by amojarrad on 2/2/18.
 */
object GetCarResults {

    fun execute( queryMap : HashMap<String,String>): Single<Response<JsonDOM>> {
        queryMap["apikey"]="HC3Ukn6MsT5B37UCEnF4iK3e9dUza5bl"
        return API.result.getCarResults(queryMap)
    }
}
