import java.lang.IllegalArgumentException

val snafuDigits = listOf('=', '-', '0', '1', '2')
fun indexOffset(snufDigit: Char): Int {
    return when (snufDigit) {
        '2', '1', '0' -> snufDigit.digitToInt()
        '-' -> -1
        else -> -2
    }
}

val sumSnafu = { left: String, right: String ->
    var (first, second) = if (left.length > right.length) left to right else right to left
    first = first.reversed()
    second = second.reversed()
    var result = ""
    var correction = 0
    first.forEachIndexed{digitIndex, f ->
        val s = if (digitIndex >= second.length) '0' else second[digitIndex]
        val resultingIndex = snafuDigits.indexOf(f) + indexOffset(s) + correction
        correction = 0
        if (resultingIndex >= 0 && resultingIndex < snafuDigits.size) {
            result += snafuDigits[resultingIndex]
        } else if (resultingIndex >= snafuDigits.size) {
            correction = 1
            result += snafuDigits[resultingIndex % snafuDigits.size]
        } else {
            correction = -1
            result += snafuDigits[snafuDigits.size + resultingIndex]
        }
    }
    if (correction == 1) {
        result += "1"
    } else if (correction == -1) throw IllegalArgumentException("quaquaqua")

    result.reversed()
}

fun main() {

    fun part1(input: List<String>): String {
        return input.foldRight("0", sumSnafu)
    }

    fun part2(input: List<String>): String {

        return "-1"
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day25_test")
    println(part1(testInput))
    check(part1(testInput) == "2=-1=0")
    println(part2(testInput))

    val input = readInput("Day25")
    println(part1(input))
    check(part1(input) == "20-==01-2-=1-2---1-0")
    println(part2(input))
}
