package com.gamezone.scoreboard.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.gamezone.scoreboard.R

val AntonFont = FontFamily(Font(R.font.anton))
val RajdhaniFont = FontFamily(Font(R.font.rajdhani, FontWeight.Bold))

val Typography = Typography(
    headlineMedium = TextStyle(
        fontFamily = AntonFont,
        fontSize = 32.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = RajdhaniFont,
        fontSize = 11.sp,
        letterSpacing = 1.sp
    )
)