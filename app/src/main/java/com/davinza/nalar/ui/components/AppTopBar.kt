package com.davinza.nalar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davinza.nalar.ui.courses.UserProgressManager

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource

@Composable
fun AppTopBar(
    title: String,
    navigationIcon: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Filled.Menu,
            contentDescription = "Menu",
            tint = Color(0xFF1A1C1C),
            modifier = Modifier.size(24.dp)
        )
    },
    actions: @Composable RowScope.() -> Unit = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Streak
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = com.davinza.nalar.R.drawable.ic_streak),
                    contentDescription = "Streak",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${UserProgressManager.streakCount}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1A1C1C)
                )
            }
            // Keys
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = com.davinza.nalar.R.drawable.ic_key),
                    contentDescription = "Keys",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = UserProgressManager.keysDisplay,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1A1C1C)
                )
            }
        }
    }
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color.White)
            .border(width = 1.dp, color = Color(0xFFE2E2E2), shape = RectangleShape)
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // LEFT: Navigation icon + Title
        Row(verticalAlignment = Alignment.CenterVertically) {
            navigationIcon()
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1A1C1C)
            )
        }
        
        // RIGHT: Actions
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            actions()
        }
    }
}
