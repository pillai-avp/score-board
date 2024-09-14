package net.insi8.scoreboard.lib.model

import java.time.LocalDateTime

sealed interface MatchStatus {
    data object NotStated : MatchStatus
    data class Progressing(val startedAt: LocalDateTime) : MatchStatus
    data class Finished(val startedAt: LocalDateTime, val endedTime: LocalDateTime) : MatchStatus
}


data class Match(
    val id: String,
    val homeTeam: String,
    val awayTeam: String,
    val status: MatchStatus = MatchStatus.NotStated,
    val score: Map<String, Int> = emptyMap()
)