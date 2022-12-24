import kotlin.math.max

data class LongPoint(var x: Long, var y: Long) {

    fun eq(x: Long, y: Long): Boolean = this.x == x && this.y == y

    fun move(direction: Char, distance: Long = 1L) {
        when (direction) {
            'R', '>' -> y += distance
            'L', '<' -> y -= distance
            'U', '^' -> x += distance
            else -> x -= distance
        }
    }

    fun highest(other: LongPoint): LongPoint = if (x > other.x) this else other
}

private typealias Rock = Set<LongPoint>

private class Wind(private val input: String) : Iterator<Char> {
    var index = 0L
    val size: Int
        get() = input.length
    val counter: Long
        get() = index

    override fun hasNext() = true

    override fun next(): Char = input[(index++ % input.length).toInt()]


}

fun Rock.move(d: Char, distance: Long = 1) = forEach { it.move(d, distance) }
fun Rock.highestPoint() = maxBy { it.x }

fun Char.reverse() = when (this) {
    '>' -> '<'
    '<' -> '>'
    'V' -> '^'
    else -> 'V'
}

fun main() {

    fun rocks(): Sequence<Rock> = sequence {
        while (true) {
            yield(
                setOf(
                    // ====
                    LongPoint(0, 0), LongPoint(0, 1), LongPoint(0, 2), LongPoint(0, 3),
                )
            )
            yield(
                setOf(
                    LongPoint(2, 1),                                         //   =
                    LongPoint(1, 0), LongPoint(1, 1), LongPoint(1, 2),   //  ===
                    LongPoint(0, 1)                                          //   =
                )
            )
            yield(
                setOf(
                    LongPoint(2, 2),
                    LongPoint(1, 2),
                    LongPoint(0, 0), LongPoint(0, 1), LongPoint(0, 2)
                )
            )
            yield(
                setOf(
                    LongPoint(3, 0),  // =
                    LongPoint(2, 0),  // =
                    LongPoint(1, 0),  // =
                    LongPoint(0, 0),  // =
                )
            )
            yield(
                setOf(
                    LongPoint(1, 0), LongPoint(1, 1),
                    LongPoint(0, 0), LongPoint(0, 1),
                )
            )

        }
    }

    data class HistoryCacheKey(val figure: Long, val wind: Long, val points: Set<Point>)
    data class HistoryCacheValue(val figuresFell: Long, val highestPoint: Long)

    fun sign(highestPoint: Long, points: Set<LongPoint>): Set<Point> {
        val deepness = 50
        return points.asSequence().filter { highestPoint - it.x < deepness }
            .mapTo(mutableSetOf()) { Point((highestPoint - it.x).toInt(), it.y.toInt()) }
    }


    class Field(val wind: Wind) {
        private var fallenFigures = mutableSetOf<LongPoint>()
        private var highestPoint = -1L
        private val historyCache = HashMap<HistoryCacheKey, HistoryCacheValue>()

        fun Rock.valid(): Boolean = none { it.y < 0 || it.y > 6 || it.x < 0 || fallenFigures.contains(it) }

        fun simulate(rounds: Long): Long {
            val rockSource = rocks().iterator()
            var toAdd = 0L
            var rockNumber = 1L
            var addOne = true
            while (rockNumber <= rounds) {
                val rock = rockSource.next()
                rock.move('>', 2)
                rock.move('^', highestPoint + 4)
                rockfall@ while (true) {
//                    print(rock)
                    val windFlow = wind.next()
                    rock.move(windFlow)
                    if (!rock.valid()) {
                        rock.move(windFlow.reverse())
                    }
                    rock.move('V')
                    if (!rock.valid()) {
                        rock.move('^')
                        highestPoint = max(highestPoint, rock.highestPoint().x)
                        fallenFigures.addAll(rock)


                        val cacheKey = HistoryCacheKey(
                            rockNumber % 5,
                            wind.counter % wind.size,
                            sign(highestPoint, fallenFigures)
                        )
                        val history = historyCache[cacheKey]
                        if (rounds > 2022 && rockNumber > 1000 && history != null) {
                            val highestDiff = highestPoint - history.highestPoint
                            val rocksDiff = rockNumber - history.figuresFell
                            val amt = (rounds - rockNumber) / rocksDiff
                            toAdd += amt * highestDiff
                            rockNumber += amt*rocksDiff
                            addOne = false
                        } else {
                            historyCache[cacheKey] = HistoryCacheValue(rockNumber, highestPoint)
                            rockNumber++
                        }
                        break@rockfall
                    }
                }

            }
            return highestPoint + toAdd + if (addOne) 1 else 0
        }

        fun print(currentRock: Rock) {
            val highest = max(highestPoint, currentRock.highestPoint().x)
            for (x in highest downTo 0) {
                for (y in 0L..6) {
                    val p = LongPoint(x, y)
                    val ch = when {
                        currentRock.contains(p) -> '@'
                        fallenFigures.contains(p) -> '#'
                        else -> '.'
                    }
                    print(ch)
                }
                println()
            }
            println("=======")
        }
    }


    fun part1(input: List<String>): Long {
        return Field(Wind(input.first())).simulate(2022)
    }

    fun part2(input: List<String>): Long {
        return Field(Wind(input.first())).simulate(1000000000000)
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day17_test")
    println(part1(testInput))
    check(part1(testInput) == 3068L)
    val part2 = part2(testInput)
    println(part2)
    check(part2 == 1514285714288)

    val input = readInput("Day17")
    println(part1(input))
    check(part1(input) == 3184L)
    println(part2(input))
    check(part2(input) == 2967L)
}
