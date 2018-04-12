package com.alimojarrad.fair.Modules.Application

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.alimojarrad.fair.Modules.Selection.FindCarActivity


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FindCarActivity.startActivity(this)
    }
}
