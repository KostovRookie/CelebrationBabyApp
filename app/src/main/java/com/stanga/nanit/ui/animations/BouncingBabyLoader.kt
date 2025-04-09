package com.stanga.nanit.ui.animations

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.res.painterResource
import com.stanga.nanit.R

@Composable
fun BouncingBabyLoader() {
    val infiniteTransition = rememberInfiniteTransition(label = "bounce")

    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -20f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetAnimation"
    )

    Image(
        painter = painterResource(id = R.drawable.ic_placeholder_baby_blue),
        contentDescription = "Loading...",
        modifier = Modifier
            .size(100.dp)
            .offset { IntOffset(0, offsetY.dp.roundToPx()) }
    )
}