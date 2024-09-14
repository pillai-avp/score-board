package net.insi8.scoreboard.lib.extensions

import java.time.LocalDateTime
import java.time.ZoneId

fun LocalDateTime.utcNow(): LocalDateTime = LocalDateTime.now(ZoneId.of("UTC"))
fun generateId(homeTeam: String, awayTeam: String) = "${homeTeam}vs$awayTeam"