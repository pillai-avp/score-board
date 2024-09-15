package net.insi8.scoreboard.app.controller

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import net.insi8.scoreboard.app.ScoreBoard
import net.insi8.scoreboard.app.service.ScoreBoardService
import net.insi8.scoreboard.lib.extensions.generateId
import net.insi8.scoreboard.lib.handler.ScoreBoardHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ScoreBoardController(
    private val scoreBoardService: ScoreBoardService,
    private val scoreBoardHandler: ScoreBoardHandler
) {

    @GetMapping("/score-board")
    suspend fun scoreBoardStream(): Flow<ScoreBoard> {
        return combine(scoreBoardService.getScore(), scoreBoardService.getLeaderBoard()) { score, leader ->
            ScoreBoard("ScoreBoard : $score", "Leaderboard : $leader")
        }
    }

    @PostMapping("/start-matches")
    suspend fun startMatches() {
        scoreBoardService.archiveAndClearTheBoard()
        coroutineScope {
            awaitAll(async { sendEvents(scoreBoardHandler) })
        }
    }

    suspend fun sendEvents(service: ScoreBoardHandler) {
        service.startMatch("Mexico", "Canada")
        delay(1000)
        service.startMatch("Spain", "Brazil")
        delay(1000)
        service.startMatch("Germany", "France")
        delay(1000)
        service.startMatch("Uruguay", "Italy")
        delay(1000)
        service.startMatch("Argentina", "Australia")

        service.updateScore(generateId("Mexico", "Canada"), 0, 2)
        delay(1000)
        service.updateScore(generateId("Spain", "Brazil"), 7, 2)
        delay(1000)
        service.updateScore(generateId("Germany", "France"), 2, 1)
        delay(1000)
        service.updateScore(generateId("Uruguay", "Italy"), 4, 6)
        delay(1000)
        service.updateScore(generateId("Argentina", "Australia"), 3, 0)
        delay(1000)

        service.updateScore(generateId("Mexico", "Canada"), 0, 5)
        delay(1000)
        service.updateScore(generateId("Spain", "Brazil"), 10, 2)
        delay(1000)
        service.updateScore(generateId("Germany", "France"), 2, 2)
        delay(1000)
        service.updateScore(generateId("Uruguay", "Italy"), 6, 6)
        delay(1000)
        service.updateScore(generateId("Argentina", "Australia"), 3, 1)
        delay(1000)
        service.finishMatch(generateId("Mexico", "Canada"))
        delay(1000)
        service.finishMatch(generateId("Germany", "France"))
        delay(1000)
        service.finishMatch(generateId("Spain", "Brazil"))
        delay(1000)
        service.finishMatch(generateId("Argentina", "Australia"))
        delay(1000)
        service.finishMatch(generateId("Uruguay", "Italy"))
    }
}