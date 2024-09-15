package net.insi8.scoreboard.app.service

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import net.insi8.scoreboard.lib.handler.ScoreBoardHandler
import org.springframework.stereotype.Service

@Service
class ScoreBoardService(val scoreBoardHandler: ScoreBoardHandler) {
    fun getScore() = scoreBoardHandler.getScoreBoard().map { list -> list.map { match -> match.score }.toString() }

    @OptIn(FlowPreview::class)
    fun getLeaderBoard() =
        scoreBoardHandler.leaderBoard().map { list -> list.map { match -> match.score }.toString() }.debounce(500)

    fun archiveAndClearTheBoard() {
        scoreBoardHandler.archiveAndClearTheBoard()
    }
}