package net.insi8.scoreboard.lib

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.insi8.scoreboard.lib.errors.InvalidOperationException
import net.insi8.scoreboard.lib.extensions.generateId
import net.insi8.scoreboard.lib.handler.ScoreBoardHandler
import net.insi8.scoreboard.lib.handler.ScoreBoardHandlerImpl
import net.insi8.scoreboard.lib.repo.MockMatchStatusDatasource
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class MatchScoreTest {
    private val service: ScoreBoardHandler =
        ScoreBoardHandlerImpl(
            matchStatusRepository = MockMatchStatusDatasource(
                matchStatus = MutableStateFlow(
                    emptyList()
                )
            )
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun `test if the score updated is correct or not`() = runTest {
        turbineScope {
            val homeTeam = "Spain"
            val awayTeam = "Germany"
            service.startMatch(homeTeam, awayTeam)
            service.getScoreBoard().test {
                var items = awaitItem()
                assertEquals(mapOf("Spain" to 0, "Germany" to 0), items.first().score)
                service.updateScore(generateId(homeTeam, awayTeam), 1, 0)
                items = awaitItem()
                assertEquals(mapOf("Spain" to 1, "Germany" to 0), items.first().score)
                service.updateScore(generateId(homeTeam, awayTeam), 2, 1)
                items = awaitItem()
                assertEquals(mapOf("Spain" to 2, "Germany" to 1), items.first().score)
            }
        }
    }

    @Test
    fun `test if only a progressing match can update the score`() = runTest {
        val homeTeam1 = "Spain"
        val awayTeam1 = "Germany"
        service.startMatch(homeTeam1, awayTeam1)
        service.updateScore(generateId(homeTeam1, awayTeam1), 1, 0)
        service.getScoreBoard().test {
            var items = awaitItem()
            assertEquals(mapOf("Spain" to 1, "Germany" to 0), items.first().score)
            service.finishMatch(generateId(homeTeam1, awayTeam1))
            items = awaitItem()
            try {
                service.updateScore(generateId(homeTeam1, awayTeam1), 1, 1)
                assertEquals(mapOf("Spain" to 1, "Germany" to 0), items.first().score)
            } catch (e: Exception) {
                assertTrue { e is InvalidOperationException }
            }
        }
    }
}