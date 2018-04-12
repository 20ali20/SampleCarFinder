package com.alimojarrad.fair.Modules.Application

import android.app.Application
import android.content.Context
import com.alimojarrad.fair.Services.API.API
import com.alimojarrad.fair.Services.API.ApiManager
import com.squareup.leakcanary.LeakCanary
import net.danlew.android.joda.JodaTimeAndroid
import timber.log.Timber

class MyApplication : Application() {
    companion object {
        private const val TAG = "MyApplication"
        lateinit var appContext: Context
            private set
    }



    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
        JodaTimeAndroid.init(this)
        Timber.plant(Timber.DebugTree())
        initToken()
    }




    private fun initToken() {
        val apiManager = ApiManager(this, API.endpointUrl)
        API.init(apiManager)
    }
}