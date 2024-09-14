package net.insi8.scoreboard.lib.repo

import kotlinx.coroutines.flow.Flow
import net.insi8.scoreboard.lib.model.Match

interface MatchStatusRepository {
    fun startMatch(match: Match)
    fun finishMatch(matchId: String)
    fun getScoreBoardOnGoingMatchesStream(): Flow<List<Match>>
    fun updateScore(matchId: String, homeTeamScore: Int, awayTeamScore: Int)
    fun getLeaderBoard(): Flow<List<Match>>
}