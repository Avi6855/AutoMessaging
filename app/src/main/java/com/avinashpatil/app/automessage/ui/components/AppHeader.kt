package com.avinashpatil.app.automessage.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avinashpatil.app.automessage.R
import com.avinashpatil.app.automessage.ui.theme.NeoAccent
import com.avinashpatil.app.automessage.ui.theme.NeoLightBackground
import com.avinashpatil.app.automessage.ui.theme.NeoPrimaryText
import com.avinashpatil.app.automessage.ui.theme.NeoSecondaryText
import com.avinashpatil.app.automessage.ui.theme.NeoSurface
import com.avinashpatil.app.automessage.ui.theme.NeumorphicCard

@Composable
fun AppHeader(
    modifier: Modifier = Modifier
) {
    NeumorphicCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        cornerRadius = 16.dp,
        elevation = 4.dp,
        backgroundColor = NeoSurface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Logo
            NeumorphicCard(
                modifier = Modifier.size(40.dp),
                cornerRadius = 20.dp,
                elevation = 3.dp,
                backgroundColor = NeoAccent.copy(alpha = 0.2f)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "AutoMessage Logo",
                        modifier = Modifier.size(24.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // App Title
            Column {
                Text(
                    text = "AutoMessage",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeoPrimaryText
                )
                Text(
                    text = "Automated Call Response",
                    fontSize = 12.sp,
                    color = NeoSecondaryText
                )
            }
        }
    }
}

@Preview
@Composable
fun AppHeaderPreview() {
    AppHeader()
}