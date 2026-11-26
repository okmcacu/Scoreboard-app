package com.gamezone.scoreboard.data

import android.content.SharedPreferences
import com.gamezone.scoreboard.model.Category
import com.gamezone.scoreboard.model.Player
import com.gamezone.scoreboard.model.Settlement
import com.gamezone.scoreboard.model.Tally
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Data structure wrapper for JSON serialization
data class ScoreboardSavedState(
    val players: List<Player>,
    val categories: List<Category>,
    val tallies: Map<String, Map<String, Tally>>,
    val allTimeStats: Map<String, Map<String, Tally>>,
    val settlements: List<Settlement>
)

/**
 * Owns all persistence for the scoreboard. The UI/ViewModel layer talks to this
 * class instead of touching SharedPreferences or JSON directly.
 */
class ScoreboardRepository(
    private val prefs: SharedPreferences,
    private val gson: Gson = Gson()
) {

    fun save(state: ScoreboardSavedState) {
        prefs.edit().putString(KEY_STATE, gson.toJson(state)).apply()
    }

    /** Returns the saved state, or null if nothing is stored or it cannot be parsed. */
    fun load(): ScoreboardSavedState? {
        val json = prefs.getString(KEY_STATE, null) ?: return null
        return try {
            val type = object : TypeToken<ScoreboardSavedState>() {}.type
            gson.fromJson<ScoreboardSavedState>(json, type)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        const val PREFS_NAME = "scoreboard_prefs"
        private const val KEY_STATE = "scoreboard_state"
    }
}
