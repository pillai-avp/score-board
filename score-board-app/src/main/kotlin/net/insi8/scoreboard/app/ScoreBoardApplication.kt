package net.insi8.scoreboard.app

import kotlinx.coroutines.*
import net.insi8.scoreboard.lib.extensions.generateId
import net.insi8.scoreboard.lib.handler.ScoreBoardHandler
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ScoreBoardApplication

private val scoreBoardHandler: ScoreBoardHandler = ScoreBoardHandler()

fun main(args: Array<String>) {
    runApplication<ScoreBoardApplication>(*args)
    runBlocking {
        delay(5000)
        coroutineScope {
            awaitAll(async { sendEvents(scoreBoardHandler) })
        }
    }
}

suspend fun sendEvents(service: ScoreBoardHandler) {
    service.startMatch("Mexico", "Canada")
    delay(5000)
    service.startMatch("Spain", "Brazil")
    delay(5000)
    service.startMatch("Germany", "France")
    delay(5000)
    service.startMatch("Uruguay", "Italy")
    delay(5000)
    service.startMatch("Argentina", "Australia")

    service.updateScore(generateId("Mexico", "Canada"), 0, 5)
    delay(5000)
    service.updateScore(generateId("Spain", "Brazil"), 10, 2)
    delay(5000)
    service.updateScore(generateId("Germany", "France"), 2, 2)
    delay(5000)
    service.updateScore(generateId("Uruguay", "Italy"), 6, 6)
    delay(5000)
    service.updateScore(generateId("Argentina", "Australia"), 3, 1)
    delay(5000)
    service.finishMatch(generateId("Mexico", "Canada"))
    delay(5000)
    service.finishMatch(generateId("Germany", "France"))
    delay(5000)
    service.finishMatch(generateId("Spain", "Brazil"))
    delay(5000)
    service.finishMatch(generateId("Argentina", "Australia"))
    delay(5000)
    service.finishMatch(generateId("Uruguay", "Italy"))
}
