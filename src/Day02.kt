enum class Shape(val alias: Set<Char>, val points: Int) {
    Rock(setOf('A', 'X'), 1),
    Paper(setOf('B', 'Y'), 2),
    Scissors(setOf('C', 'Z'), 3);

    fun score(other: Shape): Int {
        val outcome = when (other) {
            this -> 3
            beats[this] -> 6
            else -> 0
        }
        return outcome + points
    }
    companion object {
        val beats = mapOf(
            Rock to Scissors,
            Paper to Rock,
            Scissors to Paper
        )
        val looses = beats.entries.associate { (k, v) -> v to k }
        fun of(ch: Char): Shape = Shape.values().find { it.alias.contains(ch) }!!
    }
}

fun main() {

    fun part1(input: List<String>): Int {
        var result = 0
        for (match in input) {
            val opponent = Shape.of(match[0])
            val me = Shape.of(match[2])
            result += me.score(opponent)
        }
        return result
    }

    fun part2(input: List<String>): Int {
        var result = 0
        for (match in input) {
            val opponent = Shape.of(match[0])
            val outcome = match[2]
            result += when(outcome) {
                'X' -> Shape.beats[opponent]!!.score(opponent)
                'Y' -> opponent.score(opponent)
                else -> Shape.looses[opponent]!!.score(opponent)
            }
        }
        return result
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part2(testInput) == 12)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}
