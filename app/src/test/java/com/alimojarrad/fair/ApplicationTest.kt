package com.alimojarrad.fair

import android.app.Application
import android.content.Context
import com.alimojarrad.fair.Services.API.API
import com.alimojarrad.fair.Services.API.ApiManager
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(application = ApplicationTest.ApplicationStub::class)
abstract class ApplicationTest {

    init {
        val apiManager = ApiManager(context(),API.endpointUrl)
        API.init(apiManager)
    }
    fun context(): Context {
        return RuntimeEnvironment.application
    }

    fun cacheDir(): File {
        return context().cacheDir
    }

    internal class ApplicationStub : Application()
}