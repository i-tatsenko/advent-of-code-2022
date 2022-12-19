import java.lang.IllegalArgumentException

data class Valve(val name: String, val flowRate: Int, val connectionNames: List<String>)

fun buildSystem(valves: List<Valve>): ValveSystem {
    val valvesByName = valves.associateByTo(mutableMapOf()) { it.name }
    val toOpen = valves.filter { it.flowRate > 0 }.toMutableSet()
    val indexByName: Map<String, Int> = valves.foldIndexed(mutableMapOf()) { index, map, valve ->
        map[valve.name] = index
        map
    }

    val distances = Array(valves.size) { Array(valves.size) { 100_000 } }
    valves.forEachIndexed { valveIndex, valve ->
        valve.connectionNames.forEach { connection ->
            distances[valveIndex][indexByName[connection]!!] = 1
        }
    }
    for (k in valves.indices) {
        for (i in valves.indices) {
            for (j in valves.indices) {
                val proposed = distances[i][k] + distances[k][j]
                if (distances[i][j] > proposed) {
                    distances[i][j] = proposed
                }
            }
        }
    }
    return ValveSystem(toOpen, valvesByName, distances, indexByName)
}

class ValveSystem(
    val toOpen: MutableSet<Valve>,
    private val valvesByName: MutableMap<String, Valve>,
    private val distances: Array<Array<Int>>,
    private val indexByName: Map<String, Int>
) {

    private fun index(valve: Valve) = indexByName[valve.name]!!

    fun dist(from: Valve, to: Valve) = distances[index(from)][index(to)]

    fun targets(from: Valve): List<Valve> = toOpen.filter { it !== from }

    operator fun get(key: String): Valve = valvesByName[key]!!

    fun open(valve: Valve) {
        toOpen.remove(valve)
    }

    fun close(valve: Valve) = toOpen.add(valve)

}


fun main() {

    val parserRegexp =
        "^Valve (\\w\\w) has flow rate=(\\d+); tunnels? leads? to valves? ((?:\\w\\w(?:,\\s)?)+)$".toRegex()

    fun parse(input: String): Valve {
        val matcher =
            parserRegexp.matchEntire(input) ?: throw IllegalArgumentException("Invalid regexp for input string $input")
        val groups = matcher.groupValues
        val valveName = groups[1]
        val flowRate = groups[2].toInt()
        val connections = groups[3].split(", ")
        return Valve(valveName, flowRate, connections)
    }

    fun calcPressure(
        valve: Valve,
        timeLeft: Int,
        system: ValveSystem,
    ): Int {
        if (timeLeft < 2 || system.toOpen.isEmpty()) {
            return 0
        }
        var time = timeLeft
        if (valve.flowRate > 0) {
            system.open(valve)
            time--
        }

        val targets = system.targets(valve)
        if (targets.isEmpty()) {
            return valve.flowRate * time
        }
        val flow = targets.maxOf { calcPressure(it, time - system.dist(valve, it), system) }
        system.close(valve)


        return valve.flowRate * time + flow
    }

    val memo = mutableMapOf<String, Int>()
    fun calcPressureMemo(valve: Valve,
                         timeLeft: Int,
                         system: ValveSystem): Int {
        val key = valve.name + timeLeft + system.toOpen.asSequence().map { it.name }.sorted().joinToString("")
        return memo[key] ?: calcPressure(valve, timeLeft, system).also { memo[key] = it }
    }

    fun calcPressureWithBuddy(
        valve: Valve,
        timeLeft: Int,
        system: ValveSystem,
    ): Int {
        if (timeLeft < 2 || system.toOpen.isEmpty()) {
            return 0
        }
        var time = timeLeft
        if (valve.flowRate > 0) {
            system.open(valve)
            time--
        }

        val targets = system.targets(valve)
        if (targets.isEmpty()) {
            return valve.flowRate * time
        }
        val flow = targets
            .maxOf { calcPressureWithBuddy(it, time - system.dist(valve, it), system) }

        val buddyFlow = calcPressureMemo(system["AA"], 26, system)
        system.close(valve)

        val maxFlow = if (flow > buddyFlow) flow else buddyFlow

        return valve.flowRate * time + maxFlow
    }

    fun part1(input: List<String>, iterations: Int = 30): Int {
        val valves = input.map { parse(it) }
        val system = buildSystem(valves)
        return calcPressure(system["AA"], iterations, system)
    }

    fun part2(input: List<String>): Int {
        val valves = input.map { parse(it) }
        val system = buildSystem(valves)
        return calcPressureWithBuddy(system["AA"], 26, system)
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day16_test")
    check(part1(testInput) == 1651)
    check(part2(testInput) == 1707)

    val input = readInput("Day16")
    check(part1(input) == 2320)
    println("checks passed")
    println(part2(input))
    check(part2(input) == 2967)
}
