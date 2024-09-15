package net.insi8.scoreboard.app.config

import net.insi8.scoreboard.lib.handler.ScoreBoardHandler
import net.insi8.scoreboard.lib.handler.ScoreBoardHandlerImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ScoreBoardConfig {
    @Bean
    fun scoreBoardHandler(): ScoreBoardHandler = ScoreBoardHandlerImpl()
}