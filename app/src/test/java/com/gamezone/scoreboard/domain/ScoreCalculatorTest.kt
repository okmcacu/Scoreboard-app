package com.gamezone.scoreboard.domain

import com.gamezone.scoreboard.model.Category
import com.gamezone.scoreboard.model.Player
import com.gamezone.scoreboard.model.Settlement
import com.gamezone.scoreboard.model.Tally
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScoreCalculatorTest {

    private val alice = Player("p1", "Alice")
    private val bob = Player("p2", "Bob")
    private val chess = Category("c1", "Chess", 20)
    private val pool = Category("c2", "Pool", 5)

    private val tallies: TallyMap = mapOf(
        "c1" to mapOf("p1" to Tally(wins = 1, losses = 3), "p2" to Tally(wins = 3, losses = 1)),
        "c2" to mapOf("p1" to Tally(wins = 0, losses = 2))
    )

    @Test
    fun getTally_returnsStoredOrEmpty() {
        assertEquals(Tally(1, 3), ScoreCalculator.getTally(tallies, "c1", "p1"))
        assertEquals(Tally(), ScoreCalculator.getTally(tallies, "c2", "p2"))
        assertEquals(Tally(), ScoreCalculator.getTally(tallies, "missing", "p1"))
    }

    @Test
    fun totalLosses_sumsAcrossCategoriesAndPlayers() {
        assertEquals(6, ScoreCalculator.totalLosses(tallies))
        assertEquals(0, ScoreCalculator.totalLosses(emptyMap()))
    }

    @Test
    fun categoryLosses_sumsOneCategory() {
        assertEquals(4, ScoreCalculator.categoryLosses(tallies, "c1"))
        assertEquals(2, ScoreCalculator.categoryLosses(tallies, "c2"))
        assertEquals(0, ScoreCalculator.categoryLosses(tallies, "nope"))
    }

    @Test
    fun computeOwed_multipliesLossesByPrice() {
        val owed = ScoreCalculator.computeOwed(listOf(alice, bob), listOf(chess, pool), tallies)

        val a = owed[0]
        assertEquals("p1", a.playerId)
        assertEquals(2, a.breakdown.size)
        assertEquals(60, a.breakdown[0].amount)
        assertEquals(10, a.breakdown[1].amount)
        assertEquals(70, a.totalOwed)

        val b = owed[1]
        assertEquals(1, b.breakdown.size) // no activity in Pool -> omitted
        assertEquals(20, b.totalOwed)
    }

    @Test
    fun computeOwed_winsOnlyOwesNothingButIsListed() {
        val t: TallyMap = mapOf("c1" to mapOf("p1" to Tally(wins = 2, losses = 0)))
        val owed = ScoreCalculator.computeOwed(listOf(alice), listOf(chess), t)
        assertEquals(1, owed[0].breakdown.size)
        assertEquals(0, owed[0].totalOwed)
    }

    @Test
    fun computeOwed_noActivityGivesEmptyBreakdown() {
        val owed = ScoreCalculator.computeOwed(listOf(alice), listOf(chess), emptyMap())
        assertTrue(owed[0].breakdown.isEmpty())
        assertEquals(0, owed[0].totalOwed)
    }

    @Test
    fun totalOwedAll_sumsEveryPlayer() {
        assertEquals(90, ScoreCalculator.totalOwedAll(listOf(alice, bob), listOf(chess, pool), tallies))
    }

    @Test
    fun hasAnyHistory() {
        assertFalse(ScoreCalculator.hasAnyHistory(emptyMap(), emptyList()))
        assertFalse(ScoreCalculator.hasAnyHistory(mapOf("c1" to mapOf("p1" to Tally())), emptyList()))
        assertTrue(ScoreCalculator.hasAnyHistory(tallies, emptyList()))
        assertTrue(ScoreCalculator.hasAnyHistory(emptyMap(), listOf(Settlement("s", "2024-01-01", emptyList(), 0))))
    }

    @Test
    fun updateTally_incrementsWithoutMutatingInput() {
        val updated = ScoreCalculator.updateTally(tallies, "c1", "p1") { it.copy(losses = it.losses + 1) }
        assertEquals(Tally(1, 4), updated["c1"]!!["p1"])
        assertEquals(Tally(1, 3), tallies["c1"]!!["p1"])
        assertEquals(Tally(3, 1), updated["c1"]!!["p2"])
    }

    @Test
    fun updateTally_createsMissingEntryAndClampsAtZero() {
        val created = ScoreCalculator.updateTally(emptyMap(), "c9", "p9") { it.copy(wins = it.wins + 1) }
        assertEquals(Tally(1, 0), created["c9"]!!["p9"])

        val clamped = ScoreCalculator.updateTally(emptyMap(), "c9", "p9") { it.copy(losses = it.losses - 1) }
        assertEquals(Tally(0, 0), clamped["c9"]!!["p9"])
    }
}
