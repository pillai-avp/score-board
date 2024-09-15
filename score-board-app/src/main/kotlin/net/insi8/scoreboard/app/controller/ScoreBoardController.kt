package net.insi8.scoreboard.app.controller

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import net.insi8.scoreboard.app.ScoreBoard
import net.insi8.scoreboard.app.service.ScoreBoardService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ScoreBoardController(private val scoreBoardService: ScoreBoardService) {
    @OptIn(FlowPreview::class)
    @GetMapping("/score-board", produces = ["text/event-stream"])
    suspend fun scoreBoardStream(): Flow<ScoreBoard> {
        return combine(scoreBoardService.getScore(), scoreBoardService.getLeaderBoard()) { score, leader ->
            ScoreBoard("ScoreBoard : $score", "Leaderboard : $leader")
        }
    }
}