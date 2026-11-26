package com.gamezone.scoreboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gamezone.scoreboard.ScoreboardViewModel
import com.gamezone.scoreboard.ui.theme.AntonFont
import com.gamezone.scoreboard.ui.theme.GoldColor
import com.gamezone.scoreboard.ui.theme.GreenColor
import com.gamezone.scoreboard.ui.theme.MutedColor
import com.gamezone.scoreboard.ui.theme.RajdhaniFont
import com.gamezone.scoreboard.ui.theme.RedColor

@Composable
fun SessionSummaryScreen(viewModel: ScoreboardViewModel, onBack: () -> Unit) {
    val summary = viewModel.computeSessionSummary()

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(top = 4.dp, bottom = 100.dp, start = 20.dp, end = 20.dp)
    ) {
        item {
            Row(
                Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SESSION SUMMARY",
                    fontFamily = AntonFont,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(onClick = onBack) { Text("Back") }
            }
        }

        if (summary.isEmpty()) {
            item { EmptyState("No players yet. Add one from the Games tab.") }
        } else {
            if (summary.all { it.totalScore == 0 && it.totalLosses == 0 && it.expenseShare == 0 }) {
                item { EmptyState("No games logged this session yet.") }
            }
            items(summary, key = { it.playerId }) { p ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = p.name,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        SummaryValue("SCORE", p.totalScore.toString(), GreenColor)
                        Box(Modifier.padding(horizontal = 8.dp))
                        SummaryValue("LOSSES", p.totalLosses.toString(), RedColor)
                        Box(Modifier.padding(horizontal = 8.dp))
                        SummaryValue(
                            "SHARE (BIRR)",
                            String.format(java.util.Locale.getDefault(), "%,d", p.expenseShare),
                            GoldColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryValue(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    androidx.compose.foundation.layout.Column(horizontalAlignment = Alignment.End) {
        Text(text = value, fontFamily = AntonFont, fontSize = 20.sp, color = color)
        Text(
            text = label,
            fontFamily = RajdhaniFont,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = MutedColor
        )
    }
}
