package net.insi8.scoreboard.app.controller

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import net.insi8.scoreboard.app.ScoreBoard
import net.insi8.scoreboard.app.service.ScoreBoardService
import net.insi8.scoreboard.app.test.TestStartMatches
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ScoreBoardController(
    private val scoreBoardService: ScoreBoardService,
    private val testStartMatches: TestStartMatches
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
        testStartMatches.testStartMatchesAndMockScores()
    }


}