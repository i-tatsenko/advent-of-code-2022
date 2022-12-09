import kotlin.math.max
import kotlin.math.min

class VisibilityUpdater(val field: Array<Array<Int>>, val visibility: Array<Array<Boolean>>, var max: Int) {
    fun update(row: Int, column: Int) {
        if (field[row][column] > max) {
            max = field[row][column]
            visibility[row][column] = true
        }
    }
}

fun main() {

    fun buildField(input: List<String>) =
        Array(input.size) { row -> Array(input[0].length) { column -> input[row][column].digitToInt() } }

    fun part1(input: List<String>): Long {
        val field: Array<Array<Int>> = buildField(input)
        val visibility: Array<Array<Boolean>> = Array(input.size) { row ->
            Array(input[0].length) { column -> row == 0 || row == input.size - 1 || column == 0 || column == input[0].length - 1 }
        }

        val visibilityUpdater = { max: Int -> VisibilityUpdater(field, visibility, max) }

        // ----->
        for (row in 1..field.size - 2) {
            val updater = visibilityUpdater(field[row][0])
            for (column in 1..field[0].size - 2) {
                updater.update(row, column)
            }
        }
        // <-----
        for (row in 1..field.size - 2) {
            val updater = visibilityUpdater(field[row][field[0].size - 1])
            for (column in field[0].size - 2 downTo 1) {
                updater.update(row, column)
            }
        }
        //  |
        //  |
        //  V
        for (column in 1..field[0].size - 2) {
            val updater = visibilityUpdater(field[0][column])
            for (row in 1..field.size - 2) {
                updater.update(row, column)
            }
        }
        //  /\
        //  |
        //  |
        for (column in 1..field[0].size - 2) {
            val updater = visibilityUpdater(field[field.size - 1][column])
            for (row in field.size - 2 downTo 1) {
                updater.update(row, column)
            }
        }



        return visibility.sumOf { it.sumOf { point -> if (point) 1L else 0L } }
    }

    fun part2(input: List<String>): Int {
        val field: Array<Array<Int>> = buildField(input)
        val columns = field[0].size
        val visibility: Array<Array<Int>> = Array(input.size) { row ->
            Array(input[0].length) { column ->
                if (row == 0 || row == field.size - 1 || column == 0 || column == columns - 1) 0 else 1
            }
        }

        // ----->
        for (row in field.indices) {
            val visibilityVector = Array(10) { 0 }
            visibilityVector[field[row][0]] = 0
            for (column in 1 until columns) {
                val height = field[row][column]
                var closestBlock = 0
                for (testHeight in height..9) {
                    closestBlock = max(closestBlock, visibilityVector[testHeight])
                }
                visibility[row][column] *= column - closestBlock
                visibilityVector[height] = column
            }
        }

        for (row in field.indices) {
            val visibilityVector = Array(10) { columns - 1 }
            visibilityVector[field[row][columns - 1]] = columns - 1
            for (column in columns - 1 downTo 0) {
                val height = field[row][column]
                var closestBlock = columns - 1
                for (testHeight in height..9) {
                    closestBlock = min(closestBlock, visibilityVector[testHeight])
                }
                visibility[row][column] *= closestBlock - column
                visibilityVector[height] = column
            }
        }
        //  |
        //  |
        //  V
        for (column in 0 until columns) {
            val visibilityVector = Array(10) { 0 }
            visibilityVector[field[0][column]] = 0
            for (row in 1 until field.size) {
                val height = field[row][column]
                var closestBlock = 0
                for (testHeight in height..9) {
                    closestBlock = max(closestBlock, visibilityVector[testHeight])
                }
                visibility[row][column] *= row - closestBlock
                visibilityVector[height] = row
            }
        }

        for (column in 0 until columns) {
            val visibilityVector = Array(10) { field.size - 1 }
            visibilityVector[field[field.size - 1][column]] = field.size - 1
            for (row in field.size - 1 downTo 0) {
                val height = field[row][column]
                var closestBlock = field.size - 1
                for (testHeight in height..9) {
                    closestBlock = min(closestBlock, visibilityVector[testHeight])
                }
                visibility[row][column] *= closestBlock - row
                visibilityVector[height] = row
            }

        }

        field.forEach { println(it.contentToString()) }
        println()
        println()
        visibility.forEach { println(it.contentToString()) }

        return visibility.maxOf { it.max() }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 21L)
    check(part2(testInput) == 8)

    val input = readInput("Day08")
    println(part1(input))
    check(part1(input) == 1662L)
    println(part2(input))
}
