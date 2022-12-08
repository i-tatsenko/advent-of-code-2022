class Range(serialized: String) {
    private val start: Int
    private val end: Int

    init {
        val split = serialized.split('-')
        start = split[0].toInt()
        end = split[1].toInt()
    }

    fun contains(other: Range): Boolean = start <= other.start && end >= other.end

    fun overlaps(other: Range): Boolean {
        return if (start < other.start) end >= other.start else other.end >= start
    }
}

fun main() {

    fun part1(input: List<String>): Int {
        var result = 0
        for (line in input) {
            val split = line.split(',')
            val first = Range(split[0])
            val second = Range(split[1])
            if (first.contains(second) || second.contains(first)) result++
        }
        return result
    }

    fun part2(input: List<String>): Int {
        var result = 0
        for (line in input) {
            val split = line.split(',')
            val first = Range(split[0])
            val second = Range(split[1])
            if (first.overlaps(second)) result++
        }
        return result
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part2(testInput) == 4)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}
