import kotlin.math.abs


fun main() {

    data class Node(val value: Long) {
        lateinit var next: Node
        lateinit var prev: Node
        override fun toString(): String = "${prev.value} < $value > ${next.value}"
    }


    val toLeft = {node: Node -> node.prev}
    val toRight = {node: Node -> node.next }

    fun doTheMagic(nodes: Array<Node>, mult: Int = 1, rounds: Int = 1): Long {

        lateinit var zeroNode: Node
        for (i in nodes.indices) {
            val current = nodes[i]
            current.next = if (i == nodes.size - 1) nodes[0] else nodes[i + 1]
            current.prev = if (i == 0) nodes[nodes.size - 1] else nodes[i - 1]
            if (current.value == 0L) zeroNode = current
        }
        val modOfMult = mult % (nodes.size - 1)
        for (r in 1..rounds) {
            for (node in nodes) {
                if (node.value == 0L) {
                    continue
                }
                val direction = if (node.value > 0) toRight else toLeft
                var target = node
                val iterationCount = if (mult == 1) {
                    if (node.value > nodes.size) node.value % (nodes.size - 1) else node.value
                } else {
                    ((node.value % (nodes.size - 1)) * modOfMult) % (nodes.size - 1)
                }

                node.prev.next = node.next
                node.next.prev = node.prev
                for (iteration in 0 until if (node.value < 0) abs(iterationCount) + 1 else iterationCount) {
                    target = direction(target)
                }

                node.next = target.next
                node.prev = target

                target.next = node
                node.next.prev = node
            }
        }

        var node = zeroNode
        var result = 0L
        for (index in 1..3000) {
            node = node.next
            if (index % 1000 == 0) {
                result += node.value
            }
        }
        return result * mult
    }

    fun part1(input: List<String>): Long {

        return doTheMagic(Array(input.size) { Node(input[it].toLong()) })
    }

    fun part2(input: List<String>): Long {
        return doTheMagic(Array(input.size) { Node(input[it].toLong()) }, 811589153, 10)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day20_test")
    check(part1(testInput) == 3L)
    check(part2(testInput) == 1623178306L)

    val input = readInput("Day20")
    println(part1(input))
    check(part1(input) == 3700L)
    check(part2(input) == 10626948369382L)
    println(part2(input))
}
