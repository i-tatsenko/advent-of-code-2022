typealias Board = List<String>

fun Point.toUp(): Point = Point(this.x - 1, y)
fun Point.toDown(): Point = Point(this.x + 1, y)
fun Point.toRight(): Point = Point(this.x, y + 1)
fun Point.toLeft(): Point = Point(this.x, y - 1)
fun Board.get(p: Point): Char = this[p.x][p.y]

fun Board.height(p: Point): Int {
    return when (get(p)) {
        'S' -> 'a'
        'E' -> 'z'
        else -> get(p)
    }.code
}

fun Board.canMoveUp(from: Point, to: Point): Boolean {
    val (x, y) = to
    if (x < 0 || x == rows || y < 0 || y == columns || this[from.x][from.y] == 'E') {
        return false
    }
    val currentHeight = height(from)
    val targetHeight = height(to)
    return targetHeight - currentHeight < 2
}

fun Board.canMoveDown(from: Point, to: Point): Boolean {
    val (x, y) = to
    if (x < 0 || x == this.size || y < 0 || y == this[0].length || this[from.x][from.y] == 'a' || this[from.x][from.y] == 'S') {
        return false
    }
    return height(from) - height(to) < 2
}

fun Array<Array<Int>>.get(p: Point): Int = this[p.x][p.y]
fun Array<Array<Int>>.set(p: Point, value: Int): Point {
    this[p.x][p.y] = value
    return p
}

fun Array<Array<Int>>.printState() {
    println(
        this.joinToString(
            "\n",
            "=========\n"
        ) { it.joinToString { num -> if (num < 10) "0$num" else if (num < 100) "$num" else "HI" } })
}

val Board.start: Point
    get() = Point(this.indexOfFirst { it.startsWith("S") }, 0)

val Board.end: Point
    get() {
        val endRow = indexOfFirst { it.contains('E') }
        return Point(endRow, this[endRow].indexOf('E'))
    }

val Board.rows: Int
    get() = size
val Board.columns: Int
    get() = this[0].length

fun Board.buildSteps(init: (Point) -> Int): Array<Array<Int>> =
    Array(rows) { row -> Array(columns) { column -> init(Point(row, column)) } }

fun Board.allOf(ch: Char): List<Point> {
    return this.flatMapIndexed { row, line ->
        line.mapIndexedNotNull { column, char ->
            if (char == ch) Point(
                row,
                column
            ) else null
        }
    }
}

fun main() {

    fun part1(input: Board): Int {
        val steps = input.buildSteps { if (input.start == it) 0 else Int.MAX_VALUE }
        val pathQueue = mutableListOf(input.start)
        while (pathQueue.isNotEmpty()) {
            val from = pathQueue.removeLast()
            listOf(from.toLeft(), from.toRight(), from.toDown(), from.toUp())
                .filter { input.canMoveUp(from, it) }
                .forEach { target ->
                    val targetSteps = steps.get(from) + 1
                    if (steps.get(target) > targetSteps) {
                        pathQueue.add(steps.set(target, targetSteps))
                    }
                }
        }
        return steps.get(input.end)
    }

    fun part2(input: List<String>): Int {
        val steps = input.buildSteps { if (input.end == it) 0 else Int.MAX_VALUE }
        val pathQueue = mutableListOf(input.end)
        while (pathQueue.isNotEmpty()) {
            val from = pathQueue.removeLast()
            listOf(from.toLeft(), from.toRight(), from.toDown(), from.toUp())
                .filter { input.canMoveDown(from, it) }
                .forEach { target ->
                    val targetSteps = steps.get(from) + 1
                    if (steps.get(target) > targetSteps) {
                        pathQueue.add(steps.set(target, targetSteps))
                    }
                }
        }

        return input.allOf('a').map { steps.get(it) }.min()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    check(part1(testInput) == 31)
    check(part2(testInput) == 29)

    val input = readInput("Day12")
    check(part1(input) == 534)
    println(part1(input))
    println(part2(input))
}
