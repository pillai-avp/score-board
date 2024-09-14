package net.insi8.scoreboard.lib.services

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.insi8.scoreboard.lib.extensions.generateId
import net.insi8.scoreboard.lib.model.Match
import net.insi8.scoreboard.lib.model.MatchStatus
import net.insi8.scoreboard.lib.repo.MatchStatusRepository
import java.time.LocalDateTime
import java.time.ZoneId

interface MatchStatusServices {
    fun startMatch(homeTeam: String, awayTeam: String)
    fun finishMatch(matchId: String)
    fun getScoreBoard(): Flow<List<Match>>
    fun updateScore(matchId: String, homeTeamScore: Int, awayTeamScore: Int)
    fun leaderBoard(): Flow<List<Match>>
}

class MatchStatusServicesImpl(private val matchStatusRepository: MatchStatusRepository) : MatchStatusServices {
    override fun startMatch(homeTeam: String, awayTeam: String) {
        val matchStarted = Match(
            id = generateId(homeTeam, awayTeam),
            homeTeam = homeTeam,
            awayTeam = awayTeam,
            status = MatchStatus.Progressing(startedAt = LocalDateTime.now(ZoneId.of("UTC"))),
            score = mutableMapOf(homeTeam to 0, awayTeam to 0)
        )

        matchStatusRepository.startMatch(matchStarted)
    }

    override fun finishMatch(matchId: String) {
        matchStatusRepository.finishMatch(matchId = matchId)
    }

    override fun getScoreBoard(): Flow<List<Match>> = matchStatusRepository.getScoreBoardOnGoingMatchesStream()

    override fun updateScore(matchId: String, homeTeamScore: Int, awayTeamScore: Int) {
        matchStatusRepository.updateScore(
            matchId = matchId,
            homeTeamScore = homeTeamScore,
            awayTeamScore = awayTeamScore
        )
    }
    override fun leaderBoard(): Flow<List<Match>> = matchStatusRepository.getLeaderBoard().map { matches ->
        // Sort order is last started match with the highest number of total goals by both teams
        matches.sortedByDescending { match ->
            (match.status as MatchStatus.Finished).startedAt
        }.sortedByDescending { match ->
            match.score.values.reduce { acc, i -> acc + i }
        }
    }
}