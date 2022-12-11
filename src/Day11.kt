import java.util.concurrent.atomic.AtomicLong

class MonkeyRouter(rule: String, ifTrue: String, ifFalse: String) {

    val divisible: Long
    private val trueRoute: Int
    private val falseRoute: Int

    init {
        divisible = rule.removePrefix("  Test: divisible by ").toLong()
        trueRoute = ifTrue.removePrefix("    If true: throw to monkey ").toInt()
        falseRoute = ifFalse.removePrefix("    If false: throw to monkey ").toInt()
    }

    fun route(old: Long): Int = if (old % divisible == 0L) trueRoute else falseRoute
}

data class InspectionResult(val worryLevel: Long, val thrown: Int)
class Monkey(startingItems: List<Long>, private val modification: Modification, val router: MonkeyRouter) {
    private val items: MutableList<Long> = ArrayList(startingItems)
    private var inspectionsMade = 0L

    fun inspect(): Iterator<InspectionResult> = object : Iterator<InspectionResult> {
        override fun hasNext(): Boolean = items.isNotEmpty()

        override fun next(): InspectionResult {
            inspectionsMade++
            val worryLevel = modification.apply(items.removeFirst())
            return InspectionResult(worryLevel, router.route(worryLevel))
        }
    }

    fun inspections(): Long = inspectionsMade
    fun catch(worryLevel: Long) = items.add(worryLevel)

    override fun toString(): String {
        return "Monkey: ${inspections()}"
    }
}

fun interface Modification {
    fun apply(x: Long): Long
}

fun interface Operand {
    fun value(old: Long): Long
}

val Old = Operand { it }
val Num: (Long) -> Operand = { value: Long -> Operand { _: Long -> value } }

class Plus(private val operand: Operand) : Modification {
    override fun apply(x: Long): Long = x + operand.value(x)
}

class Minus(private val operand: Operand) : Modification {
    override fun apply(x: Long): Long = x - operand.value(x)
}

class Mult(private val operand: Operand) : Modification {
    override fun apply(x: Long): Long = x * operand.value(x)
}

class Divide(private val operand: Operand) : Modification {
    override fun apply(x: Long): Long = x / operand.value(x)
}


fun main() {

    fun parseMonkeys(
        input: List<String>,
        modificationDecorator: (Modification) -> Modification
    ): List<Monkey> {
        val inputIterator = input.iterator()
        val monkeys = mutableListOf<Monkey>()
        while (inputIterator.hasNext()) {
            inputIterator.next()
            val startingItems = inputIterator.next().removePrefix("  Starting items: ").split(", ").map { it.toLong() }
            val (operator, operand) = inputIterator.next().removePrefix("  Operation: new = old ").split(" ")
            val parsedOperand = if (operand == "old") Old else Num(operand.toLong())
            val operation = when (operator) {
                "+" -> Plus(parsedOperand)
                "-" -> Minus(parsedOperand)
                "*" -> Mult(parsedOperand)
                else -> Divide(parsedOperand)
            }

            val router = MonkeyRouter(inputIterator.next(), inputIterator.next(), inputIterator.next())
            if (inputIterator.hasNext()) inputIterator.next()
            monkeys.add(Monkey(startingItems, modificationDecorator(operation), router))
        }
        return monkeys
    }

    fun part1(input: List<String>): Long {
        val monkeys = parseMonkeys(input) { mod -> Modification { value -> mod.apply(value) / 3 } }

        for (i in 1..20) {
            monkeys.forEach { monkey ->
                monkey.inspect().forEach { (worryLevel, routed) ->
                    monkeys[routed].catch(worryLevel)
                }
            }
        }

        val sorted = monkeys.sortedByDescending { it.inspections() }

        return sorted[0].inspections() * sorted[1].inspections()
    }

    fun part2(input: List<String>): Long {
        val div = AtomicLong(1)
        val monkeys = parseMonkeys(input) { Modification { value -> it.apply(value) % div.toLong() } }
        monkeys.map { it.router.divisible }.reduceRight { v, acc -> v * acc }.also { div.set(it) }

        for (i in 1..10_000) {
            monkeys.forEach { monkey ->
                monkey.inspect().forEach { (worryLevel, routed) ->
                    monkeys[routed].catch(worryLevel)
                }
            }
        }

        val sorted = monkeys.sortedByDescending { it.inspections() }

        return sorted[0].inspections() * sorted[1].inspections()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check(part1(testInput) == 10605L)
    check(part2(testInput) == 2713310158L)

    val input = readInput("Day11")
    check(part1(input) == 67830L)
    println(part1(input))
    println(part2(input))
}
