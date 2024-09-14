package net.insi8.scoreboard.lib.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import net.insi8.scoreboard.lib.model.Match
import net.insi8.scoreboard.lib.model.MatchStatus
import java.io.InvalidObjectException
import java.time.LocalDateTime
import java.time.ZoneId


class MockMatchStatusDatasource : MatchStatusRepository {
    private val _matchStatus: MutableStateFlow<Set<Match>> = MutableStateFlow(emptySet())
    private val scoreBoard: Flow<List<Match>> =
        _matchStatus.map { set -> set.filter { match -> match.status is MatchStatus.Progressing } }

    override fun startMatch(match: Match) {
        val existingMatch = _matchStatus.value.singleOrNull { fromStorage -> fromStorage.id == match.id }
        if (existingMatch != null) {
            throw InvalidObjectException("The match already exists")
        }
        _matchStatus.update { value ->
            value.toMutableSet().plus(
                match
            )
        }
    }

    override fun getScoreBoardOnGoingMatchesStream(): Flow<List<Match>> = scoreBoard

    override fun finishMatch(matchId: String) {
        _matchStatus.update { value ->
            val mutableSet = value.toMutableSet()
            mutableSet.singleOrNull { match ->
                match.id == matchId
            }?.let { match ->
                val status = match.status
                mutableSet.minus(match).plus(
                    if (status is MatchStatus.Progressing) {
                        match.copy(status = MatchStatus.Finished(status.startedAt, LocalDateTime.now(ZoneId.of("UTC"))))
                    } else {
                        match
                    }
                )
            } ?: mutableSet
        }
    }

    override fun updateScore(matchId: String, homeTeamScore: Int, awayTeamScore: Int) {
        val matchToUpdate =
            _matchStatus.value.singleOrNull { match -> match.id == matchId && match.status is MatchStatus.Progressing }
        matchToUpdate?.let { match ->
            _matchStatus.update { value ->
                val mutableSet = value.toMutableSet()
                mutableSet.minus(match).plus(
                    match.copy(
                        score = mutableMapOf(
                            match.homeTeam to homeTeamScore,
                            match.awayTeam to awayTeamScore
                        )
                    )
                )
            }
        } ?: throw NoSuchMethodException("updateScore")

    }
}