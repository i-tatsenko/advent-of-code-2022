import java.util.PriorityQueue

class Sums(val input: List<String>) : Iterable<Int> {
    override fun iterator(): Iterator<Int> {
        return object : Iterator<Int> {
            var index = 0
            override fun hasNext(): Boolean = index < input.size

            override fun next(): Int {
                var sum = 0
                while (index < input.size && input[index].isNotBlank()) {
                    sum += Integer.parseInt(input[index++])
                }
                index++
                return sum
            }

        }
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        var max = 0
        for (sum in Sums(input)) {
            max = max.coerceAtLeast(sum)
        }
        return max
    }

    fun part2(input: List<String>): Int {
        val maxes = PriorityQueue<Int>(4)
        for (sum in Sums(input)) {
            maxes.add(sum)
            if (maxes.size > 3) {
                maxes.poll()
            }
        }
        return maxes.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part2(testInput) == 45000)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
