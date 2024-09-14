package net.insi8.scoreboard.lib

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.insi8.scoreboard.lib.model.MatchStatus
import net.insi8.scoreboard.lib.repo.MatchStatusRepository
import net.insi8.scoreboard.lib.repo.MockMatchStatusDatasource
import net.insi8.scoreboard.lib.services.MatchStatusServices
import net.insi8.scoreboard.lib.services.MatchStatusServicesImpl
import org.junit.Before
import org.junit.Test
import java.io.InvalidObjectException
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class MatchStartFinishTest {
    private val repo: MatchStatusRepository = MockMatchStatusDatasource()
    private val service: MatchStatusServices = MatchStatusServicesImpl(matchStatusRepository = repo)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun `test if the started matches are visible in the score board`() = runTest {
        turbineScope {
            service.startMatch("a", "b")
            service.startMatch("ab", "ab")
            service.getScoreBoard().test {
                val items = awaitItem()
                assertEquals(2, items.size)
                val booleanList = items.map { it.status is MatchStatus.Progressing }
                assertTrue { booleanList.all { it } }
            }
        }
    }

    @Test
    fun `test should not be allowed to add an existing match to the list`() = runTest {
        turbineScope {
            service.startMatch("a", "b")
            try {
                service.getScoreBoard().test {
                    val items = awaitItem()
                    assertEquals(1, items.size)
                }
                service.startMatch("a", "a")
            } catch (e: Exception) {
                assertTrue { e is InvalidObjectException }
            }
        }
    }


    @Test
    fun `test if the finished matches are removed from the score board`() = runTest {
        turbineScope {
            service.startMatch("a", "b")
            service.getScoreBoard().test {
                var items = awaitItem()
                assertEquals(1, items.size)
                service.finishMatches("a", "b")
                items = awaitItem()
                assertEquals(0, items.size)
            }
        }
    }
}