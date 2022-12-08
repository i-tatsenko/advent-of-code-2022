
interface Node {
    val name: String
    fun size(): Long
}

data class File(override val name: String, val size: Long): Node {
    override fun size(): Long = size
}

data class Folder(override val name: String): Node {

    private var children: Map<String, Node> = mutableMapOf()
    private var size: Long? = null
    override fun size(): Long {
        if (size == null) {
            this.size = children.values.sumOf { it.size() }
        }
        return size!!
    }

    fun childFolder(name: String): Folder = children[name] as Folder

    fun initContent(children: List<Node>) {
        this.children = children.associateBy { it.name }
    }

    fun smallFolders(): List<Folder> {
        val result = children.values.filterIsInstance<Folder>().flatMap { it.smallFolders() }
        if (size() <= 100000) {
            return result + this
        }
        return result
    }

    fun findMinimallyLarger(atLeast: Long): Folder? {
        if (this.size() < atLeast) {
            return null
        }
        val folders = children.values.filterIsInstance<Folder>()
            .mapNotNull { it.findMinimallyLarger(atLeast) }
        return if (folders.isEmpty()) this else folders.minBy { it.size() }
    }
}

interface Command {
    val parameter: String?
}

data class Cd(override val parameter: String): Command
data class Ls(private val output: List<String>, override val parameter: String? = null): Command {

    fun content(): List<Node> {
        return output.map {
            if (it.startsWith("dir")) Folder(it.substring("dir ".length))
            else {
                val split = it.split(" ")
                File(split[1], split[0].toLong())
            }
        }
    }
}

fun terminalOutput(output: List<String>): Iterable<Command> = Iterable {TerminalOutputIterator(output)}

class TerminalOutputIterator(private val output: List<String>): Iterator<Command> {

    private var index = 0
    override fun hasNext(): Boolean = index < output.size

    override fun next(): Command {
        val command = output[index++]
        if (command.startsWith("$ cd")) {
            return Cd(command.substring("$ cd ".length))
        }
        val lsOutput = mutableListOf<String>()
        while(index < output.size && !output[index].startsWith("$")) {
            lsOutput.add(output[index++])
        }
        return Ls(lsOutput)
    }
}

fun main() {

    fun restoreFs(input: List<String>): Folder {
        val stack = mutableListOf<Folder>()
        for (command in terminalOutput(input)) {
            when(command) {
                is Cd -> when (command.parameter) {
                    "/" -> stack.add(Folder("/"))
                    ".." -> stack.removeLast()
                    else -> stack.add(stack.last().childFolder(command.parameter))
                }
                is Ls -> stack.last().initContent(command.content())
            }
        }
        return stack.first()
    }

    fun part1(input: List<String>): Long {
        return restoreFs(input).smallFolders().sumOf { it.size() }
    }

    fun part2(input: List<String>): Long {
        val root = restoreFs(input)
        val freeSpace = 70000000 - root.size()
        val required = 30000000 - freeSpace
        val folder: Folder = root.findMinimallyLarger(required)!!
        return folder.size()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 95437L)
    check(part2(testInput) == 24933642L)

    val input = readInput("Day07")
    check(part1(input) == 1783610L)
    println(part1(input))
    println(part2(input))
}
