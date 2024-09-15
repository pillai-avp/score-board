package net.insi8.scoreboard.lib.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import net.insi8.scoreboard.lib.errors.InvalidOperationException
import net.insi8.scoreboard.lib.extensions.replace
import net.insi8.scoreboard.lib.model.Match
import net.insi8.scoreboard.lib.model.MatchStatus
import java.time.LocalDateTime
import java.time.ZoneId

internal class MockMatchStatusDatasource(
    private val matchStatus: MutableStateFlow<List<Match>> = MutableStateFlow(
        emptyList()
    )
) : MatchStatusRepository {

    private val scoreBoard: Flow<List<Match>> =
        matchStatus.map { set -> set.filter { match -> match.status is MatchStatus.Progressing } }

    private val leaderBoard =
        matchStatus.map { list -> list.filter { match -> match.status is MatchStatus.Finished } }

    override fun startMatch(match: Match) {
        val existingMatch = matchStatus.value.singleOrNull { fromStorage -> fromStorage.id == match.id }
        if (existingMatch != null) {
            throw InvalidOperationException("You cannot start a match that is already in progress.")
        }
        matchStatus.update { value ->
            value.toMutableList().plus(match)
        }
    }

    override fun getScoreBoardOnGoingMatchesStream(): Flow<List<Match>> = scoreBoard

    override fun finishMatch(matchId: String) {
        matchStatus.update { value ->
            val allMatches = value.toMutableList()
            allMatches.singleOrNull { match -> match.id == matchId }?.let { match ->
                val status = match.status
                val editedMatch = if (status is MatchStatus.Progressing) {
                    match.copy(status = MatchStatus.Finished(status.startedAt, LocalDateTime.now(ZoneId.of("UTC"))))
                } else {
                    match
                }
                allMatches.replace(match, editedMatch)
            } ?: allMatches
        }
    }

    override fun updateScore(matchId: String, homeTeamScore: Int, awayTeamScore: Int) {
        val matchToUpdate =
            matchStatus.value.singleOrNull { match -> match.id == matchId && match.status is MatchStatus.Progressing }
        matchToUpdate?.let { match ->
            matchStatus.update { value ->
                value.replace(
                    match,
                    match.copy(
                        score = mapOf(
                            match.homeTeam to homeTeamScore,
                            match.awayTeam to awayTeamScore
                        )
                    )
                )
            }
        } ?: throw InvalidOperationException("This match cannot be fount, may be its already finished.")
    }

    override fun getLeaderBoard(): Flow<List<Match>> = leaderBoard
}