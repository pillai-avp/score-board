package net.insi8.scoreboard.lib.extensions

fun <T> List<T>.replace(old: T, newItem: T): List<T> {
    val index = indexOf(old)
    return this.toMutableList().apply {
        removeAt(index)
        add(index, newItem)
    }
}