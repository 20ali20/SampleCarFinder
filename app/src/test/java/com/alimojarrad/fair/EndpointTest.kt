package com.alimojarrad.fair

import com.alimojarrad.fair.Services.API.API
import com.alimojarrad.fair.Services.API.Interfaces.CarResultQueryParam
import org.junit.Before
import org.junit.Test
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog





@Config(manifest=Config.NONE)


class EndpointTest : ApplicationTest() {
    @Before
    fun init() {
        ShadowLog.stream = System.out
    }
    @Test
    fun testCarProviderEndPoint(){
        var map = HashMap<String, String>()
        map["apikey"] = "HC3Ukn6MsT5B37UCEnF4iK3e9dUza5bl"
        map[CarResultQueryParam.Latitude.q] = "34.054"
        map[CarResultQueryParam.Longitutde.q] = "-118.304"
        map[CarResultQueryParam.DropOff.q] = "2018-06-08"
        map[CarResultQueryParam.PickupTime.q] = "2018-06-07"
        map[CarResultQueryParam.Radius.q] = "50"
        val fetch = API.result.getCarResults(map).test()
        fetch.assertValue {
            ShadowLog.v("body", "${it.body()}}")
            ShadowLog.v("errorBody", "${it.errorBody()}")
            ShadowLog.v("statusCode", "${it.code()}");

            it.isSuccessful
        }

    }
}