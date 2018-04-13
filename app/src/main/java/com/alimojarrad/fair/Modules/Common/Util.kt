package com.sidecarhealth.Modules.Common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.support.v4.content.ContextCompat
import com.alimojarrad.fair.R
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import java.math.RoundingMode
import java.text.DecimalFormat


/**
 * Created by amojarrad on 1/31/18.
 */
object Util {

    fun getMarkerBitmap(context: Context, drawable : Int?): BitmapDescriptor {
        var drawable = ContextCompat.getDrawable(context, drawable?:R.drawable.v_gps_location_marker)

        var bitmap = Bitmap.createBitmap(
                drawable!!.getIntrinsicWidth(),
                drawable!!.intrinsicHeight,
                Bitmap.Config.ARGB_8888
        )
        var canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    fun getRoundedDouble(double : Double) : Double {
        val df = DecimalFormat("#.###")
        df.roundingMode = RoundingMode.CEILING
        return df.format(double).toDouble()
    }

    fun getRoundedFloatAsInt(float : Float) : Int {
        val df = DecimalFormat("#.#")
        df.roundingMode = RoundingMode.CEILING
        return df.format(float).toInt()
    }


}