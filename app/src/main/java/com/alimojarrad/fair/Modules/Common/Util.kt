package com.sidecarhealth.Modules.Common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.support.v4.content.ContextCompat
import com.alimojarrad.fair.R
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory


/**
 * Created by amojarrad on 1/31/18.
 */
object Util {

    fun getMarkerBitmap(context: Context, drawable : Int?): BitmapDescriptor {
        var drawable = ContextCompat.getDrawable(context, drawable?:R.drawable.v_locate_marker)

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


}