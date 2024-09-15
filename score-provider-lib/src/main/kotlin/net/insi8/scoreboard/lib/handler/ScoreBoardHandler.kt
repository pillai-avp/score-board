package net.insi8.scoreboard.lib.handler

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import net.insi8.scoreboard.lib.model.Match
import net.insi8.scoreboard.lib.repo.MockMatchStatusDatasource
import net.insi8.scoreboard.lib.services.MatchStatusServices
import net.insi8.scoreboard.lib.services.MatchStatusServicesImpl

private val matchStatus: MutableStateFlow<List<Match>> = MutableStateFlow(
    emptyList()
)

class ScoreBoardHandler {
    private val services: MatchStatusServices =
        MatchStatusServicesImpl(matchStatusRepository = MockMatchStatusDatasource(matchStatus = matchStatus))

    fun startMatch(homeTeam: String, awayTeam: String) {
        services.startMatch(homeTeam, awayTeam)
    }

    fun finishMatch(matchId: String) {
        services.finishMatch(matchId = matchId)
    }

    fun getScoreBoard(): Flow<List<Match>> {
        return services.getScoreBoard()
    }

    fun updateScore(matchId: String, homeTeamScore: Int, awayTeamScore: Int) {
        services.updateScore(matchId, homeTeamScore, awayTeamScore)
    }

    fun leaderBoard(): Flow<List<Match>> {
        return services.leaderBoard()
    }

    fun archiveAndClearTheBoard() {
        return services.archiveAndClearTheBoard()
    }
}