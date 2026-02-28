package com.avinashpatil.app.automessage.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 🎨 Neumorphic Color Palette
val NeoLightBackground = Color(0xFFE8EEF5)
val NeoSurface = Color(0xFFF6FAFF)
val NeoCardBackground = Color(0xFFFFFFFF)
val NeoSoftShadow = Color(0xFFBEC8D2)
val NeoLightShadow = Color(0xFFFFFFFF)
val NeoPrimaryText = Color(0xFF2E3A59)
val NeoSecondaryText = Color(0xFF7B8FA1)
val NeoAccent = Color(0xFFFFA726)
val NeoAccentStart = Color(0xFFFFB84D)
val NeoAccentEnd = Color(0xFFFF8C00)
val NeoInactiveTab = Color(0xFFE0E7ED)
val NeoSearchBackground = Color(0xFFF0F3F8)

// 🔤 Neumorphic Typography
val NeoTypographyFamily = FontFamily.Default

// 📦 Base Neumorphic Surface
@Composable
fun NeumorphicSurface(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    elevation: Dp = 8.dp,
    contentPadding: Dp = 16.dp,
    backgroundColor: Color = NeoCardBackground,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(cornerRadius)
            )
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = NeoSoftShadow.copy(alpha = 0.6f),
                spotColor = NeoLightShadow.copy(alpha = 0.9f)
            )
            .padding(contentPadding),
        content = content
    )
}

// 🃏 Neumorphic Card
@Composable
fun NeumorphicCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    elevation: Dp = 6.dp,
    contentPadding: Dp = 16.dp,
    backgroundColor: Color = NeoCardBackground.copy(alpha = 0.95f),
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val scale by animateFloatAsState(
        targetValue = if (onClick != null) 0.98f else 1f,
        animationSpec = tween(100), label = ""
    )
    
    Box(
        modifier = modifier
            .scale(scale)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(cornerRadius)
            )
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = NeoSoftShadow.copy(alpha = 0.5f),
                spotColor = NeoLightShadow.copy(alpha = 0.8f)
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                } else Modifier
            )
            .padding(contentPadding),
        content = content
    )
}

// 🔘 Neumorphic Button
@Composable
fun NeumorphicButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 50.dp,
    enabled: Boolean = true,
    textColor: Color = Color.White,
    fontSize: TextUnit = 16.sp,
    fontWeight: FontWeight = FontWeight.Bold
) {
    val scale by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.95f,
        animationSpec = tween(150), label = ""
    )
    
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = if (enabled) 8.dp else 4.dp,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = NeoSoftShadow,
                spotColor = NeoLightShadow
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(cornerRadius),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = if (enabled) {
                            listOf(NeoAccentStart, NeoAccentEnd)
                        } else {
                            listOf(NeoInactiveTab, NeoInactiveTab)
                        }
                    ),
                    shape = RoundedCornerShape(cornerRadius)
                )
                .height(48.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text.uppercase(),
                color = if (enabled) textColor else NeoSecondaryText,
                fontSize = fontSize,
                fontWeight = fontWeight,
                softWrap = false,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontFamily = NeoTypographyFamily
            )
        }
    }
}

// 📋 Neumorphic Tab
@Composable
fun NeumorphicTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    fontSize: TextUnit = 16.sp
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) NeoAccent else NeoInactiveTab,
        animationSpec = tween(200), label = ""
    )
    
    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else NeoPrimaryText,
        animationSpec = tween(200), label = ""
    )
    
    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(cornerRadius)
            )
            .shadow(
                elevation = if (isSelected) 6.dp else 3.dp,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = NeoSoftShadow.copy(alpha = 0.4f),
                spotColor = NeoLightShadow.copy(alpha = 0.7f)
            )
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = fontSize,
            fontFamily = NeoTypographyFamily,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
    }
}

// 🔍 Neumorphic Search Field
@Composable
fun NeumorphicSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Search...",
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    NeumorphicSurface(
        modifier = modifier,
        cornerRadius = cornerRadius,
        elevation = 4.dp,
        contentPadding = 0.dp,
        backgroundColor = NeoSearchBackground
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingIcon?.invoke()
            if (leadingIcon != null) {
                Spacer(modifier = Modifier.width(12.dp))
            }
            
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(
                    color = NeoPrimaryText,
                    fontSize = 16.sp,
                    fontFamily = NeoTypographyFamily
                ),
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = NeoSecondaryText,
                            fontSize = 16.sp,
                            fontFamily = NeoTypographyFamily
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}

// 🏷️ Neumorphic Badge
@Composable
fun NeumorphicBadge(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = NeoAccent,
    textColor: Color = Color.White,
    cornerRadius: Dp = 12.dp
) {
    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(cornerRadius)
            )
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = NeoSoftShadow.copy(alpha = 0.3f),
                spotColor = NeoLightShadow.copy(alpha = 0.6f)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = NeoTypographyFamily
        )
    }
}

// 🎛️ Neumorphic Switch
@Composable
fun NeumorphicSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (checked) NeoAccent else NeoInactiveTab,
        animationSpec = tween(200), label = ""
    )
    
    Box(
        modifier = modifier
            .size(width = 50.dp, height = 28.dp)
            .background(
                color = backgroundColor,
                shape = CircleShape
            )
            .shadow(
                elevation = 4.dp,
                shape = CircleShape,
                ambientColor = NeoSoftShadow.copy(alpha = 0.4f),
                spotColor = NeoLightShadow.copy(alpha = 0.7f)
            )
            .clickable { onCheckedChange(!checked) }
            .padding(4.dp),
        contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(
                    color = Color.White,
                    shape = CircleShape
                )
                .shadow(
                    elevation = 2.dp,
                    shape = CircleShape,
                    ambientColor = NeoSoftShadow.copy(alpha = 0.3f),
                    spotColor = NeoLightShadow.copy(alpha = 0.6f)
                )
        )
    }
}

// 📊 Neumorphic Progress Bar
@Composable
fun NeumorphicProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 12.dp,
    backgroundColor: Color = NeoInactiveTab,
    progressColor: Color = NeoAccent
) {
    Box(
        modifier = modifier
            .height(8.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(cornerRadius)
            )
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = NeoSoftShadow.copy(alpha = 0.3f),
                spotColor = NeoLightShadow.copy(alpha = 0.5f)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .background(
                    color = progressColor,
                    shape = RoundedCornerShape(cornerRadius)
                )
        )
    }
}