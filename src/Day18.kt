data class Point3D(val x: Int, val y: Int, val z: Int) {
    constructor(csv: String) : this(csv.split(",").map { it.toInt() })
    constructor(arr: List<Int>) : this(arr[0], arr[1], arr[2])
}

typealias Surface = Pair<Point3D, Point3D>
typealias Edge = Pair<Point3D, Point3D>

class Cube(val p: Point3D) {
    val surfaces: Set<Surface>
    val edges: Set<Edge>

    constructor(input: String) : this(Point3D(input))
    constructor(x: Int, y: Int, z: Int) : this(Point3D(x, y, z))

    init {
        val diagonalPoint = Point3D(p.x + 1, p.y + 1, p.z + 1)

        val surfs = mutableSetOf<Surface>()
        surfs.add(p to Point3D(p.x + 1, p.y + 1, p.z))
        surfs.add(Point3D(p.x, p.y, p.z + 1) to diagonalPoint)

        surfs.add(p to Point3D(p.x, p.y + 1, p.z + 1))
        surfs.add(Point3D(p.x + 1, p.y, p.z) to diagonalPoint)

        surfs.add(p to Point3D(p.x + 1, p.y, p.z + 1))
        surfs.add(Point3D(p.x, p.y + 1, p.z) to diagonalPoint)
        surfaces = surfs

        val edges = mutableSetOf<Edge>()
        edges.add(p to Point3D(p.x + 1, p.y, p.z))
        edges.add(p to Point3D(p.x, p.y + 1, p.z))
        edges.add(p to Point3D(p.x, p.y, p.z + 1))

        edges.add(Point3D(diagonalPoint.x - 1, diagonalPoint.y, diagonalPoint.z) to diagonalPoint)
        edges.add(Point3D(diagonalPoint.x, diagonalPoint.y - 1, diagonalPoint.z) to diagonalPoint)
        edges.add(Point3D(diagonalPoint.x, diagonalPoint.y, diagonalPoint.z - 1) to diagonalPoint)
        this.edges = edges
    }

    fun touches(other: Cube): Boolean = surfaces.intersect(other.surfaces).isNotEmpty()

    override fun equals(other: Any?): Boolean {
        if (other !is Cube) {
            return false
        }
        return p == other.p
    }

    override fun hashCode(): Int {
        return p.hashCode()
    }
}

fun main() {

    fun part1(input: List<String>): Int {
        return input.map { Cube(it) }
            .flatMap { it.surfaces }
            .groupBy { it }
            .count { it.value.size == 1 }

    }

    fun part2(input: List<String>): Int {
        val cubes = input.mapTo(HashSet()) { Cube(it) }
        val gazMinX = cubes.minOf { it.p.x } - 1
        val gazMinY = cubes.minOf { it.p.y } - 1
        val gazMinZ = cubes.minOf { it.p.z } - 1
        val gazMaxX = cubes.maxOf { it.p.x } + 1
        val gazMaxY = cubes.maxOf { it.p.y } + 1
        val gazMaxZ = cubes.maxOf { it.p.z } + 1
        val gaz = mutableSetOf<Cube>()
        for (x in gazMinX..gazMaxX) {
            for (y in gazMinY..gazMaxY) {
                for (z in gazMinZ..gazMaxZ) {
                    val gazCube = Cube(x, y, z)
                    if (!cubes.contains(gazCube)) {
                        gaz.add(gazCube)
                    }
                }
            }
        }
        val startingBurn = Cube(gazMinX, gazMinY, gazMinZ)
        var toBurn = mutableSetOf(startingBurn)
        while (toBurn.isNotEmpty()) {
            gaz.removeAll(toBurn)
            toBurn = toBurn.flatMapTo(HashSet()) { burnedGaz -> gaz.filter { it.touches(burnedGaz) } }
        }

        val edges = cubes.flatMap { it.surfaces }
            .groupByTo(HashMap()) { it }

        gaz.forEach{it.surfaces.forEach {surf -> edges.remove(surf)}}

        return edges.count { it.value.size == 1 }
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day18_test")
    println(part1(testInput))
    check(part1(testInput) == 64)
    val part2 = part2(testInput)
    println(part2)
    check(part2 == 58)

    val input = readInput("Day18")
    println(part1(input))
    check(part1(input) == 4192)
    println(part2(input))
}
