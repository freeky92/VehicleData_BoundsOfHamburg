package com.asurspace.vehicledata_boundsofhamburg.ui.screens.map_components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest

typealias OnPointClickListener = (PointOfInterest) -> Unit

@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@OptIn(
    ExperimentalAnimationApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun ShowPlaceInfo(
    onPointClickInfo: PointOfInterest,
    onPointClickListener: OnPointClickListener,
    modifier: Modifier = Modifier
) {
    val emptyPointOfInterest = PointOfInterest(LatLng(0.0, 0.0), "", "")

    AnimatedVisibility(
        modifier = modifier,
        visible = onPointClickInfo.placeId != "",
        enter = scaleIn(
            transformOrigin = TransformOrigin.Center,
            animationSpec = tween(durationMillis = 350)
        ),
        exit = scaleOut(
            transformOrigin = TransformOrigin.Center,
            animationSpec = tween(durationMillis = 350)
        ) + fadeOut(),
    ) {
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(10.dp),
            elevation = 1.dp,
            onClick = {
                onPointClickListener.invoke(emptyPointOfInterest)
            }) {
            Text(
                text = onPointClickInfo.name,
                Modifier
                    .padding(12.dp)
            )
        }
    }
}