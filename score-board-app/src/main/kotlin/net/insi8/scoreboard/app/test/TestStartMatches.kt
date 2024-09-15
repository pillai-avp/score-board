package net.insi8.scoreboard.app.test

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import net.insi8.scoreboard.lib.extensions.generateId
import net.insi8.scoreboard.lib.handler.ScoreBoardHandler
import org.springframework.stereotype.Service

@Service
class TestStartMatches(private val scoreBoardHandler: ScoreBoardHandler) {

    suspend fun testStartMatchesAndMockScores() {
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