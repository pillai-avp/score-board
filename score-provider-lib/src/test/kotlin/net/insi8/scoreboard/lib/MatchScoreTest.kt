package net.insi8.scoreboard.lib

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.insi8.scoreboard.lib.extensions.generateId
import net.insi8.scoreboard.lib.repo.MatchStatusRepository
import net.insi8.scoreboard.lib.repo.MockMatchStatusDatasource
import net.insi8.scoreboard.lib.services.MatchStatusServices
import net.insi8.scoreboard.lib.services.MatchStatusServicesImpl
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class MatchScoreTest {
    private val repo: MatchStatusRepository = MockMatchStatusDatasource()
    private val service: MatchStatusServices = MatchStatusServicesImpl(matchStatusRepository = repo)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun `test if the score updated is correct or not`() = runTest {
        turbineScope {
            val homeTeam = "a"
            val awayTeam = "b"
            service.startMatch(homeTeam, awayTeam)
            service.getScoreBoard().test {
                var items = awaitItem()
                assertEquals(mapOf("a" to 0, "b" to 0), items.first().score)
                service.updateScore(generateId(homeTeam, awayTeam), 1, 0)
                items = awaitItem()
                assertEquals(mapOf("a" to 1, "b" to 0), items.first().score)
                service.updateScore(generateId(homeTeam, awayTeam), 2, 1)
                items = awaitItem()
                assertEquals(mapOf("a" to 2, "b" to 1), items.first().score)
            }
        }
    }

    @Test
    fun `test if only a progressing match can update the score`() = runTest {
        val homeTeam1 = "a"
        val awayTeam1 = "b"
        service.startMatch(homeTeam1, awayTeam1)
        service.updateScore(generateId(homeTeam1, awayTeam1), 1, 0)
        service.getScoreBoard().test {
            val items = awaitItem()
            assertEquals(mapOf("a" to 1, "b" to 0), items.first().score)
            service.finishMatches(homeTeam1, awayTeam1)
            try {
                service.updateScore(generateId(homeTeam1, awayTeam1), 1, 1)
            } catch (e: Exception) {
                assertTrue { e is NoSuchMethodException }
            }
        }
    }
}