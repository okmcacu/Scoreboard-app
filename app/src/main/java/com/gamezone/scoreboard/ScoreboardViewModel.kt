package com.gamezone.scoreboard

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gamezone.scoreboard.model.Category
import com.gamezone.scoreboard.model.OwedBreakdown
import com.gamezone.scoreboard.model.OwedPlayer
import com.gamezone.scoreboard.model.Player
import com.gamezone.scoreboard.model.Settlement
import com.gamezone.scoreboard.model.Tally
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class WinnerSelectionState(
    val categoryId: String,
    val loserId: String
)

// Data structure wrapper for JSON serialization
data class ScoreboardSavedState(
    val players: List<Player>,
    val categories: List<Category>,
    val tallies: Map<String, Map<String, Tally>>,
    val allTimeStats: Map<String, Map<String, Tally>>,
    val settlements: List<Settlement>
)

class ScoreboardViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("scoreboard_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    var players by mutableStateOf<List<Player>>(
        listOf(
            Player("p1", "Mikiyas"),
            Player("p2", "Nathan"),
            Player("p3", "Yonas")
        )
    )
        private set

    var categories by mutableStateOf<List<Category>>(
        listOf(
            Category("c1", "EA FC", 20),
            Category("c2", "UFC", 20),
            Category("c3", "Pool", 20)
        )
    )
        private set

    var tallies by mutableStateOf<Map<String, Map<String, Tally>>>(emptyMap())
        private set

    var allTimeStats by mutableStateOf<Map<String, Map<String, Tally>>>(emptyMap())
        private set

    var settlements by mutableStateOf<List<Settlement>>(emptyList())
        private set

    var activeWinnerSelection by mutableStateOf<WinnerSelectionState?>(null)
        private set

    init {
        loadData()
    }

    private fun saveData() {
        val state = ScoreboardSavedState(players, categories, tallies, allTimeStats, settlements)
        val json = gson.toJson(state)
        prefs.edit().putString("scoreboard_state", json).apply()
    }

    private fun loadData() {
        val json = prefs.getString("scoreboard_state", null) ?: return
        try {
            val type = object : TypeToken<ScoreboardSavedState>() {}.type
            val loadedState: ScoreboardSavedState = gson.fromJson(json, type)
            players = loadedState.players
            categories = loadedState.categories
            tallies = loadedState.tallies
            allTimeStats = loadedState.allTimeStats
            settlements = loadedState.settlements
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getTally(catId: String, playerId: String): Tally = tallies[catId]?.get(playerId) ?: Tally()
    fun getAllTimeTally(catId: String, playerId: String): Tally = allTimeStats[catId]?.get(playerId) ?: Tally()

    fun totalLosses(): Int = tallies.values.sumOf { byP -> byP.values.sumOf { it.losses } }
    fun categoryLosses(catId: String): Int = tallies[catId]?.values?.sumOf { it.losses } ?: 0

    fun hasAnyHistory(): Boolean {
        if (settlements.isNotEmpty()) return true
        return allTimeStats.values.any { byP -> byP.values.any { it.wins > 0 || it.losses > 0 } }
    }

    fun computeOwed(): List<OwedPlayer> = players.map { p ->
        val breakdown = categories.mapNotNull { c ->
            val t = getTally(c.id, p.id)
            if (t.wins > 0 || t.losses > 0) {
                OwedBreakdown(c.id, c.name, t.wins, t.losses, c.price, t.losses * c.price)
            } else null
        }
        OwedPlayer(p.id, p.name, breakdown, breakdown.sumOf { it.amount })
    }

    fun totalOwedAll(): Int = computeOwed().sumOf { it.totalOwed }

    fun initiateLogLoss(catId: String, loserId: String) {
        val eligibleWinners = players.filter { it.id != loserId }
        if (eligibleWinners.isEmpty()) return

        if (eligibleWinners.size == 1) {
            logLoss(catId, loserId = loserId, winnerId = eligibleWinners.first().id)
        } else {
            activeWinnerSelection = WinnerSelectionState(catId, loserId)
        }
    }

    fun confirmWinner(winnerId: String) {
        val selection = activeWinnerSelection ?: return
        logLoss(selection.categoryId, selection.loserId, winnerId)
        dismissWinnerSelection()
    }

    fun dismissWinnerSelection() {
        activeWinnerSelection = null
    }

    fun logLoss(catId: String, loserId: String, winnerId: String) {
        tallies = updateTally(tallies, catId, loserId) { it.copy(losses = it.losses + 1) }
        tallies = updateTally(tallies, catId, winnerId) { it.copy(wins = it.wins + 1) }
        allTimeStats = updateTally(allTimeStats, catId, loserId) { it.copy(losses = it.losses + 1) }
        allTimeStats = updateTally(allTimeStats, catId, winnerId) { it.copy(wins = it.wins + 1) }
        saveData()
    }

    fun undo(catId: String, playerId: String) {
        val t = getTally(catId, playerId)
        if (t.losses > 0) {
            tallies = updateTally(tallies, catId, playerId) { it.copy(losses = it.losses - 1) }
            val at = getAllTimeTally(catId, playerId)
            if (at.losses > 0) {
                allTimeStats = updateTally(allTimeStats, catId, playerId) { it.copy(losses = it.losses - 1) }
            }
        } else if (t.wins > 0) {
            tallies = updateTally(tallies, catId, playerId) { it.copy(wins = it.wins - 1) }
            val at = getAllTimeTally(catId, playerId)
            if (at.wins > 0) {
                allTimeStats = updateTally(allTimeStats, catId, playerId) { it.copy(wins = it.wins - 1) }
            }
        }
        saveData()
    }

    fun settle(): Boolean {
        val owed = computeOwed()
        val totalPaid = owed.sumOf { it.totalOwed }
        if (totalPaid <= 0) return false
        settlements = settlements + Settlement(
            id = newId("st"),
            date = today(),
            players = owed.filter { it.breakdown.isNotEmpty() },
            totalPaid = totalPaid
        )
        tallies = emptyMap()
        saveData()
        return true
    }

    fun deleteSettlement(id: String) {
        val s = settlements.find { it.id == id } ?: return
        var newAllTime = allTimeStats
        s.players.forEach { p ->
            p.breakdown.forEach { b ->
                val current = newAllTime[b.categoryId]?.get(p.playerId) ?: Tally()
                val updated = Tally(
                    wins = maxOf(0, current.wins - b.wins),
                    losses = maxOf(0, current.losses - b.losses)
                )
                val byPlayer = (newAllTime[b.categoryId] ?: emptyMap()).toMutableMap()
                byPlayer[p.playerId] = updated
                newAllTime = newAllTime.toMutableMap().apply { put(b.categoryId, byPlayer) }
            }
        }
        allTimeStats = newAllTime
        settlements = settlements.filterNot { it.id == id }
        saveData()
    }

    fun clearHistory() {
        allTimeStats = emptyMap()
        settlements = emptyList()
        saveData()
    }

    fun addCategory(name: String, price: Int) {
        if (name.isBlank()) return
        categories = categories + Category(newId("c"), name.trim(), price)
        saveData()
    }

    fun removeCategory(id: String) {
        categories = categories.filterNot { it.id == id }
        tallies = tallies.toMutableMap().apply { remove(id) }
        allTimeStats = allTimeStats.toMutableMap().apply { remove(id) }
        saveData()
    }

    fun updateCategoryPrice(id: String, price: Int) {
        categories = categories.map { if (it.id == id) it.copy(price = price) else it }
        saveData()
    }

    fun addPlayer(name: String) {
        if (name.isBlank()) return
        players = players + Player(newId("p"), name.trim())
        saveData()
    }

    fun removePlayer(id: String) {
        players = players.filterNot { it.id == id }
        saveData()
    }

    fun exportCsv(): String {
        val rows = mutableListOf(listOf("Game", "Player", "Wins", "Losses"))
        categories.forEach { c ->
            players.forEach { p ->
                val t = getAllTimeTally(c.id, p.id)
                rows.add(listOf(c.name, p.name, t.wins.toString(), t.losses.toString()))
            }
        }
        return rows.joinToString("\n") { row -> row.joinToString(",") { csvEscape(it) } }
    }

    fun importCsv(text: String) {
        val lines = text.split(Regex("\r?\n")).filter { it.isNotBlank() }
        if (lines.isEmpty()) return
        var startIdx = 0
        val first = parseCsvLine(lines[0]).map { it.trim().lowercase(Locale.ROOT) }
        if (first.getOrNull(0) == "game" && first.getOrNull(1) == "player") startIdx = 1

        var newCategories = categories
        var newPlayers = players
        var newAllTime = allTimeStats

        for (i in startIdx until lines.size) {
            val cols = parseCsvLine(lines[i]).map { it.trim() }
            if (cols.size < 4) continue
            val gameName = cols[0]
            val playerName = cols[1]
            val winsStr = cols[2]
            val lossesStr = cols[3]
            if (gameName.isBlank() || playerName.isBlank()) continue

            var cat = newCategories.find { it.name.equals(gameName, ignoreCase = true) }
            if (cat == null) {
                cat = Category(newId("c"), gameName, 20)
                newCategories = newCategories + cat
            }
            var player = newPlayers.find { it.name.equals(playerName, ignoreCase = true) }
            if (player == null) {
                player = Player(newId("p"), playerName)
                newPlayers = newPlayers + player
            }
            val byPlayer = (newAllTime[cat.id] ?: emptyMap()).toMutableMap()
            byPlayer[player.id] = Tally(winsStr.toIntOrNull() ?: 0, lossesStr.toIntOrNull() ?: 0)
            newAllTime = newAllTime.toMutableMap().apply { put(cat.id, byPlayer) }
        }
        categories = newCategories
        players = newPlayers
        allTimeStats = newAllTime
        saveData()
    }

    private fun updateTally(
        map: Map<String, Map<String, Tally>>,
        catId: String,
        playerId: String,
        transform: (Tally) -> Tally
    ): Map<String, Map<String, Tally>> {
        val byPlayer = (map[catId] ?: emptyMap()).toMutableMap()
        val current = byPlayer[playerId] ?: Tally()
        val next = transform(current)
        byPlayer[playerId] = Tally(maxOf(0, next.wins), maxOf(0, next.losses))
        return map.toMutableMap().apply { put(catId, byPlayer) }
    }
}

private fun csvEscape(v: String): String =
    if (Regex("[\",\n]").containsMatchIn(v)) "\"" + v.replace("\"", "\"\"") + "\"" else v

private fun parseCsvLine(line: String): List<String> {
    val result = mutableListOf<String>()
    val cur = StringBuilder()
    var inQuotes = false
    var i = 0
    while (i < line.length) {
        val ch = line[i]
        if (inQuotes) {
            if (ch == '"') {
                if (i + 1 < line.length && line[i + 1] == '"') {
                    cur.append('"')
                    i++
                } else {
                    inQuotes = false
                }
            } else {
                cur.append(ch)
            }
        } else {
            if (ch == '"') {
                inQuotes = true
            } else if (ch == ',') {
                result.add(cur.toString())
                cur.clear()
            } else {
                cur.append(ch)
            }
        }
        i++
    }
    result.add(cur.toString())
    return result
}

fun newId(prefix: String): String =
    prefix + "_" + System.currentTimeMillis().toString(36) + (0..4).map { ('a'..'z').random() }.joinToString("")

fun today(): String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)