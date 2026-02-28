package com.avinashpatil.app.automessage.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.avinashpatil.app.automessage.ui.theme.LightBackground

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    blurRadius: Dp = 10.dp,
    borderWidth: Dp = 1.dp,
    borderColor: Color = Color.White.copy(alpha = 0.2f),
    //backgroundColor: Color = LightBackground,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            //.background(
                //color = backgroundColor.copy(alpha = 0.85f),
                //shape = RoundedCornerShape(cornerRadius)
           // )
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(cornerRadius)
            )
            .blur(radius = blurRadius)
    ) {
        content()
    }
}

@Preview
@Composable
fun GlassCardPreview() {
    GlassCard {
        Text(
            text = "This is a Glass Card",
            modifier = Modifier.fillMaxSize()
        )
    }
}