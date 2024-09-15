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
import net.insi8.scoreboard.lib.model.MatchStatus
import net.insi8.scoreboard.lib.repo.MockMatchStatusDatasource
import net.insi8.scoreboard.lib.services.MatchStatusServices
import net.insi8.scoreboard.lib.services.MatchStatusServicesImpl
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class MatchStartFinishTest {
    private val service: MatchStatusServices =
        MatchStatusServicesImpl(
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
    fun `test if the started matches are visible in the score board`() = runTest {
        turbineScope {
            service.startMatch("Spain", "Germany")
            service.startMatch("Germany", "France")
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
            service.startMatch("Spain", "Germany")
            try {
                service.getScoreBoard().test {
                    val items = awaitItem()
                    assertEquals(1, items.size)
                }
                service.startMatch("Spain", "Germany")
            } catch (e: Exception) {
                assertTrue { e is InvalidOperationException }
            }
        }
    }


    @Test
    fun `test if the finished matches are removed from the score board`() = runTest {
        turbineScope {
            service.startMatch("Spain", "Germany")
            service.getScoreBoard().test {
                var items = awaitItem()
                assertEquals(1, items.size)
                service.finishMatch(generateId("Spain", "Germany"))
                items = awaitItem()
                assertEquals(0, items.size)
            }
        }
    }

    @Test
    fun `test if the finished matches are appearing on leader board`() = runTest {
        turbineScope {
            service.startMatch("Spain", "Germany")
            service.startMatch("Brazil", "Argentina")
            service.startMatch("Japan", "India")
            service.getScoreBoard().test {
                var items = awaitItem()
                val matchToFinish = items[0]
                assertEquals(3, items.size)
                service.finishMatch(matchToFinish.id)
                items = awaitItem()
                assertEquals(2, items.size)
                service.leaderBoard().test {
                    items = awaitItem()
                    assertEquals(1, items.size)
                    assertEquals(items[0].id, matchToFinish.id)
                }
            }
        }
    }

    @Test
    fun `test the leader board order`() = runTest {
        turbineScope {
            service.startMatch("Mexico", "Canada")
            service.startMatch("Spain", "Brazil")
            service.startMatch("Germany", "France")
            service.startMatch("Uruguay", "Italy")
            service.startMatch("Argentina", "Australia")

            service.updateScore(generateId("Mexico", "Canada"), 0, 5)
            service.updateScore(generateId("Spain", "Brazil"), 10, 2)
            service.updateScore(generateId("Germany", "France"), 2, 2)
            service.updateScore(generateId("Uruguay", "Italy"), 6, 6)
            service.updateScore(generateId("Argentina", "Australia"), 3, 1)

            service.finishMatch(generateId("Mexico", "Canada"))
            service.finishMatch(generateId("Germany", "France"))
            service.finishMatch(generateId("Spain", "Brazil"))
            service.finishMatch(generateId("Argentina", "Australia"))
            service.finishMatch(generateId("Uruguay", "Italy"))

            val expectedResult: List<String> = listOf(
                generateId("Uruguay", "Italy"),
                generateId("Spain", "Brazil"),
                generateId("Mexico", "Canada"),
                generateId("Argentina", "Australia"),
                generateId("Germany", "France")
            )
            service.leaderBoard().test {
                val items = awaitItem()
                assertEquals(5, items.size)
                assertEquals(expectedResult, items.map { it.id })
            }
        }
    }
}