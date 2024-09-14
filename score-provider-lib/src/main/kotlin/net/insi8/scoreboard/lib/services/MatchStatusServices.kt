package net.insi8.scoreboard.lib.services

import kotlinx.coroutines.flow.Flow
import net.insi8.scoreboard.lib.extensions.generateId
import net.insi8.scoreboard.lib.model.Match
import net.insi8.scoreboard.lib.model.MatchStatus
import net.insi8.scoreboard.lib.repo.MatchStatusRepository
import java.time.LocalDateTime
import java.time.ZoneId

interface MatchStatusServices {
    fun startMatch(homeTeam: String, awayTeam: String)
    fun finishMatches(homeTeam: String, awayTeam: String)
    fun getScoreBoard(): Flow<List<Match>>

    fun updateScore(matchId: String, homeTeamScore: Int, awayTeamScore: Int)
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

    override fun finishMatches(homeTeam: String, awayTeam: String) {
        matchStatusRepository.finishMatch(matchId = generateId(homeTeam, awayTeam))
    }

    override fun getScoreBoard(): Flow<List<Match>> = matchStatusRepository.getScoreBoardOnGoingMatchesStream()

    override fun updateScore(matchId: String, homeTeamScore: Int, awayTeamScore: Int) {
        matchStatusRepository.updateScore(
            matchId = matchId,
            homeTeamScore = homeTeamScore,
            awayTeamScore = awayTeamScore
        )
    }
}