package net.insi8.scoreboard.app

import net.insi8.scoreboard.lib.handler.ScoreBoardHandler
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ScoreBoardApplication

private val scoreBoardHandler: ScoreBoardHandler = ScoreBoardHandler()

fun main(args: Array<String>) {
    runApplication<ScoreBoardApplication>(*args)
}
