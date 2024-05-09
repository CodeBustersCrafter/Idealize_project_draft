package com.codebusters.idealizeprojectdraft

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition


@Composable
fun MainScreen(){
    val composition = rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.splashvideo))
    val progress by animateLottieCompositionAsState(
        composition = composition.value,
        iterations = 1
    )

    Box (modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        LottieAnimation(
            modifier = Modifier.fillMaxSize(),
            composition = composition.value,
            progress = {progress}
        )
    }

}
