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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.gamezone.scoreboard.ui.theme.MutedColor
import com.gamezone.scoreboard.ui.theme.RajdhaniFont
import com.gamezone.scoreboard.ui.theme.RedColor
import com.gamezone.scoreboard.ui.theme.Surface2Color

@Composable
fun HistoryScreen(viewModel: ScoreboardViewModel) {
    val sorted = viewModel.settlements.sortedByDescending { it.date }

    if (sorted.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 20.dp, end = 20.dp)
        ) {
            EmptyState("No settlements yet. Settle up from the Settle tab once you've logged some games.")
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(top = 4.dp, bottom = 100.dp, start = 20.dp, end = 20.dp)
    ) {
        items(sorted, key = { it.id }) { s ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(10.dp)
                    )
            ) {
                Column {
                    // Settlement Header (.settlement-header)
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(Surface2Color)
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = s.date.uppercase(),
                            fontFamily = RajdhaniFont,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = MutedColor
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = String.format(java.util.Locale.getDefault(), "%,d", s.totalPaid),
                                fontFamily = AntonFont,
                                fontSize = 20.sp,
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

                    // Players Breakdown List
                    Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                        s.players.forEachIndexed { index, p ->
                            Column(Modifier.padding(vertical = 6.dp)) {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = p.name,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = String.format(java.util.Locale.getDefault(), "%,d", p.totalOwed),
                                            fontFamily = RajdhaniFont,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldColor
                                        )
                                        Text(
                                            text = " Birr",
                                            fontFamily = RajdhaniFont,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MutedColor
                                        )
                                    }
                                }

                                if (p.breakdown.isNotEmpty()) {
                                    Row(
                                        Modifier.padding(top = 4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        p.breakdown.forEach { b ->
                                            Text(
                                                text = "${b.categoryName} ${b.losses}L",
                                                fontFamily = RajdhaniFont,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = RedColor
                                            )
                                        }
                                    }
                                }
                            }
                            if (index < s.players.size - 1) {
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                    thickness = 1.dp,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)

                    // Delete Action Row
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { viewModel.deleteSettlement(s.id) },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "DELETE",
                                fontFamily = RajdhaniFont,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = RedColor
                            )
                        }
                    }
                }
            }
        }
    }
}