package com.alimojarrad.fair.Services.API

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

class ApiManager(context : Context, endPoint : String) {

    private val retrofit: Retrofit

    companion object {
        private const val TAG = "APIManager"
        var gson : Gson? = null
    }



    init {

        val client = OkHttpClient.Builder().addInterceptor { chain ->
            var request = chain.request()

            var requestJWTToken  = ""

            if (requestJWTToken !== "") {
                request = chain.request().newBuilder()
                        .addHeader("Content-Type","application/json")
                        .addHeader("Accept","application/json")
                        .build()
            }

            var response : Response = Response.Builder()
                    .request(request)
                    .code(404)
                    .protocol(Protocol.HTTP_2)
                    .message("")
                    .body(ResponseBody.create(MediaType.parse("application/json"), ""))
                    .build()

            try {
                response = chain.proceed(request)
            } catch (e: ConnectException) {
                Log.e(TAG, "Cannot Connect", e)
            } catch (e: SocketTimeoutException) {
                Log.e(TAG, "Cannot Connect", e)
            } catch (e: IOException) {
                Log.e(TAG, "Cannot Connect", e)
            }
            response
        }.build()

        gson = GsonBuilder()
                .create()

        GsonBuilder()

        retrofit = Retrofit.Builder()
                .baseUrl(endPoint)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
    }

    fun <T> createAPI(apiInterface: Class<T>): T {
        return retrofit.create(apiInterface)
    }

}

