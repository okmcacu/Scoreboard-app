package com.gamezone.scoreboard.ui

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.gamezone.scoreboard.ScoreboardViewModel
import com.gamezone.scoreboard.ui.theme.GreenColor
import com.gamezone.scoreboard.ui.theme.MutedColor
import com.gamezone.scoreboard.ui.theme.RedColor
import com.gamezone.scoreboard.ui.theme.Surface2Color
import java.io.BufferedReader
import java.io.InputStreamReader

@Composable
fun StatsScreen(viewModel: ScoreboardViewModel) {
    val context = LocalContext.current

    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        try {
            context.contentResolver.openOutputStream(uri)?.use { out ->
                out.write(viewModel.exportCsv().toByteArray())
            }
            Toast.makeText(context, "Stats exported", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        try {
            val text = context.contentResolver.openInputStream(uri)?.use { input ->
                BufferedReader(InputStreamReader(input)).readText()
            } ?: ""
            viewModel.importCsv(text)
            Toast.makeText(context, "Stats imported", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Import failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    if (viewModel.categories.isEmpty()) {
        EmptyState("No games yet. Add one from the Games tab.")
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 8.dp)
    ) {
        item {
            Text(
                "ALL-TIME STATS",
                style = MaterialTheme.typography.labelMedium,
                color = MutedColor,
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }
        item {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { exportLauncher.launch("stats.csv") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Download CSV")
                }
                Button(
                    onClick = { importLauncher.launch(arrayOf("text/csv", "text/comma-separated-values", "*/*")) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Upload CSV")
                }
            }
        }

        items(viewModel.categories, key = { it.id }) { category ->
            val rows = viewModel.players.map { p ->
                val t = viewModel.getAllTimeTally(category.id, p.id)
                val total = t.wins + t.losses
                val winPct = if (total > 0) (t.wins * 100) / total else 0
                Triple(p.name, t, winPct)
            }.sortedWith(compareByDescending<Triple<String, com.gamezone.scoreboard.model.Tally, Int>> { it.second.wins }
                .thenBy { it.second.losses })

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Text(
                    category.name.uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(Modifier.padding(14.dp))
                )
                if (viewModel.players.isEmpty()) {
                    Text(
                        "No players yet.",
                        color = MutedColor,
                        modifier = Modifier.padding(14.dp)
                    )
                } else {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("PLAYER", Modifier.weight(2f), style = MaterialTheme.typography.labelSmall, color = MutedColor)
                        Text("W", Modifier.weight(1f), style = MaterialTheme.typography.labelSmall, color = MutedColor, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        Text("L", Modifier.weight(1f), style = MaterialTheme.typography.labelSmall, color = MutedColor, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        Text("WIN%", Modifier.weight(1f), style = MaterialTheme.typography.labelSmall, color = MutedColor, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
                    rows.forEach { (name, t, winPct) ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(name, Modifier.weight(2f), style = MaterialTheme.typography.bodyLarge)
                            Text("${t.wins}", Modifier.weight(1f), color = GreenColor, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                            Text("${t.losses}", Modifier.weight(1f), color = RedColor, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                            Text("$winPct%", Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                    }
                }
            }
        }
    }
}
