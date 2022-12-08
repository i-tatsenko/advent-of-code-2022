import java.lang.IllegalArgumentException

class CratesState(private val rows: MutableList<MutableList<Char>>) {
    fun move(count: Int, from: Int, to: Int, reverse: Boolean) {
        val buffer = mutableListOf<Char>()
        for (i in 0 until count) {
            buffer.add(rows[from - 1].removeLast())
        }
        rows[to - 1].addAll(if (reverse) buffer.reversed() else buffer)
    }

    fun topCrates(): String = rows.map { it.last() }.joinToString(separator = "")

    companion object {
        fun from(serialized: List<String>): CratesState {
            val rows: MutableList<MutableList<Char>> = mutableListOf()
            for (i in 0 until (serialized[0].length + 1) / 4) {
                rows.add(mutableListOf())
            }

            for (line in serialized) {
                line.chunked(4).forEachIndexed {index: Int, crate: String ->
                    if (crate.isNotBlank()) {
                        rows[index].add(crate[1])
                    }
                }
            }
            return CratesState(rows)
        }
    }
}

fun main() {

    fun part1(input: List<String>, reversed: Boolean = false): String {
        val serializedRows = mutableListOf<String>()
        var index = 0
        while (!input[index].startsWith(" 1")) {
            serializedRows.add(0, input[index++])
        }
        val state = CratesState.from(serializedRows)

        val commandPattern = "^move (\\d+) from (\\d+) to (\\d+)$".toRegex()
        input.subList(index + 2, input.size).forEach {
            val matcher = commandPattern.find(it) ?: throw IllegalArgumentException(it)
            val (count, fromRow, toRow) = matcher.destructured
            state.move(count = count.toInt(), from = fromRow.toInt(), to = toRow.toInt(), reversed)
        }

        return state.topCrates()
    }

    fun part2(input: List<String>): String {
        return part1(input, true)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == "CMZ")
    check(part2(testInput) == "MCD")

    val input = readInput("Day05")
    check(part1(input) == "BZLVHBWQF")
    println(part1(input))
    println(part2(input))
}
