package com.asurspace.vehicledata_boundsofhamburg.presentation.ui.map.map_components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.*
import com.asurspace.vehicledata_boundsofhamburg.R

@Composable
fun ShowProgress(isMapLoaded: Boolean, modifier: Modifier = Modifier) {
    if (!isMapLoaded) {
        AnimatedVisibility(
            modifier = modifier,
            visible = !isMapLoaded,
            enter = EnterTransition.None,
            exit = fadeOut()
        ) {
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.maps_car))
            LottieAnimation(composition, iterations = LottieConstants.IterateForever)
        }
    }
}