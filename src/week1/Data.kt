package week1

import java.lang.IllegalArgumentException
import java.lang.StringBuilder
import kotlin.math.abs
import kotlin.math.max

/**
 * A maze to find our way through.
 *
 * NB: Requirements checked in parsing function since intializers of inline classes can't do it. Yet.
 */
inline class Labyrinth(private val grid: Array<Array<NodeType>>) {
    val rows: Int
        get() = grid.size
    val columns: Int
        get() = grid[0].size

    val start: Node
        get() =
            (0 until rows).flatMap { r ->
                (0 until columns).map { c ->
                    Node(Pair(r, c))
                }
            }.find { typeAt(it) == NodeType.Start } ?: throw IllegalArgumentException("Maze without start!")

    val end: Node
        get() =
            (0 until rows).flatMap { r ->
                (0 until columns).map { c ->
                    Node(Pair(r, c))
                }
            }.find { typeAt(it) == NodeType.End } ?: throw IllegalArgumentException("Maze without end!")

    /**
     * All nodes in this maze that can be moved to.
     */
    val nodes: Collection<Node>
        get() = (0 until rows).flatMap { r ->
            (0 until columns).map { c ->
                Node(Pair(r, c))
            }
        }.filter { typeAt(it) != NodeType.Wall }

    fun contains(v: Node): Boolean =
        v.row in 0..(rows - 1) && v.column in 0..(columns - 1)

    fun typeAt(v: Node): NodeType =
        grid[v.row][v.column]

    /**
     * Returns all nodes adjacent to `v` that can be moved to, i.e.
     * that are part of this maze _and_ are not walls.
     */
    fun neighbours(v: Node): Set<Node> {
        return sequenceOf(
            Pair(v.row - 1, v.column - 1),
            Pair(v.row - 1, v.column),
            Pair(v.row - 1, v.column + 1),
            Pair(v.row, v.column - 1),
            // Pair(v.row, v.column), <-- that's v!
            Pair(v.row, v.column + 1),
            Pair(v.row + 1, v.column - 1),
            Pair(v.row + 1, v.column),
            Pair(v.row + 1, v.column + 1)
        ).map { Node(it) }.filter { contains(it) && typeAt(it) != NodeType.Wall }.toSet()
    }

    /**
     * The cost of moving from `u` to `v`.
     */
    fun cost(u: Node, v: Node): Double {
        require(contains(u) && contains(v))

        return if (typeAt(u) == NodeType.Wall || typeAt(v) == NodeType.Wall) {
            Double.MAX_VALUE
        } else if (v.isAdjacentTo(u)) {
            if (u.row == v.row || u.column == v.column) {
                1.0
            } else {
                1.5
            }
        } else {
            Double.MAX_VALUE
        }
    }

    override fun toString(): String =
        grid.joinToString("\n") { row ->
            row.joinToString("") { it.toString() }
        }

    operator fun plus(path: List<Node>): MazeWithPath =
        MazeWithPath(Pair(this, path))
}

/**
 * Intermediate type to make tiny DSL `maze + path` for pretty-printing work.
 */
inline class MazeWithPath /* internal constructor */(private val mazeAndPath: Pair<Labyrinth, Collection<Node>>) {
    private val map: Labyrinth
        get() = mazeAndPath.first
    private val path: Collection<Node>
        get() = mazeAndPath.second

    override fun toString(): String {
        val output = StringBuilder(map.toString())
        path.forEach {
            output[it.row * (map.columns + 1) + it.column] = '*'
        }
        return output.toString()
    }
}

/**
 * A single (abstract) node in a [Labyrinth], identified by its coordinates.
 */
inline class Node(private val coordinates: Pair<Int, Int>) {
    val row: Int
        get() = coordinates.first
    val column: Int
        get() = coordinates.second

    fun isAdjacentTo(other: Node) =
        max(abs(other.column - this.column), abs(other.row - this.row)) == 1
}

/**
 * The different types of nodes that we see in [Labyrinth]s.
 */
enum class NodeType {
    Regular,
    Start,
    End,
    Wall;

    override fun toString(): String =
        when (this) {
            NodeType.Start -> "S"
            NodeType.End -> "X"
            NodeType.Regular -> "."
            NodeType.Wall -> "B"
        }
}
