import java.lang.IllegalArgumentException
import java.util.LinkedList
import java.util.regex.Pattern
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class IntervalComparator : Comparator<Interval> {
    override fun compare(t: Interval, other: Interval): Int {
        val startDiff = t.from - other.from
        if (startDiff != 0) {
            return startDiff
        }
        return t.toIncl - other.toIncl
    }

}

val comparator: Comparator<Interval> = IntervalComparator()

data class Interval(val from: Int, val toIncl: Int) {

    fun toList(): List<Int> {
        return (from..toIncl).toList()
    }

    fun canMerge(other: Interval): Boolean {
        val (left, right) = if (comparator.compare(this, other) < 0) this to other else other to this
        return left.toIncl - right.from == 1 || right.from <= left.toIncl
    }

    fun merge(other: Interval): Interval = Interval(min(from, other.from), max(toIncl, other.toIncl))

    fun contains(x: Int): Boolean = x in from..toIncl

}

fun main() {
    val inputRegexp = Pattern.compile("^Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)$")

    fun parse(input: String): Pair<Point, Point> {
        val matcher = inputRegexp.matcher(input)
        if (!matcher.matches()) {
            throw IllegalArgumentException("Wrong regexp")
        }
        val sensor = Point(matcher.group(1).toInt(), matcher.group(2).toInt())
        val beacon = Point(matcher.group(3).toInt(), matcher.group(4).toInt())
        return sensor to beacon
    }

    fun Point.distance(other: Point): Int = abs(this.x - other.x) + abs(this.y - other.y)

    fun toInterval(sensor: Point, beacon: Point, y: Int): Interval? {
        val dist = sensor.distance(beacon)
        val left = dist - abs(sensor.y - y)
        if (left < 0) return null
        return Interval(sensor.x - left, sensor.x + left)
    }

    fun part1(input: List<String>, row: Int = 10): Int {
        val sensorsAndBeacons = input.map { parse(it) }
        val points =
            sensorsAndBeacons.mapNotNull { toInterval(it.first, it.second, row) }.flatMap { it.toList() }.toSet()
        return points.size - 1
    }

    fun part2(input: List<String>, maxCoord: Int): Long {
        val sensorsAndBeacons = input.map { parse(it) }
        for (y in 1..maxCoord) {
            val intervals =
                sensorsAndBeacons.mapNotNull { toInterval(it.first, it.second, y) }.sortedWith(comparator)
                    .toCollection(LinkedList())
            while (intervals.size != 1) {
                val first = intervals.removeFirst()
                val second = intervals.removeFirst()
                if (!first.canMerge(second)) {
                    return (first.toIncl + 1).toLong() * 4000000L + y.toLong()
                }
                intervals.addFirst(first.merge(second))
            }
        }

        return -1
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    check(part1(testInput) == 26)
    println(part2(testInput, 20))
    check(part2(testInput, 20) == 56000011L)
//    check(part2(testInput) == 2713358)
//your answer is too low
    val input = readInput("Day15")
    println(part1(input, 2000000))
    check(part1(input, 2000000) == 5832528)
    println("started with p2")
    println(part2(input, 4000000))
    check(part2(input, 4000000) == 13360899249595L)
}
