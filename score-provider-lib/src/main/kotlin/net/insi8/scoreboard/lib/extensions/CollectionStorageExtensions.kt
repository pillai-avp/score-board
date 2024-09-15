package net.insi8.scoreboard.lib.extensions

import net.insi8.scoreboard.lib.model.Match
import net.insi8.scoreboard.lib.model.MatchStatus

fun <T> List<T>.replace(old: T, newItem: T): List<T> {
    val index = indexOf(old)
    return this.toMutableList().apply {
        removeAt(index)
        add(index, newItem)
    }
}

fun List<Match>.getAllProgressingMatches(): List<Match> {
    return this.filter { it.status is MatchStatus.Progressing }
}

fun List<Match>.getAllFinishedMatches(): List<Match> {
    return this.filter { it.status is MatchStatus.Finished }
}