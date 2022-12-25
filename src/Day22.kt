import Bound.*
import java.lang.IllegalArgumentException

private typealias Field = Array<Array<Char>>

private val Field.width: Int
    get() = this[0].size

private val visited: MutableMap<Point, Direction> = mutableMapOf()
private lateinit var lastVisited: Point
private fun addLastVisited(p: Point, d: Direction) {
    lastVisited = p
    visited[p] = d
}


private enum class Bound {
    LEFT, RIGHT, TOP, BOTTOM
}

private data class Connection(val side: Side, val bound: Bound)


private class Side(val field: Field, val drift: Point) {
    val connections: MutableMap<Bound, Connection> = mutableMapOf()

    fun flip(v: Int): Int = field.size - 1 - v

    fun translate(p: Point, from: Bound, to: Bound): Pair<Point, Direction> {
        val point = when {
            from == RIGHT && to == LEFT -> Point(p.x, 0)
            from == RIGHT && to == RIGHT -> Point(flip(p.x), field.width - 1)
            from == RIGHT && to == BOTTOM -> Point(field.size - 1, p.x)

            from == LEFT && to == RIGHT -> Point(p.x, field.size - 1)
            from == LEFT && to == LEFT -> Point(flip(p.x),0)
            from == LEFT && to == TOP -> Point(0, p.x)

            from == BOTTOM && to == TOP -> Point(0, p.y)
            from == BOTTOM && to == RIGHT -> Point(p.y, field.width -1)

            from == TOP && to == BOTTOM -> Point(field.size - 1, p.y)
            from == TOP && to == LEFT -> Point(p.y, 0)

            else -> throw IllegalArgumentException("How to translate $from to $to")
        }

        return point to when (to) {
            BOTTOM -> Direction('^')
            RIGHT -> Direction('<')
            TOP -> Direction('V')
            else -> Direction('>')
        }
    }

    fun move(from: Point, d: Direction): Pair<Pair<Point, Direction>, Side> {
        val to = d.move(from)
        if (to.y == field.size) {
            val conn = connections[RIGHT]!!
            return translate(from, RIGHT, conn.bound) to conn.side
        }
        if (to.y == -1) {
            val conn = connections[LEFT]!!
            return translate(from, LEFT, conn.bound) to conn.side
        }
        if (to.x == field.size) {
            val conn = connections[BOTTOM]!!
            return translate(from, BOTTOM, conn.bound) to conn.side
        }
        if (to.x == -1) {
            val conn = connections[TOP]!!
            return translate(from, TOP, conn.bound) to conn.side
        }
        if (field[to] == '.') return to to d to this
        return from to d to this
    }
}

fun Field.firstIndexOf(row: Int, chars: Set<Char>): Int = this[row].indexOfFirst { chars.contains(it) }
operator fun Field.get(p: Point): Char = this[p.x][p.y]

val playableChars = setOf('.', '#')

fun Field.move(from: Point, to: Point): Point {
    if (to.x == this.size || to.x < 0 || to.y == this[0].size || to.y < 0 || this[to] == ' ') {
        if (from.x == to.x) {
            val toColumn = if (to.y > from.y) firstIndexOf(
                from.x,
                playableChars
            ) else this[from.x].indexOfLast { playableChars.contains(it) }
            return if (this[from.x][toColumn] == '.') Point(from.x, toColumn) else from
        }
        val range = if (to.x > from.x) 0..from.x else (this.size - 1) downTo from.x
        val toRow = range.first { row -> playableChars.contains(this[row][to.y]) }

        return if (this[toRow][from.y] == '.') Point(toRow, from.y) else from
    }
    if (this[to] == '#') return from
    return to
}


data class Direction(val d: Char) {

    fun move(p: Point): Point {
        return when (d) {
            '>' -> Point(p.x, p.y + 1)
            '<' -> Point(p.x, p.y - 1)
            '^' -> Point(p.x - 1, p.y)
            else -> Point(p.x + 1, p.y)
        }
    }

    fun rotate(action: Action.Rotate): Direction {
        return if (action.clockwise) {
            Direction(
                when (d) {
                    '<' -> '^'
                    '^' -> '>'
                    '>' -> 'V'
                    else -> '<'
                }
            )
        } else {
            Direction(
                when (d) {
                    '>' -> '^'
                    '^' -> '<'
                    '<' -> 'V'
                    else -> '>'
                }
            )
        }
    }

    fun toInt(): Int = when (d) {
        '>' -> 0
        '<' -> 2
        '^' -> 3
        else -> 1
    }

    override fun toString(): String = d.toString()
}

sealed interface Action {
    data class Move(val v: Int) : Action
    data class Rotate(val clockwise: Boolean) : Action
}

