package com.gamezone.scoreboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gamezone.scoreboard.ScoreboardViewModel
import com.gamezone.scoreboard.ui.theme.AntonFont
import com.gamezone.scoreboard.ui.theme.GoldColor
import com.gamezone.scoreboard.ui.theme.GreenColor
import com.gamezone.scoreboard.ui.theme.MutedColor
import com.gamezone.scoreboard.ui.theme.RajdhaniFont
import com.gamezone.scoreboard.ui.theme.Surface2Color

@Composable
fun SettleScreen(viewModel: ScoreboardViewModel, onSettled: () -> Unit) {
    val owed = viewModel.computeOwed()
    val payable = owed.filter { it.breakdown.isNotEmpty() }
    val grandTotal = owed.sumOf { it.totalOwed }

    if (payable.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, start = 20.dp, end = 20.dp)
        ) {
            EmptyState("Nothing owed yet. Tap Log Loss on the Board tab as you play, then come back here to settle up.")
            Spacer(Modifier.height(14.dp))
            Button(
                onClick = {},
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = Surface2Color,
                    disabledContentColor = MutedColor
                )
            ) {
                Text(
                    text = "MARK AS PAID",
                    fontFamily = RajdhaniFont,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(top = 4.dp, bottom = 100.dp, start = 20.dp, end = 20.dp)
    ) {
        items(payable, key = { it.playerId }) { p ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(10.dp)
                    )
            ) {
                Column {
                    // Player Total Header
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = p.name,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = String.format(java.util.Locale.getDefault(), "%,d", p.totalOwed),
                                fontFamily = AntonFont,
                                fontSize = 22.sp,
                                color = GoldColor
                            )
                            Text(
                                text = " BIRR",
                                fontFamily = RajdhaniFont,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldColor,
                                modifier = Modifier.padding(start = 2.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)

                    // Breakdown List
                    Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                        p.breakdown.forEach { b ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${b.categoryName} · ${b.losses}L × ${b.price}",
                                    fontFamily = RajdhaniFont,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MutedColor
                                )
                                Text(
                                    text = "${b.amount} Birr",
                                    fontFamily = RajdhaniFont,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // Grand Total Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 16.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Surface2Color)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(16.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TOTAL TO SETTLE",
                        fontFamily = RajdhaniFont,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = MutedColor
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = String.format(java.util.Locale.getDefault(), "%,d", grandTotal),
                            fontFamily = AntonFont,
                            fontSize = 26.sp,
                            color = GoldColor
                        )
                        Text(
                            text = " BIRR",
                            fontFamily = RajdhaniFont,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldColor,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                }
            }
        }

        // Action Button: Mark as Paid
        item {
            Button(
                onClick = {
                    if (viewModel.settle()) onSettled()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenColor,
                    contentColor = MaterialTheme.colorScheme.background
                )
            ) {
                Text(
                    text = "MARK AS PAID",
                    fontFamily = RajdhaniFont,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}