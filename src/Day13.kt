import Signal.Scalar
import Signal.Seq
import java.lang.IllegalArgumentException

sealed class Signal {

    fun scalar(): Int = if (this is Scalar) this.value else throw IllegalArgumentException("Can't cast Seq to Scalar")
    fun seq(): Seq = if (this is Seq) this else Seq(listOf(this))
    data class Scalar(val value: Int) : Signal() {
        override fun toString(): String = value.toString()
    }

    data class Seq(val value: List<Signal>) : Signal(), List<Signal> by value {
        override fun toString(): String = value.toString()
    }
}


class PairsIterator(private val input: List<String>) : Iterator<Pair<Seq, Seq>> {

    private var index: Int = 0
    override fun hasNext(): Boolean = index < input.size

    private fun parse(s: String): Seq {
        val result = mutableListOf<Signal>()
        val stack: MutableList<MutableList<Signal>> = mutableListOf(result)
        var lastNum = ""
        for (ch in s.removePrefix("[")) {
            when (ch) {
                '[' -> {
                    val newList = ArrayList<Signal>()
                    stack.last().add(Seq(newList))
                    stack.add(newList)
                }

                ']' -> {
                    if (lastNum.isNotEmpty()) {
                        stack.last().add(Scalar(lastNum.toInt()))
                        lastNum = ""
                    }
                    stack.removeLast()
                }

                ' ' -> {}
                ',' -> {
                    if (lastNum.isNotEmpty()) {
                        stack.last().add(Scalar(lastNum.toInt()))
                        lastNum = ""
                    }
                }

                else -> lastNum += ch
            }
        }
        return Seq(result)
    }

    override fun next(): Pair<Seq, Seq> {
        val result = Pair(parse(input[index++]), parse(input[index++]))
        index++
        return result
    }

}

fun main() {

    fun compare(left: Seq, right: Seq): Int {
        for (index in left.indices) {
            if (right.size <= index) {
                return 1
            }
            val leftItem = left[index]
            val rightItem = right[index]
            val someIsList = (leftItem is Seq) || (rightItem is Seq)
            val compared = if (someIsList) {
                compare(leftItem.seq(), rightItem.seq())
            } else {
                leftItem.scalar() - rightItem.scalar()
            }
            if (compared != 0) {
                return compared
            }
        }
        return if (left.size == right.size) 0 else -1
    }

    fun part1(input: List<String>): Int {
        var indexes = 0
        var index = 1
        for ((left, right) in Iterable { PairsIterator(input) }) {
            val compared = compare(left, right)
            if (compared < 0) indexes += index
            index++
        }
        return indexes
    }

    fun part2(input: List<String>): Int {
        val toAdd2 = Seq(listOf(Seq(listOf(Scalar(2)))))
        val toAdd6 = Seq(listOf(Seq(listOf(Scalar(6)))))
        val seqList: List<Seq> = Iterable { PairsIterator(input) }
            .flatMap { (l, r) -> listOf(l, r) } + listOf(toAdd2, toAdd6)
        val sorted = seqList.sortedWith { l, r -> compare(l, r) }
        return (sorted.indexOf(toAdd2) + 1) * (sorted.indexOf(toAdd6) + 1)
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 140)

    val input = readInput("Day13")
    println(part1(input))
    check(part1(input) == 5806)
    check(part2(input) == 23600)

    println(part2(input))
}
