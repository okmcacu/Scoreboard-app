package com.gamezone.scoreboard.ui

/** Result of validating a numeric amount (game price / score value) typed by the user. */
sealed class AmountResult {
    data class Valid(val value: Int) : AmountResult()
    data class Invalid(val message: String) : AmountResult()
}

/**
 * Validates a user-entered amount. Rejects empty, non-numeric, negative
 * and out-of-range values with a human-readable message.
 */
fun validateAmount(text: String, label: String = "Amount"): AmountResult {
    val trimmed = text.trim()
    if (trimmed.isEmpty()) return AmountResult.Invalid("$label is required")
    if (!Regex("[+-]?\\d+").matches(trimmed)) return AmountResult.Invalid("$label must be a whole number")
    val value = trimmed.toIntOrNull()
        ?: return AmountResult.Invalid(
            if (trimmed.startsWith("-")) "$label cannot be negative" else "$label is too large"
        )
    if (value < 0) return AmountResult.Invalid("$label cannot be negative")
    return AmountResult.Valid(value)
}
