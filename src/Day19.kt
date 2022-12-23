import ResourceType.*


val inputRegex =
    "^Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.$".toRegex()

data class RobotConfig(
    val oreCost: Int,
    val clayCost: Int,
    val obsCost: Pair<Int, Int>,
    val geodeCost: Pair<Int, Int>
)

data class Resources(val ore: Resource, val clay: Resource, val obs: Resource, val geode: Resource) {

    fun tick() {
        ore.tick()
        clay.tick()
        obs.tick()
        geode.tick()
    }

    fun withOre(ore: Resource): Resources {
        return Resources(
            ore,
            clay.copy(),
            obs.copy(),
            geode.copy()
        )
    }


    fun withOreAndClay(ore: Resource, clay: Resource): Resources = Resources(
        ore,
        clay,
        obs.copy(),
        geode.copy()
    )

    fun copy(): Resources = Resources(ore.copy(), clay.copy(), obs.copy(), geode.copy())

}

enum class ResourceType {
    ORE {
        override fun buildRobot(config: RobotConfig, r: Resources): Resources =
            r.withOre(r.ore.build(config.oreCost))

        override fun canBuild(config: RobotConfig, r: Resources): Boolean {
            return r.ore.value >= config.oreCost
        }
    },
    CLAY {
        override fun buildRobot(config: RobotConfig, r: Resources): Resources =
            r.withOreAndClay(r.ore - config.clayCost, r.clay.build())

        override fun canBuild(config: RobotConfig, r: Resources): Boolean {
            return r.ore.value >= config.clayCost
        }
    },
    OBS {
        override fun buildRobot(config: RobotConfig, r: Resources): Resources =
            Resources(
                r.ore - config.obsCost.first,
                r.clay - config.obsCost.second,
                r.obs.build(),
                r.geode.copy()
            )
        override fun canBuild(config: RobotConfig, r: Resources): Boolean {
            return r.ore.value >= config.obsCost.first && r.clay.value >= config.obsCost.second
        }
        },
    GEODE{
        override fun buildRobot(config: RobotConfig, r: Resources): Resources =
            Resources(
                r.ore - config.geodeCost.first,
                r.clay.copy(),
                r.obs - config.geodeCost.second,
                r.geode.build()
            )

        override fun canBuild(config: RobotConfig, r: Resources): Boolean {
            return r.ore.value >= config.geodeCost.first && r.obs.value >= config.geodeCost.second
        }
    };

    abstract fun buildRobot(config: RobotConfig, r: Resources): Resources
    abstract fun canBuild(config: RobotConfig, r: Resources): Boolean
}

data class Resource(var value: Int = 0, val gain: Int = 0) {
    fun tick() {
        value += gain
    }

    fun hasGain(): Boolean = gain > 0

    fun build(cost: Int = 0) = Resource(value - cost, gain + 1)

    operator fun minus(value: Int): Resource = Resource(this.value - value, gain)

    override fun toString(): String {
        return "$value[$gain]"
    }
}

var cap = 0
fun main() {

    fun maxGeode(time: Int, config: RobotConfig, r: Resources, buildOrder: List<Pair<ResourceType, Int>> = emptyList()): Pair<Int,List<Pair<ResourceType, Int>>>  {
        if (time < 2) {
            if (time == 1) r.tick()
            return r.geode.value to buildOrder
        }
        val capIdle = r.geode.value + r.geode.gain * time
        if (capIdle > cap) {
            cap = capIdle
        }
        if (capIdle + (time * (time - 1)) / 2 < cap) {
            return r.geode.value to buildOrder
        }
        val toBuild = mutableListOf(ORE, CLAY)
        if (r.clay.hasGain()) {
            toBuild.add(OBS)
        }
        if (r.obs.hasGain()) toBuild.add(GEODE)

        var maxGeode: Pair<Int,List<Pair<ResourceType, Int>>> = 0 to emptyList()
        var timeLeft = time
        while (toBuild.isNotEmpty() && timeLeft > 0) {
            val toBuildIterator = toBuild.iterator()
            while(toBuildIterator.hasNext()) {
                val type = toBuildIterator.next()
                if (type.canBuild(config, r)) {
                    if (type == GEODE) {
                        val a = 1
                    }
                    val rCopy = r.copy()
                    rCopy.tick()
                    val withBuilt =
                        maxGeode(timeLeft - 1, config, type.buildRobot(config, rCopy), buildOrder /*+ (type to timeLeft)*/)
                    if (withBuilt.first > maxGeode.first) {
                        maxGeode = withBuilt
                    }
                    toBuildIterator.remove()
                }
            }

            timeLeft--
            r.tick()
        }

        return maxGeode
    }

    fun part1(input: List<String>): Int {
        var output = 0
        input.forEach {
            cap = 0
            val match = inputRegex.matchEntire(it) ?: throw IllegalArgumentException("Invalid regex for input string $it")
            val (number, oreCost, clayCost, obsOreCost, obsClayCost, geodeOreCost, geodeObsCost) = match.destructured
            val config = RobotConfig(
                oreCost.toInt(),
                clayCost.toInt(),
                obsOreCost.toInt() to obsClayCost.toInt(),
                geodeOreCost.toInt() to geodeObsCost.toInt()
            )
            val maxGeode = maxGeode(
                24, config, Resources(
                    Resource(0, 1), Resource(), Resource(),
                    Resource()
                )
            )
            output += number.toInt() * maxGeode.first

        }
        return output
    }

    fun part2(input: List<String>): Int {
        var output = 1
        input.take(if (input.size > 3) 3 else 2).forEach {
            cap = 0
            val match = inputRegex.matchEntire(it) ?: throw IllegalArgumentException("Invalid regex for input string $it")
            val (number, oreCost, clayCost, obsOreCost, obsClayCost, geodeOreCost, geodeObsCost) = match.destructured
            val config = RobotConfig(
                oreCost.toInt(),
                clayCost.toInt(),
                obsOreCost.toInt() to obsClayCost.toInt(),
                geodeOreCost.toInt() to geodeObsCost.toInt()
            )
            val maxGeode = maxGeode(
                32, config, Resources(
                    Resource(0, 1), Resource(), Resource(),
                    Resource()
                )
            )
            println("Build order $maxGeode")
            output *= maxGeode.first

        }
        return output
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day19_test")
    println("test1: " + part1(testInput))
    check(part1(testInput) == 33)
    check(part2(testInput) == 62*56)

    val input = readInput("Day19")
    val part1Result = part1(input)
    println(part1Result)
    check(part1Result == 1650)
    println(part2(input))
}
