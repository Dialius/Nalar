package com.davinza.nalar.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davinza.nalar.R

// Reusable animated pushable button (like in the reference)
@Composable
fun PushableButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    backgroundColor: Color = Color.White,
    textColor: Color = Color(0xFF111827) // gray-900
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val yOffset by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (isPressed && !isLoading) 2.dp else 0.dp,
        label = "buttonYOffset"
    )

    Box(
        modifier = modifier
            .height(60.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && !isLoading,
                onClick = onClick
            )
    ) {
        // Shadow/Bottom Border Layer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp)
                .height(56.dp)
                .background(
                    if (enabled && !isLoading) Color(0xFFE5E7EB) else Color(0xFFF3F4F6),
                    RoundedCornerShape(20.dp)
                )
        )
        // Top Layer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .offset(y = yOffset)
                .background(
                    if (enabled && !isLoading) backgroundColor else Color(0xFFF3F4F6),
                    RoundedCornerShape(20.dp)
                )
                .border(
                    2.dp,
                    if (enabled && !isLoading) Color(0xFFE5E7EB) else Color(0xFFF3F4F6),
                    RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                // Tampilkan spinner loading
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = textColor.copy(alpha = 0.7f),
                        strokeWidth = 2.5.dp
                    )
                    Text(
                        text = text,
                        color = textColor.copy(alpha = 0.7f),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Text(
                    text = text,
                    color = if (enabled) textColor else Color(0xFF9CA3AF),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Pushable icon button for Social logins
@Composable
fun PushableIconButton(
    iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val yOffset by androidx.compose.animation.core.animateDpAsState(targetValue = if (isPressed) 2.dp else 0.dp, label = "iconYOffset")

    Box(
        modifier = modifier
            .height(56.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        // Bottom border layer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp)
                .height(52.dp)
                .background(Color(0xFFE5E7EB), RoundedCornerShape(20.dp))
        )
        // Top layer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .offset(y = yOffset)
                .background(backgroundColor, RoundedCornerShape(20.dp))
                .border(2.dp, Color(0xFFE5E7EB), RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color.Unspecified
            )
        }
    }
}

// Pushable full-width social button with text and icon
@Composable
fun PushableSocialButton(
    text: String,
    iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    textColor: Color = Color.Black
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val yOffset by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (isPressed) 2.dp else 0.dp,
        label = "socialYOffset"
    )

    Box(
        modifier = modifier
            .height(56.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        // Bottom Shadow Layer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp)
                .height(52.dp)
                .background(Color(0xFFE5E7EB), RoundedCornerShape(20.dp))
        )
        // Top Main Layer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .offset(y = yOffset)
                .background(backgroundColor, RoundedCornerShape(20.dp))
                .border(2.dp, Color(0xFFE5E7EB), RoundedCornerShape(20.dp))
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Onboarding Top Progress Bar
@Composable
fun OnboardingProgressBar(
    progress: Float, // 0.0 to 1.0
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick, modifier = Modifier.size(24.dp)) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_revert), // Placeholder back icon
                contentDescription = "Back"
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = Color(0xFF4CAF50), // Green progress
            trackColor = Color(0xFFE0E0E0)
        )
    }
}

// Custom Text Field for Inputs (like Email, Name, Password)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    backgroundColor: Color = Color.Transparent,
    borderColor: Color = Color(0xFF212121),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.Gray) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF9CA3AF),
            unfocusedBorderColor = Color(0xFFE5E7EB),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
        ),
        keyboardOptions = keyboardOptions,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = {
            if (isPassword) {
                val image = if (passwordVisible)
                    android.R.drawable.ic_menu_view // use appropriate eye icon
                else
                    android.R.drawable.ic_secure // eye-off

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(painter = painterResource(id = image), contentDescription = null, tint = Color.Gray)
                }
            }
        }
    )
}

// Pill Selection Option
@Composable
fun SelectionPill(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.95f else 1f, label = "pillScale")

    val bgBrush = if (isSelected) {
        Brush.horizontalGradient(listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9)))
    } else {
        Brush.horizontalGradient(listOf(Color(0xFFF5F5F5), Color(0xFFF5F5F5)))
    }

    val textColor = if (isSelected) Color(0xFF2E7D32) else Color(0xFF424242)

    Box(
        modifier = modifier
            .scale(scale)
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bgBrush)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color(0xFF4CAF50) else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                color = textColor,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

// Circular gradient spinner for loading screens
@Composable
fun CircularGradientSpinner(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        modifier = modifier.size(80.dp),
        color = Color(0xFF4CAF50),
        strokeWidth = 8.dp,
        trackColor = Color(0xFFEEEEEE)
    )
}

// Grid Selection Card for GridTemplate
@Composable
fun GridSelectionCard(
    text: String,
    iconRes: Int?, // Optional icon resource ID
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.95f else 1f, label = "gridCardScale")

    val bgBrush = if (isSelected) {
        Brush.horizontalGradient(listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9)))
    } else {
        Brush.horizontalGradient(listOf(Color(0xFFF5F5F5), Color(0xFFF5F5F5)))
    }
    
    val textColor = if (isSelected) Color(0xFF2E7D32) else Color(0xFF424242)

    Box(
        modifier = modifier
            .scale(scale)
            .aspectRatio(1f) // Square shape
            .clip(RoundedCornerShape(16.dp))
            .background(bgBrush)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color(0xFF4CAF50) else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            if (iconRes != null) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.height(12.dp))
            } else {
                // Placeholder icon block if no icon is provided
                Box(modifier = Modifier.size(48.dp).background(Color.Gray.copy(alpha=0.3f), RoundedCornerShape(12.dp)))
                Spacer(modifier = Modifier.height(12.dp))
            }
            Text(
                text = text,
                color = textColor,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun NalarAvatar(
    avatarUrl: String?,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 48.dp
) {
    val avatarRes = when (avatarUrl) {
        "fox", "🦊" -> com.davinza.nalar.R.drawable.avatar_fox
        "koala", "🐨" -> com.davinza.nalar.R.drawable.avatar_koala
        "lion", "🦁" -> com.davinza.nalar.R.drawable.avatar_lion
        "owl", "🦉" -> com.davinza.nalar.R.drawable.logo_nalar_premium
        "astronaut", "👨‍🚀" -> com.davinza.nalar.R.drawable.avatar_owl_astronaut
        "cool", "😎" -> com.davinza.nalar.R.drawable.avatar_owl_cool
        "graduate", "🎓" -> com.davinza.nalar.R.drawable.avatar_owl_graduate
        "cat", "🐱" -> com.davinza.nalar.R.drawable.avatar_cat
        "panda", "🐼" -> com.davinza.nalar.R.drawable.avatar_panda
        else -> com.davinza.nalar.R.drawable.logo_nalar_premium
    }
    
    androidx.compose.foundation.Image(
        painter = painterResource(id = avatarRes),
        contentDescription = "Avatar",
        modifier = modifier
            .size(size)
            .clip(androidx.compose.foundation.shape.CircleShape)
            .background(Color(0xFFF5F5F5))
    )
}

