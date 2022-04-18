package com.asurspace.vehicledata_boundsofhamburg.common.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import com.asurspace.vehicledata_boundsofhamburg.R
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

fun bitmapDescriptorFromVector(
    context: Context,
    vehicleType: String,
    id: Int
): BitmapDescriptor {
    val vectorResId = when {
        vehicleType == "TAXI" -> R.drawable.ic_yellow_car
        (vehicleType == "POOLING" && id % 1000 in (0..250)) -> R.drawable.ic_black_car
        else -> R.drawable.ic_white_car
    }
    val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
    vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
    val bitmap = Bitmap.createBitmap(
        vectorDrawable.intrinsicWidth,
        vectorDrawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}