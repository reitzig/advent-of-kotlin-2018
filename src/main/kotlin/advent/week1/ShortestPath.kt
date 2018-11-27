package advent.week1

/**
 * Reads in a maze/labyrinth/map from stdin, and
 * prints it with the shortest path from S to X to stdout.
 */
fun main() {
    val maze = readMaze(generateSequence { readLine() })

    val path = Dijkstra.shortestPath(maze, maze.start, maze.end)

    println(maze + path)
}

interface ShortestPathSolver {
    /**
     * Compute the shortest path from `source` to `target` in `maze`.
     */
    fun shortestPath(maze: Labyrinth, source: Node = maze.start, target: Node = maze.end): List<Node>
}

/**
 * Parses a [Labyrinth] as specified by the
 *   [problem statement](https://blog.kotlin-academy.com/the-advent-of-kotlin-2018-week-1-229e442a143).
 */
fun readMaze(bigString: String): Labyrinth =
        readMaze(bigString.splitToSequence("\n"))

/**
 * Parses a [Labyrinth] as specified by the
 *   [problem statement](https://blog.kotlin-academy.com/the-advent-of-kotlin-2018-week-1-229e442a143).
 */
fun readMaze(rows: Sequence<String>): Labyrinth {
    val grid = rows.map { row ->
        row.map {
            when (it) {
                'S'  -> NodeType.Start
                'X'  -> NodeType.End
                '.'  -> NodeType.Regular
                'B'  -> NodeType.Wall
                else -> throw IllegalArgumentException("Symbol '$it' not allowed")
            }
        }.toTypedArray()
    }.toList().toTypedArray()

    // Validate input requirements, which [Labyrinth] et al. rely on.
    require(grid.isNotEmpty())
    require(grid[0].isNotEmpty())
    require(grid.all { it.size == grid[0].size })
    require(grid.map { row -> row.count { it == NodeType.Start } }.sum() == 1)
    require(grid.map { row -> row.count { it == NodeType.End } }.sum() == 1)

    return Labyrinth(grid)
}

