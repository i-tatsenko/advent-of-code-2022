import kotlin.collections.HashSet

fun main() {
    class BoundedDirection(private val x: Int, private val y: Int, private var d: Direction) {

        fun move(p: Point): Point {
            val moved = d.move(p)
            if (moved.x > x) return Point(0, p.y)
            if (moved.x < 0) return Point(x, p.y)
            if (moved.y > y) return Point(p.x, 0)
            if (moved.y < 0) return Point(p.x, y)
            return moved
        }

        override fun toString(): String {
            return d.d.toString()
        }
    }

    fun generateMoves(p: Point): Sequence<Point> = sequence {
        yield(p.move("V"))
        yield(p.move(">"))
        yield(p.move("^"))
        yield(p.move("<"))
        yield(p)
    }


    data class Blizzard(var p: Point, val d: BoundedDirection) {
        fun move(): Blizzard = Blizzard(d.move(p), d)

    }

    fun parseBlizzards(input: List<String>): Pair<List<Blizzard>, Point> {
        val lastX = input.size - 3
        val lastY = input[0].length - 3
        val blizzards = mutableListOf<Blizzard>()
        for (row in 1 until input.size - 1) {
            input[row].forEachIndexed { column, ch ->
                if (ch != '.' && ch != '#') blizzards.add(
                    Blizzard(
                        Point(row - 1, column - 1),
                        BoundedDirection(lastX, lastY, Direction(ch))
                    )
                )
            }
        }
        return blizzards to Point(lastX, lastY)
    }

    fun movesToExit(start: Point, exit: Point, blizzardsInitial: List<Blizzard>, lastX: Int, lastY: Int): Pair<Int, List<Blizzard>> {
        var blizzards = blizzardsInitial
        var locations = mutableSetOf(start)
        var moves = 0
        while (locations.isNotEmpty()) {
            blizzards = blizzards.map { it.move() }
            locations = locations.flatMap {
                if (it == exit) return moves + 1 to blizzards else generateMoves(it)
            }
                .filterTo(HashSet()) { p -> p.inBounds(lastX, lastY) && blizzards.none { it.p == p } }
            moves++
            blizzards.forEach { locations.remove(it.p) }
            if (locations.isEmpty()) locations.add(start)
        }
        return -1 to blizzards
    }


    fun part1(input: List<String>): Int {
        val (blizzards, exit) = parseBlizzards(input)
        return movesToExit(Point(-1, 0), exit, blizzards, exit.x, exit.y).first
    }

    fun part2(input: List<String>): Int {
        val (blizzards, exit) = parseBlizzards(input)
        val (movesToExit, cameToExitBlizzards) = movesToExit(Point(-1, 0), exit, blizzards, exit.x, exit.y)
        val (movesToStart, returnedToStartBlizzards) = movesToExit(
            Point(exit.x + 1, exit.y),
            Point(0, 0),
            cameToExitBlizzards,
            exit.x, exit.y
        )
        val (movesToExitAgain, _) = movesToExit(Point(-1, 0), exit, returnedToStartBlizzards, exit.x, exit.y)
        return movesToExit + movesToStart + movesToExitAgain
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day24_test")
    println(part1(testInput))
    check(part1(testInput) == 18)
    check(part2(testInput) == 54)

    val input = readInput("Day24")
    println(part1(input))
    check(part1(input) == 373)
    println(part2(input))
}
