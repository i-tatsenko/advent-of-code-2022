import java.lang.IllegalArgumentException

fun main() {

    fun findPacketIndex(line: String, distinctChars: Int): Int {
        val inSubstr = mutableMapOf<Char, Int>()
        for (index in line.indices) {
            if (index > distinctChars - 1) {
                inSubstr.compute(line[index - distinctChars]) { _, current -> if (current!! - 1 == 0) null else current - 1 }
            }
            inSubstr.compute(line[index]) { _, current -> (current ?: 0) + 1 }
            if (inSubstr.size == distinctChars) {
                return index + 1
            }
        }
        throw IllegalArgumentException()
    }

    fun part1(input: List<String>): Int {
        val line = input[0]
        return findPacketIndex(line, 4)
    }

    fun part2(input: List<String>): Int {
        val line = input[0]
        return findPacketIndex(line, 14)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(part1(testInput) == 5)
    check(part2(testInput) == 23)

    val input = readInput("Day06")
    println(part1(input))
    println(part2(input))
}

