package com.avinashpatil.app.automessage.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avinashpatil.app.automessage.R
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import com.avinashpatil.app.automessage.ui.theme.NeumorphicCard
import com.avinashpatil.app.automessage.ui.theme.AutoMessageTheme
import com.avinashpatil.app.automessage.ui.theme.NeoAccent
import com.avinashpatil.app.automessage.ui.theme.NeoLightBackground
import com.avinashpatil.app.automessage.ui.theme.NeoPrimaryText
import com.avinashpatil.app.automessage.ui.theme.NeoSecondaryText
import com.avinashpatil.app.automessage.ui.theme.NeoSurface
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.Dp

class SplashActivity : ComponentActivity() {
    private lateinit var splashHandler: Handler
    private var splashRunnable: Runnable? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Render splash with defensive fallback to avoid release crashes
        runCatching {
            setContent {
                AutoMessageTheme {
                    SplashScreen()
                }
            }
        }.onFailure {
            // If compose/content creation fails, navigate directly to main
            navigateToMainActivity()
        }

        // Navigate to MainActivity after 2.5 seconds
        splashHandler = Handler(Looper.getMainLooper())
        splashRunnable = Runnable { navigateToMainActivity() }
        splashHandler.postDelayed(splashRunnable!!, SPLASH_DURATION)
    }
    
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
        // Add fade transition
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
    
    companion object {
        private const val SPLASH_DURATION = 2500L // 2.5 seconds
    }
    
    override fun onDestroy() {
        super.onDestroy()
        try {
            splashRunnable?.let { splashHandler.removeCallbacks(it) }
            splashRunnable = null
        } catch (_: Exception) { }
    }
}

@Composable
fun SplashScreen() {
    // Animated gradient background with neumorphic depth
    val infiniteTransition = androidx.compose.animation.core.rememberInfiniteTransition()
    val animatedProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    // Pulse animation for logo shadow
    val pulseTransition = androidx.compose.animation.core.rememberInfiniteTransition()
    val pulseScale by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = androidx.compose.animation.core.FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    val shadowIntensity by pulseTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = androidx.compose.animation.core.FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    // Neumorphic gradient colors with soft light tones
    val gradientColors = listOf(
        NeoLightBackground,
        NeoSurface.copy(alpha = 0.9f),
        NeoAccent.copy(alpha = 0.15f),
        NeoSurface.copy(alpha = 0.7f),
        NeoLightBackground.copy(alpha = 0.95f)
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = gradientColors,
                    center = androidx.compose.ui.geometry.Offset(0.5f, 0.5f),
                    radius = 1000f + animatedProgress * 200f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // App Logo with enhanced neumorphic styling and pulse animation
            Box(
                modifier = Modifier
                    .size(200.dp * pulseScale)
                    .padding(bottom = 16.dp)
            ) {
                // Breathing glow ring behind logo
                Box(
                    modifier = Modifier
                        .size(190.dp * pulseScale)
                        .align(Alignment.Center)
                        .background(NeoAccent.copy(alpha = 0.12f), shape = CircleShape)
                        .blurIfSupported(20.dp)
                        .alpha(0.8f * shadowIntensity)
                )
                NeumorphicCard(
                    modifier = Modifier
                        .size(180.dp)
                        .align(Alignment.Center),
                    cornerRadius = 90.dp,
                    elevation = (6f * shadowIntensity).dp,
                    backgroundColor = NeoSurface.copy(alpha = 0.4f)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Inner elevated container
                        // Load painter safely without calling composables inside remember
                        val primaryPainter = runCatching { painterResource(id = R.drawable.logo) }.getOrNull()
                        val logoPainter = primaryPainter ?: runCatching { painterResource(id = R.drawable.ic_notification) }.getOrNull()
                        if (logoPainter != null) {
                            Image(
                                painter = logoPainter,
                                contentDescription = "AutoMessage Logo",
                                modifier = Modifier.size(150.dp),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            // Minimal fallback to avoid crash if resources are missing
                            Text(
                                text = "AutoMessage",
                                color = NeoPrimaryText,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // App Name with neumorphic text styling
            NeumorphicCard(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                cornerRadius = 16.dp,
                elevation = 3.dp,
                backgroundColor = NeoSurface.copy(alpha = 0.3f)
            ) {
                Text(
                    text = "Auto Messaging",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeoPrimaryText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                )
            }
            
            // Tagline with subtle neumorphic styling
            NeumorphicCard(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                cornerRadius = 12.dp,
                elevation = 2.dp,
                backgroundColor = NeoSurface.copy(alpha = 0.2f)
            ) {
                Text(
                    text = "Automated Call Response",
                    fontSize = 14.sp,
                    color = NeoSecondaryText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // Developer info with minimal neumorphic styling
            Text(
                text = "Developer: Avinash Patil",
                fontSize = 12.sp,
                color = NeoSecondaryText.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

// Safely apply blur only on supported SDKs
private fun Modifier.blurIfSupported(radius: Dp): Modifier {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) this.blur(radius) else this
}