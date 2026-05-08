package com.davinza.nalar.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davinza.nalar.ui.components.PushableButton

@Composable
fun HomeScreen() {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Matematika", "Fisika", "Biologi", "Kimia", "Ekonomi")

    val courses = listOf(
        "Aritmetika Dasar",
        "Geometri & Pengukuran",
        "Aljabar Dasar",
        "Statistika Deskriptif"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top Tab Row
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            edgePadding = 16.dp,
            containerColor = Color.White,
            contentColor = Color(0xFF4A148C),
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    height = 3.dp,
                    color = Color(0xFF4A148C)
                )
            },
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == index) Color(0xFF4A148C) else Color.Gray
                        )
                    }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${tabs[selectedTabIndex]} Foundations",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "Kuasai dasar-dasar ${tabs[selectedTabIndex]}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                    
                    // Large 3D Icon placeholder
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFE8EAF6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("3D\nIcon", textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontSize = 12.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }

            itemsIndexed(courses) { index, course ->
                LearningPathNode(
                    title = course,
                    isRecommended = index == 1,
                    isLast = index == courses.size - 1
                )
            }
        }
    }
}

@Composable
fun LearningPathNode(
    title: String,
    isRecommended: Boolean,
    isLast: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Node Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(if (isRecommended) Color(0xFFFFF3E0) else Color.White) // Light orange if recommended
                .border(
                    width = 2.dp,
                    color = if (isRecommended) Color(0xFFFFB74D) else Color(0xFFE0E0E0),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Icon placeholder
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE8EAF6))
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    if (isRecommended) {
                        Text(
                            text = "RECOMMENDED",
                            color = Color(0xFFE65100),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .background(Color(0xFFFFCC80), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }

        // Vertical Line Connector
        if (!isLast) {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(40.dp)
                    .background(Color(0xFFE0E0E0))
            )
        }
    }
}
