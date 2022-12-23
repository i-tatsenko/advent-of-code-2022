import kotlin.math.abs

data class Point(val x: Int, val y: Int) {

    fun eq(x: Int, y: Int): Boolean = this.x == x && this.y == y

    fun move(direction: String): Point {
        return when (direction) {
            "R" -> Point(x, y + 1)
            "L" -> Point(x, y - 1)
            "U" -> Point(x + 1, y)
            else -> Point(x - 1, y)
        }
    }

//    fun keepUp(to: Point): Point {
//        if (abs(x - to.x) < 2 && abs(y - to.y) < 2) {
//            return this
//        }
//        val newX = if (abs(x - to.x) > 1) if (x < to.x) x + 1 else x - 1 else x
//        val newY = if (abs(y - to.y) > 1) if (y < to.y) y + 1 else y - 1 else y
//        return Point(newX, newY)
//    }

    fun keepUp(to: Point): Point {
        val xDiff = x - to.x
        val yDiff = y - to.y
        if (abs(xDiff) < 2 && abs(yDiff) < 2) {
            return this
        }
        if (abs(xDiff) == 2 && abs(yDiff) == 2) {
            val newX = if (to.x > x) x + 1 else x - 1
            val newY = if (to.y > y) y + 1 else y - 1
            return Point(newX, newY)
        }
        if (x == to.x || abs(yDiff) > abs(xDiff)) {
            return Point(to.x, if (y < to.y) to.y - 1 else to.y + 1)
        }
        return Point(if (x < to.x) to.x - 1 else to.x + 1, to.y)
    }

    operator fun plus(other: Point): Point = Point(x + other.x, y + other.y)
}

val MutableList<Point>.tail: Point
    get() = this.last()

var MutableList<Point>.head: Point
    get() = this.first()
    set(p) {
        this[0] = p
    }
fun MutableList<Point>.head() = this.first()
fun main() {
    fun part1(input: List<String>, knotsCount: Int, boardRows: Int = 5, bordColumns: Int = 6, doPrintBoard: Boolean = false): Int {
        val tailVisited = mutableSetOf<Point>()
        val rope = mutableListOf<Point>()
        for (index in 1..knotsCount) {
            rope.add(Point(0, 0))
        }
        tailVisited.add(rope.tail)

        val printBoard = {
            for (x in boardRows - 1 downTo 0) {
                println()
                for (y in 0 until bordColumns) {
                    val point = Point(x, y)
                    val indexOf = rope.indexOf(point)

                    if (rope.head() == point) {
                        print('H')
                    } else if(indexOf != -1) {
                        print(indexOf)
                    } else if (rope.tail == point) {
                        print('T')
                    }  else if (tailVisited.contains(point)) {
                        print('#')
                    } else {
                        print(".")
                    }
                }
            }
            println()
            println()

        }


        input.forEach {
            val split = it.split(" ")
            val direction = split[0]
            val distance = split[1].toInt()

            for (move in 1..distance) {
                rope.head = rope.head.move(direction)
                for (knot in 1 until knotsCount) {
                    rope[knot] = rope[knot].keepUp(rope[knot - 1])
                }
                tailVisited.add(rope.tail)
                if (doPrintBoard) printBoard()
            }
        }
        return tailVisited.size
    }

    fun part2(input: List<String>): Int {
        return part1(input, 10, 21, 26, true)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    val testInput21 = readInput("Day09_test_2")
    check(part1(testInput, 2) == 13)
//    check(part2(testInput2) == 9)
    check(part2(testInput21) == 36)

    val input = readInput("Day09")
    println(part1(input, 2))
    check(part1(input, 2) == 6190)
    println(part2(input))
}
