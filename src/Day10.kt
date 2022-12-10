import kotlin.math.abs

fun interface RegisterCmd {

    fun apply(x: Int): Int
}

class Add(private val value: Int): RegisterCmd {
    override fun apply(x: Int): Int = x + value
}

val Noop = RegisterCmd {it}
class CommandIterator(private val commands: List<String>): Iterator<RegisterCmd> {
    private var index = 0
    private var evaluatedAdd = false
    override fun hasNext(): Boolean = index < commands.size

    override fun next(): RegisterCmd {
        if (commands[index] == "noop") {
            index++
            return Noop
        }
        if (!evaluatedAdd) {
            evaluatedAdd = true
            return Noop
        }
        evaluatedAdd = false
        return Add(commands[index++].substring("addx ".length).toInt())
    }
}
fun main() {

    fun part1(input: List<String>): Int {
        val commandIterator = CommandIterator(input)
        var register = 1
        var strength = 0
        loop@ for (cycle in 1..220) {
            if ((cycle - 20) % 40 == 0) {
                strength += cycle * register
            }
            register = commandIterator.next().apply(register)
        }
        return strength
    }

    fun part2(input: List<String>): String {
        val result = mutableListOf<Char>()
        var register = 1
        val commandIterator = CommandIterator(input)
        for (pixel in 0 until 240) {
            result.add(if (abs((pixel % 40) - register) < 2) '#' else '.')
            register = commandIterator.next().apply(register)
        }

        return result.chunked(40).joinToString("\n") { it.joinToString("") }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    check(part1(testInput) == 13140)
    println(part2(testInput))

    val input = readInput("Day10")
    println(part1(input))
    println(part2(input))
}
