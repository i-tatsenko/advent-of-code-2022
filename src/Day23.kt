import Direction23.*

private enum class Direction23 {
    NORTH {
        override fun safeZone(p: Point): List<Point> = safeRange(p.y) { y -> Point(p.x - 1, y) }

        override fun move(p: Point): Point = Point(p.x - 1, p.y)
    },
    EAST {
        override fun safeZone(p: Point): List<Point> = safeRange(p.x) { x -> Point(x, p.y + 1) }

        override fun move(p: Point): Point = Point(p.x, p.y + 1)
    },
    WEST {
        override fun safeZone(p: Point): List<Point> = safeRange(p.x) { x -> Point(x, p.y - 1) }

        override fun move(p: Point): Point = Point(p.x, p.y - 1)
    },
    SOUTH {
        override fun safeZone(p: Point): List<Point> = safeRange(p.y) { y -> Point(p.x + 1, y) }

        override fun move(p: Point): Point = Point(p.x + 1, p.y)
    };

    abstract fun safeZone(p: Point): List<Point>
    abstract fun move(p: Point): Point
    fun safeRange(coord: Int, toPoint: (Int) -> Point): List<Point> = (coord - 1..coord + 1).map(toPoint)
}

private fun Point.shouldMove(others: Set<Point>): Boolean =
    Direction23.values().flatMap { it.safeZone(this) }.any { others.contains(it) }


fun main() {


    fun parseElves(input: List<String>): MutableSet<Point> {
        val elves = mutableSetOf<Point>()
        input.forEachIndexed { row, line ->
            line.forEachIndexed { column, ch -> if (ch == '#') elves.add(Point(row, column)) }
        }
        return elves
    }

    fun MutableList<Direction23>.rotate() {
        this.add(this.removeFirst())
    }

    fun part1(input: List<String>): Int {
        val directions = mutableListOf(NORTH, SOUTH, WEST, EAST)
        val elves = parseElves(input)
        val proposedMoves = mutableMapOf<Point, MutableList<Point>>() //where by who
        for (i in 1..10) {
            proposedMoves.clear()
            elves.filter { it.shouldMove(elves) }.forEach { elf ->
                directions.firstOrNull { it.safeZone(elf).none { safe -> elves.contains(safe) } }
                    ?.also { direction ->
                        proposedMoves.compute(direction.move(elf)) { _, presentList ->
                            if (presentList == null) mutableListOf(elf) else {
                                presentList.add(elf)
                                presentList
                            }
                        }
                    }
            }
            proposedMoves.forEach {
                if (it.value.size == 1) {
                    elves.remove(it.value.first())
                    elves.add(it.key)
                }
            }
            directions.rotate()



        }
        val minX = elves.minOf { it.x }
        val minY = elves.minOf { it.y }
        val maxX = elves.maxOf { it.x }
        val maxY = elves.maxOf { it.y }
        return (maxX - minX + 1) * (maxY - minY + 1) - elves.size
    }

    fun part2(input: List<String>): Int {
        val directions = mutableListOf(NORTH, SOUTH, WEST, EAST)
        val elves = parseElves(input)
        val proposedMoves = mutableMapOf<Point, MutableList<Point>>() //where by who
        for (i in 1..Int.MAX_VALUE) {
            proposedMoves.clear()
            elves.filter { it.shouldMove(elves) }.forEach { elf ->
                directions.firstOrNull { it.safeZone(elf).none { safe -> elves.contains(safe) } }
                    ?.also { direction ->
                        proposedMoves.compute(direction.move(elf)) { _, presentList ->
                            if (presentList == null) mutableListOf(elf) else {
                                presentList.add(elf)
                                presentList
                            }
                        }
                    }
            }
            var moved = false
            proposedMoves.forEach {
                if (it.value.size == 1) {
                    elves.remove(it.value.first())
                    elves.add(it.key)
                    moved = true
                }
            }
            if (!moved) return i
            directions.rotate()

        }
        return -1
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day23_test")
    println(part1(testInput))
    check(part1(testInput) == 110)
    println(part2(testInput) )
    check(part2(testInput) == 20)

    val input = readInput("Day23")
    println(part1(input))
    check(part1(input) == 4075)
    println(part2(input))
}