private fun actions(line: String): Sequence<Action> = sequence {
    var lastNum = 0
    for (ch in line) {
        if (ch.isDigit()) {
            lastNum = lastNum * 10 + ch.digitToInt()
        } else {
            yield(Action.Move(lastNum))
            lastNum = 0
            yield(Action.Rotate(ch == 'R'))
        }
    }
    if (lastNum != 0) yield(Action.Move(lastNum))
}

private class SideReader(val input: List<String>, val sideLength: Int) {

    fun read(start: Point): Side {
        val result = mutableListOf<CharSequence>()
        for (row in start.x until start.x + sideLength) {
            result.add(input[row].subSequence(start.y, start.y + sideLength))
        }
        return Side(Array(sideLength) { row -> Array(sideLength) { column -> result[row][column] } }, start)
    }
}

fun main() {

    fun part1(input: List<String>): Int {
        visited.clear()
        val blankRow = input.indexOf("")
        val fieldDescription = input.take(blankRow)
        val field = Field(blankRow) { Array(fieldDescription.maxOf { it.length }) { ' ' } }
        fieldDescription.forEachIndexed { row, line ->
            line.forEachIndexed { column, ch -> field[row][column] = ch }
        }

        var direction = Direction('>')
        var position = Point(0, field.firstIndexOf(0, setOf('.')))
        for (action in actions(input.last())) {
            addLastVisited(position, direction)
            if (action is Action.Rotate) {
                direction = direction.rotate(action)
            } else if (action is Action.Move) {
                for (i in 1..action.v) {
                    position = field.move(position, direction.move(position))
                    addLastVisited(position, direction)
                }
            }
        }
//        field.print()

        return (position.x + 1) * 1000 + 4 * (position.y + 1) + direction.toInt()
    }

    fun part2(input: List<String>): Int {
        visited.clear()
        val sideLength = 50
        val sideReader = SideReader(input, sideLength)
        val B = sideReader.read(Point(0, 50))
        val A = sideReader.read(Point(0, 100))
        val C = sideReader.read(Point(50, 50))
        val D = sideReader.read(Point(100, 50))
        val E = sideReader.read(Point(100, 0))
        val F = sideReader.read(Point(150, 0))
        A.connections[LEFT] = Connection(B, RIGHT)
        A.connections[BOTTOM] = Connection(C, RIGHT)
        A.connections[RIGHT] = Connection(D, RIGHT)
        A.connections[TOP] = Connection(F, BOTTOM)

        B.connections[LEFT] = Connection(E, LEFT)
        B.connections[BOTTOM] = Connection(C, TOP)
        B.connections[RIGHT] = Connection(A, LEFT)
        B.connections[TOP] = Connection(F, LEFT)

        C.connections[LEFT] = Connection(E, TOP)
        C.connections[BOTTOM] = Connection(D, TOP)
        C.connections[RIGHT] = Connection(A, BOTTOM)
        C.connections[TOP] = Connection(B, BOTTOM)

        D.connections[LEFT] = Connection(E, RIGHT)
        D.connections[BOTTOM] = Connection(F, RIGHT)
        D.connections[RIGHT] = Connection(A, RIGHT)
        D.connections[TOP] = Connection(C, BOTTOM)

        E.connections[LEFT] = Connection(B, LEFT)
        E.connections[BOTTOM] = Connection(F, TOP)
        E.connections[RIGHT] = Connection(D, LEFT)
        E.connections[TOP] = Connection(C, LEFT)

        F.connections[LEFT] = Connection(B, TOP)
        F.connections[BOTTOM] = Connection(A, TOP)
        F.connections[RIGHT] = Connection(D, BOTTOM)
        F.connections[TOP] = Connection(E, BOTTOM)

        var side = B
        var point = Point(0, 0)
        var direction = Direction('>')
        actionLoop@for (action in actions(input.last())) {

            if (action is Action.Rotate) {
                direction = direction.rotate(action)
            } else if (action is Action.Move) {
                for (i in 1..action.v) {
                    val (pAndDir, newSide) = side.move(point, direction)
                    if (newSide.field[pAndDir.first] == '#') continue@actionLoop
                    side = newSide
                    point = pAndDir.first
                    direction = pAndDir.second
                }
            }
            addLastVisited(point + side.drift, direction)
        }


        val absolutePoint = point + side.drift
        return (absolutePoint.x + 1) * 1000 + 4 * (absolutePoint.y + 1) + direction.toInt()

    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day22_test")
    val part1Answ = part1(testInput)
    println(part1Answ)
    check(part1Answ == 6032)
//    check(part2(testInput) == -1)

    val input = readInput("Day22")
    val part1 = part1(input)
    println(part1)
    check(part1 == 117102)
    //24164 is too low
    //35097 is too low
    //139605 is incorrect
    println(part2(input))
}
