import kotlin.math.max

data class LongPoint(val x: Long, val y: Long) {

    fun eq(x: Long, y: Long): Boolean = this.x == x && this.y == y

    fun move(direction: String, distance: Long = 1L): LongPoint {
        return when (direction) {
            "R", ">" -> LongPoint(x, y + distance)
            "L", "<" -> LongPoint(x, y - distance)
            "U", "^" -> LongPoint(x + distance, y)
            else -> LongPoint(x - distance, y)
        }
    }
}

private typealias Rock = Set<LongPoint>

fun Rock.move(d: String, distance: Long = 1): Rock = mapTo(mutableSetOf()) { it.move(d, distance) }
fun Rock.highestPoint() = maxOf { it.x }


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
                    LongPoint(2,2),
                    LongPoint(1,2),
                    LongPoint(0, 0), LongPoint(0, 1), LongPoint(0,2)
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
            yield(setOf(
                LongPoint(1, 0), LongPoint(1, 1),
                LongPoint(0, 0), LongPoint(0, 1),
            ))

        }
    }


    class Field(val wind: Iterator<String>) {
        private var fallenFigures = mutableSetOf<LongPoint>()
        private var highestPoint = -1L

        fun Rock.valid(): Boolean = none {it.y < 0 || it.y > 6 || it.x < 0 || fallenFigures.contains(it)}

        fun simulate(rounds: Long): Long {
            val rockSource = rocks().iterator()
            for (round in 1..rounds) {
                var rock = rockSource.next()
                rock = rock.move(">", 2)
                rock = rock.move("^", highestPoint + 4)
                rockfall@while(true) {
//                    print(rock)
                    val windFlow = wind.next()
                    val afterWind = rock.move(windFlow)
                    if (afterWind.valid()) {
                        rock = afterWind
                    }
                    val afterFall = rock.move("V")
                    if (afterFall.valid()) {
                        rock = afterFall
                    } else {
                        highestPoint = max(highestPoint, rock.highestPoint())
                        fallenFigures.addAll(rock)
                        break@rockfall
                    }
                }

            }
            return highestPoint + 1
        }

        fun print(currentRock: Rock) {
            for (x in max(highestPoint, currentRock.highestPoint()) downTo 0) {
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
        val wind = sequence {
            while (true) {
                for (ch in input[0]) {
                    yield(ch.toString())
                }
            }
        }
        return Field(wind.iterator()).simulate(2022)
    }

    fun part2(input: List<String>): Long {
        val wind = sequence {
            while (true) {
                for (ch in input[0]) {
                    yield(ch.toString())
                }
            }
        }
        return Field(wind.iterator()).simulate(1000000000000)
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
