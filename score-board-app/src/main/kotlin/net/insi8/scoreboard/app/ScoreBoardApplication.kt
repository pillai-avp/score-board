package net.insi8.scoreboard.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ScoreBoardApplication

fun main(args: Array<String>) {
    runApplication<ScoreBoardApplication>(*args)
}
