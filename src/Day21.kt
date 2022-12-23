import java.lang.IllegalArgumentException

private interface Num {
    fun eval(): Long
    fun init(monkeys: Map<String, Num>) {}

    val isHumn: Boolean

    fun balance(value: Long): Long
}

private data class ScalarMonkey(val scalar: Long, override val isHumn: Boolean) : Num {
    override fun eval(): Long = scalar

    override fun balance(value: Long): Long = if (isHumn) value else throw IllegalArgumentException("Humn is in another castle")
}

private data class RefMonkey(private val refs: Pair<String, String>, val operation: String): Num {

    lateinit var leftRef: Num
    lateinit var rightRef: Num

    override fun init(monkeys: Map<String, Num>) {
        leftRef = monkeys[refs.first]!!
        rightRef = monkeys[refs.second]!!
        leftRef.init(monkeys)
        rightRef.init(monkeys)
    }
    override fun eval(): Long {
        val leftArg = leftRef.eval()
        val rightArg = rightRef.eval()
        return when(operation) {
            "*" ->  leftArg * rightArg
            "/" ->  leftArg / rightArg
            "+" ->  leftArg + rightArg
            else -> leftArg - rightArg
        }
    }

    override val isHumn: Boolean
        get() = leftRef.isHumn || rightRef.isHumn

    override fun balance(value: Long): Long {
        val (toBalance, static) = if (leftRef.isHumn) leftRef to rightRef else rightRef to leftRef
        val staticValue = static.eval()
        return toBalance.balance(when(operation) {
            "*" -> value / staticValue
            "/" -> if (leftRef === toBalance) staticValue * value else staticValue / value
            "+" -> value - staticValue
            else -> if ((leftRef === toBalance)) staticValue + value else staticValue - value
        })
    }

}
fun main() {

    val operationRegexp = "^(\\w+) ([+/\\-*]) (\\w+)$".toRegex()
    fun linkMonkeys(input: List<String>): RefMonkey {
        val monkeys = mutableMapOf<String, Num>()
        input.forEach {
            val (monkeyName, monkeyEval) = it.split(": ")
            val refMatch = operationRegexp.matchEntire(monkeyEval)
            if (refMatch == null) {
                monkeys[monkeyName] = ScalarMonkey(monkeyEval.toLong(), monkeyName == "humn")
            } else {
                val (first, operation, second) = refMatch.destructured
                monkeys[monkeyName] = RefMonkey(first to second, operation)
            }
        }
        val root = monkeys["root"]!!
        (root as RefMonkey).init(monkeys)
        return root
    }

    fun part1(input: List<String>): Long {
        val root = linkMonkeys(input)
        return root.eval()
    }

    fun part2(input: List<String>): Long {
        val root = linkMonkeys(input)
        if (root.leftRef.isHumn) return root.leftRef.balance(root.rightRef.eval())
        return root.rightRef.balance(root.leftRef.eval())
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day21_test")
    println(part1(testInput))
    check(part1(testInput) == 152L)
    val part2 = part2(testInput)
    println(part2)
    check(part2 == 301L)

    val input = readInput("Day21")
    println(part1(input))
    check(part1(input) == 309248622142100)
    println(part2(input))
}
