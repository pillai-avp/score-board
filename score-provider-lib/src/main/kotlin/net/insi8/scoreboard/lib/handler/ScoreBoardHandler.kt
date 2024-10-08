package net.insi8.scoreboard.lib.handler

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import net.insi8.scoreboard.lib.errors.InvalidOperationException
import net.insi8.scoreboard.lib.extensions.generateId
import net.insi8.scoreboard.lib.model.Match
import net.insi8.scoreboard.lib.model.MatchStatus
import net.insi8.scoreboard.lib.repo.MatchStatusRepository
import net.insi8.scoreboard.lib.repo.MockMatchStatusDatasource
import java.time.LocalDateTime
import java.time.ZoneId

interface ScoreBoardHandler {
    fun startMatch(homeTeam: String, awayTeam: String)
    fun finishMatch(matchId: String)
    fun getScoreBoard(): Flow<List<Match>>
    fun updateScore(matchId: String, homeTeamScore: Int, awayTeamScore: Int)
    fun leaderBoard(): Flow<List<Match>>
    fun archiveAndClearTheBoard()
}

// set a global database
private val matchStatus: MutableStateFlow<List<Match>> = MutableStateFlow(
    emptyList()
)

class ScoreBoardHandlerImpl(
    private val matchStatusRepository: MatchStatusRepository = MockMatchStatusDatasource(
        matchStatus = matchStatus
    )
) : ScoreBoardHandler {
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

    override fun archiveAndClearTheBoard() {
        if (matchStatusRepository.areMatchesInProgress()) {
            throw InvalidOperationException("You cant clear board the when there ongoing matches")
        } else {
            matchStatusRepository.archiveAndClear()
        }
    }
}