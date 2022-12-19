
fun toPoints(input: String): List<Point> {
    return input.split(" -> ").map { it.split(",") }.map { split -> Point(split[1].toInt(), split[0].toInt()) }
}

fun range(start: Int, end: Int): IntProgression = if (start <= end) start..end else start downTo end

fun getBlocked(input: List<String>): Set<Point>  {
    val blocked = mutableSetOf<Point>()
    input.forEach { line ->
        val points = toPoints(line)
        var from = points[0]
        blocked.add(from)
        for (i in 1 until points.size) {
            val current = points[i]
            val blockedPoints = if (from.x == current.x)
                range(from.y, current.y).map { Point(from.x, it) }
            else
                range(from.x, current.x).map { Point(it, from.y) }
            blockedPoints.forEach { p ->
                blocked.add(p)
            }
            from = current
        }
    }
    return blocked
}

fun printState(blocked: Set<Point>, sand: Set<Point>) {
    for (x in 0..blocked.maxBy { it.x }.x + 1) {
        for (y in blocked.minBy { it.y }.y - 1..blocked.maxBy { it.y }.y + 1) {
            val element = Point(x, y)
            print(
                when {
                    blocked.contains(element) -> '#'
                    sand.contains(element) -> 'o'
                    element.x == 0 && element.y == 500 -> '+'
                    else -> '.'
                }
            )
        }
        println()
    }
    println()

}
fun main() {

    fun part1(input: List<String>): Int {
        val blocked = getBlocked(input)

        var droppedSand = -1
        val sand = mutableSetOf<Point>()

        val lowestX = blocked.maxBy { it.x }.x

        dropSand@ while (true) {
//            printState()
            var p = Point(0, 500)
            droppedSand++
            moveSand@ while (true) {
                if (p.x == lowestX) {
                    break@dropSand
                }
                val finalPosition = listOf(Point(p.x + 1, p.y), Point(p.x + 1, p.y - 1), Point(p.x + 1, p.y + 1))
                    .find { !blocked.contains(it) && !sand.contains(it)} ?: break@moveSand
                p = finalPosition
            }
            sand.add(p)
        }

        printState(blocked, sand)

        return droppedSand
    }

    fun part2(input: List<String>): Int {
        val blocked = getBlocked(input)

        var droppedSand = 0
        val sand = mutableSetOf<Point>()

        val lowestX = blocked.maxBy { it.x }.x + 1

        dropSand@ while (true) {
//            printState()
            var p = Point(0, 500)
            droppedSand++
            moveSand@ while (true) {
                if (p.x == lowestX) break@moveSand
                val finalPosition = listOf(Point(p.x + 1, p.y), Point(p.x + 1, p.y - 1), Point(p.x + 1, p.y + 1))
                    .find { !blocked.contains(it) && !sand.contains(it)}
                if (finalPosition == null) {
                    if (p.x == 0 && p.y == 500) {
                        break@dropSand
                    } else {
                        break@moveSand
                    }
                }
                p = finalPosition

            }
            sand.add(p)
        }

        printState(blocked, sand)

        return droppedSand
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    check(part1(testInput) == 24)
//    check(part2(testInput) == 2713358)
//your answer is too low
    val input = readInput("Day14")
    check(part1(input) == 674)
    println(part1(input))

    check(part2(input) == 24957)
    println(part2(input))
}
