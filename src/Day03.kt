fun Char.priority(): Int = if (code > 96) code - 96 else code - 64 + 26
fun main() {

    fun part1(input: List<String>): Int {
        var result = 0
        for (line in input) {
            val firstCompartment = IntArray(53)
            for (i in 0 until line.length / 2) {
                firstCompartment[line[i].priority()] = 1
            }
            for (i in line.length / 2 until line.length) {
                if (firstCompartment[line[i].priority()] == 1) {
                    result += line[i].priority()
                    break
                }
            }
        }
        return result
    }

    fun part2(input: List<String>): Int {
        var result = 0
        lateinit var items: IntArray
        for ((index, line) in input.withIndex()) {
            if (index % 3 == 0) items = IntArray(53)
            val expected = index % 3
            for (i in line.indices) {
                val current = items[line[i].priority()]
                if (current == expected) {
                    if (expected == 2) {
                        result += line[i].priority()
                        break
                    } else {
                        items[line[i].priority()] = expected + 1
                    }
                }
            }
        }
        return result
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part2(testInput) == 70)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
