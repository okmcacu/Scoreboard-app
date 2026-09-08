package com.gamezone.scoreboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.gamezone.scoreboard.ScoreboardViewModel
import com.gamezone.scoreboard.ui.theme.AntonFont
import com.gamezone.scoreboard.ui.theme.GoldColor
import com.gamezone.scoreboard.ui.theme.GreenColor
import com.gamezone.scoreboard.ui.theme.MutedColor
import com.gamezone.scoreboard.ui.theme.RajdhaniFont
import com.gamezone.scoreboard.ui.theme.RedColor
import com.gamezone.scoreboard.ui.theme.Surface2Color
import com.gamezone.scoreboard.ui.theme.SurfaceRaisedColor

private data class PendingLoss(val categoryId: String, val loserId: String)

@Composable
fun BoardScreen(viewModel: ScoreboardViewModel) {
    var pending by remember { mutableStateOf<PendingLoss?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(top = 4.dp, bottom = 100.dp, start = 20.dp, end = 20.dp)
    ) {
        item {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatBox(
                    label = "Total Losses",
                    value = viewModel.totalLosses().toString(),
                    valueColor = RedColor,
                    modifier = Modifier.weight(1f)
                )
                StatBox(
                    label = "Birr Owed",
                    value = String.format("%,d", viewModel.totalOwedAll()),
                    valueColor = GoldColor,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (viewModel.categories.isEmpty()) {
            item { EmptyState("No games yet. Add one from the Games tab.") }
        }

        itemsIndexed(viewModel.categories, key = { _, c -> c.id }) { index, category ->
            val categoryLosses = viewModel.players.sumOf { viewModel.getTally(category.id, it.id).losses }
            val borderAccentColor = when (index % 3) {
                1 -> RedColor
                2 -> GoldColor
                else -> GreenColor
            }

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
                Row(Modifier.fillMaxWidth()) {
                    // Left Border Accent Bar
                    Box(
                        Modifier
                            .width(3.dp)
                            .fillMaxHeight()
                            .background(borderAccentColor)
                    )

                    Column(Modifier.weight(1f)) {
                        // Game Card Header
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .background(Surface2Color)
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = category.name.uppercase(),
                                fontFamily = AntonFont,
                                fontSize = 19.sp,
                                letterSpacing = 0.5.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${category.price} BIRR / LOSS",
                                    fontFamily = RajdhaniFont,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MutedColor
                                )
                                Text(
                                    text = "$categoryLosses LOSSES",
                                    fontFamily = RajdhaniFont,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RedColor
                                )
                            }
                        }

                        // Players Rows
                        if (viewModel.players.isEmpty()) {
                            Text(
                                text = "Add players from the Games tab.",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp,
                                color = MutedColor
                            )
                        } else {
                            viewModel.players.forEach { player ->
                                val t = viewModel.getTally(category.id, player.id)
                                Column {
                                    Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
                                    Row(
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = player.name,
                                            modifier = Modifier.weight(1f),
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            modifier = Modifier.padding(end = 10.dp)
                                        ) {
                                            Text(
                                                text = "${t.wins}W",
                                                fontFamily = RajdhaniFont,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = GreenColor
                                            )
                                            Text(
                                                text = "${t.losses}L",
                                                fontFamily = RajdhaniFont,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = RedColor
                                            )
                                        }

                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            // Log Loss Button
                                            Button(
                                                onClick = {
                                                    if (viewModel.players.size > 1) {
                                                        pending = PendingLoss(category.id, player.id)
                                                    }
                                                },
                                                enabled = viewModel.players.size > 1,
                                                shape = RoundedCornerShape(6.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = RedColor.copy(alpha = 0.14f),
                                                    contentColor = RedColor
                                                ),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 7.dp)
                                            ) {
                                                Text(
                                                    text = "LOG LOSS",
                                                    fontFamily = RajdhaniFont,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    letterSpacing = 0.5.sp
                                                )
                                            }

                                            // Undo Button
                                            if (t.wins > 0 || t.losses > 0) {
                                                Button(
                                                    onClick = { viewModel.undo(category.id, player.id) },
                                                    shape = RoundedCornerShape(6.dp),
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = Surface2Color,
                                                        contentColor = MutedColor
                                                    ),
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 7.dp)
                                                ) {
                                                    Text(
                                                        text = "↺",
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Dialog: "Who Won?"
    pending?.let { p ->
        val category = viewModel.categories.find { it.id == p.categoryId }
        val loser = viewModel.players.find { it.id == p.loserId }
        val others = viewModel.players.filter { it.id != p.loserId }

        Dialog(onDismissRequest = { pending = null }) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SurfaceRaisedColor,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${loser?.name.orEmpty()} lost at ${category?.name.orEmpty()}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "WHO WON?",
                        fontFamily = RajdhaniFont,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = MutedColor,
                        modifier = Modifier.padding(top = 6.dp)
                    )

                    Spacer(Modifier.height(18.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (others.isEmpty()) {
                            Text(
                                text = "Add another player first.",
                                fontSize = 13.sp,
                                color = MutedColor,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp)
                            )
                        } else {
                            others.forEach { winner ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Surface2Color)
                                        .border(
                                            1.dp,
                                            MaterialTheme.colorScheme.outline,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            viewModel.logLoss(p.categoryId, p.loserId, winner.id)
                                            pending = null
                                        }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = winner.name,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(6.dp))

                    TextButton(
                        onClick = { pending = null },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    ) {
                        Text(
                            text = "CANCEL",
                            fontFamily = RajdhaniFont,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = MutedColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatBox(label: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
            .padding(vertical = 14.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                fontFamily = AntonFont,
                fontSize = 28.sp,
                color = valueColor
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = label.uppercase(),
                fontFamily = RajdhaniFont,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = MutedColor
            )
        }
    }
}

@Composable
fun EmptyState(text: String) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp, horizontal = 16.dp)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(40.dp, 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = MutedColor,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
    }
}