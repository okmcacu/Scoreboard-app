package com.gamezone.scoreboard.domain

import com.gamezone.scoreboard.model.Category
import com.gamezone.scoreboard.model.OwedBreakdown
import com.gamezone.scoreboard.model.OwedPlayer
import com.gamezone.scoreboard.model.Player
import com.gamezone.scoreboard.model.Settlement
import com.gamezone.scoreboard.model.Tally

typealias TallyMap = Map<String, Map<String, Tally>>

/** Pure score / expense calculations (no Android dependencies). */
object ScoreCalculator {

    fun getTally(tallies: TallyMap, catId: String, playerId: String): Tally =
        tallies[catId]?.get(playerId) ?: Tally()

    fun totalLosses(tallies: TallyMap): Int =
        tallies.values.sumOf { byP -> byP.values.sumOf { it.losses } }

    fun categoryLosses(tallies: TallyMap, catId: String): Int =
        tallies[catId]?.values?.sumOf { it.losses } ?: 0

    fun hasAnyHistory(allTimeStats: TallyMap, settlements: List<Settlement>): Boolean {
        if (settlements.isNotEmpty()) return true
        return allTimeStats.values.any { byP -> byP.values.any { it.wins > 0 || it.losses > 0 } }
    }

    fun computeOwed(players: List<Player>, categories: List<Category>, tallies: TallyMap): List<OwedPlayer> =
        players.map { p ->
            val breakdown = categories.mapNotNull { c ->
                val t = getTally(tallies, c.id, p.id)
                if (t.wins > 0 || t.losses > 0) {
                    OwedBreakdown(c.id, c.name, t.wins, t.losses, c.price, t.losses * c.price)
                } else null
            }
            OwedPlayer(p.id, p.name, breakdown, breakdown.sumOf { it.amount })
        }

    fun totalOwedAll(players: List<Player>, categories: List<Category>, tallies: TallyMap): Int =
        computeOwed(players, categories, tallies).sumOf { it.totalOwed }

    /** Returns a new map with [transform] applied to one tally; counts never go below zero. */
    fun updateTally(
        map: TallyMap,
        catId: String,
        playerId: String,
        transform: (Tally) -> Tally
    ): TallyMap {
        val byPlayer = (map[catId] ?: emptyMap()).toMutableMap()
        val current = byPlayer[playerId] ?: Tally()
        val next = transform(current)
        byPlayer[playerId] = Tally(maxOf(0, next.wins), maxOf(0, next.losses))
        return map.toMutableMap().apply { put(catId, byPlayer) }
    }
}
