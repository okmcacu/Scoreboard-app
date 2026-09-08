package com.gamezone.scoreboard.model

data class Player(val id: String, val name: String)

data class Category(val id: String, val name: String, val price: Int)

data class Tally(val wins: Int = 0, val losses: Int = 0)

data class OwedBreakdown(
    val categoryId: String,
    val categoryName: String,
    val wins: Int,
    val losses: Int,
    val price: Int,
    val amount: Int
)

data class OwedPlayer(
    val playerId: String,
    val name: String,
    val breakdown: List<OwedBreakdown>,
    val totalOwed: Int
)

data class Settlement(
    val id: String,
    val date: String,
    val players: List<OwedPlayer>,
    val totalPaid: Int
)

enum class AppTab(val label: String) {
    BOARD("Board"),
    STATS("Stats"),
    SETTLE("Settle"),
    HISTORY("History"),
    GAMES("Games")
}
