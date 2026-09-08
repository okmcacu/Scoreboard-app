package com.gamezone.scoreboard

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gamezone.scoreboard.model.AppTab
import com.gamezone.scoreboard.ui.BoardScreen
import com.gamezone.scoreboard.ui.GamesScreen
import com.gamezone.scoreboard.ui.HistoryScreen
import com.gamezone.scoreboard.ui.SettleScreen
import com.gamezone.scoreboard.ui.StatsScreen
import com.gamezone.scoreboard.ui.theme.AntonFont
import com.gamezone.scoreboard.ui.theme.CardBackground
import com.gamezone.scoreboard.ui.theme.GreenColor
import com.gamezone.scoreboard.ui.theme.MutedColor
import com.gamezone.scoreboard.ui.theme.RedColor
import com.gamezone.scoreboard.ui.theme.ScoreboardTheme

class MainActivity : ComponentActivity() {
    private val viewModel: ScoreboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScoreboardTheme {
                ScoreboardApp(viewModel)
            }
        }
    }
}

@Composable
fun ScoreboardApp(viewModel: ScoreboardViewModel) {
    var tab by remember { mutableStateOf(AppTab.BOARD) }
    var showMenu by remember { mutableStateOf(false) }
    var showClearHistoryDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Launcher for exporting CSV file
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { stream ->
                stream.write(viewModel.exportCsv().toByteArray())
            }
            Toast.makeText(context, "Exported successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher for importing CSV file
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.openInputStream(it)?.use { stream ->
                val content = stream.bufferedReader().use { reader -> reader.readText() }
                viewModel.importCsv(content)
            }
            Toast.makeText(context, "Imported successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardBackground)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Side: Brand and Subtitle
                    Column {
                        Text(
                            text = "GAME ZONE",
                            style = MaterialTheme.typography.labelSmall,
                            color = GreenColor,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "SCOREBOARD",
                            fontFamily = AntonFont,
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    // Right Side: Loss Counter + More Options Menu
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${viewModel.totalLosses()}",
                                color = RedColor,
                                fontFamily = AntonFont,
                                fontSize = 28.sp
                            )
                            Text(
                                text = "TOTAL LOSSES",
                                style = MaterialTheme.typography.labelSmall,
                                color = MutedColor,
                                fontSize = 9.sp
                            )
                        }

                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Options Menu",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }

                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Export CSV") },
                                    leadingIcon = { Icon(Icons.Default.Download, contentDescription = null) },
                                    onClick = {
                                        showMenu = false
                                        exportLauncher.launch("scoreboard_data.csv")
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Import CSV") },
                                    leadingIcon = { Icon(Icons.Default.Upload, contentDescription = null) },
                                    onClick = {
                                        showMenu = false
                                        importLauncher.launch("*/*")
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Clear History", color = RedColor) },
                                    leadingIcon = { Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = RedColor) },
                                    onClick = {
                                        showMenu = false
                                        showClearHistoryDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                NavigationBarItem(
                    selected = tab == AppTab.BOARD,
                    onClick = { tab = AppTab.BOARD },
                    icon = { Icon(Icons.Filled.SportsEsports, contentDescription = null) },
                    label = { Text("Board") }
                )
                NavigationBarItem(
                    selected = tab == AppTab.STATS,
                    onClick = { tab = AppTab.STATS },
                    icon = { Icon(Icons.Filled.BarChart, contentDescription = null) },
                    label = { Text("Stats") }
                )
                NavigationBarItem(
                    selected = tab == AppTab.SETTLE,
                    onClick = { tab = AppTab.SETTLE },
                    icon = { Icon(Icons.Filled.Payments, contentDescription = null) },
                    label = { Text("Settle") }
                )
                NavigationBarItem(
                    selected = tab == AppTab.HISTORY,
                    onClick = { tab = AppTab.HISTORY },
                    icon = { Icon(Icons.Filled.History, contentDescription = null) },
                    label = { Text("History") }
                )
                NavigationBarItem(
                    selected = tab == AppTab.GAMES,
                    onClick = { tab = AppTab.GAMES },
                    icon = { Icon(Icons.Filled.Groups, contentDescription = null) },
                    label = { Text("Games") }
                )
            }
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            when (tab) {
                AppTab.BOARD -> BoardScreen(viewModel)
                AppTab.STATS -> StatsScreen(viewModel)
                AppTab.SETTLE -> SettleScreen(viewModel) { tab = AppTab.HISTORY }
                AppTab.HISTORY -> HistoryScreen(viewModel)
                AppTab.GAMES -> GamesScreen(viewModel)
            }
        }

        if (showClearHistoryDialog) {
            AlertDialog(
                onDismissRequest = { showClearHistoryDialog = false },
                title = { Text("Clear History?") },
                text = { Text("Are you sure you want to clear all history and settlements? This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.clearHistory()
                            showClearHistoryDialog = false
                            Toast.makeText(context, "History cleared", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text("Clear", color = RedColor)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearHistoryDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}