package week1

import java.util.*

/**
 * Compute the shortest path from `source` to `target` in `maze`.
 */
fun shortestPath(maze: Labyrinth, source: Node = maze.start, target: Node = maze.end) : List<Node> {
    require(maze.contains(source) && maze.contains(target))

    // Set up auxiliary data structures
    val distances = HashMap<Node, Double>()
    val queue = PriorityQueue<Node>(kotlin.Comparator { u, v ->
        val distU = distances[u] ?: Double.MAX_VALUE
        val distV = distances[v] ?: Double.MAX_VALUE
        return@Comparator distU.compareTo(distV)
    })
    val predecessors = Array(maze.rows) { Array<Node?>(maze.columns) { null } }

    /**
     * Utilize that node `v` can be reached source `source` via a path
     * through `via` of length `d`.
     */
    fun relax(v: Node, d: Double, via: Node? = null) {
        assert(v == source || via != null)

        if (d < distances.getOrDefault(v, Double.MIN_VALUE)) { // the new path is better¹
            if (queue.remove(v)) { // we haven't reached `v` yet²
                distances[v] = d
                queue.add(v)
                predecessors[v.row][v.column] = via
            }
        }

        // 1. Default applies target nodes we have already removed;
        //    never do anything for those!

        // 2. Technically, the second check is redundant:
        //    if the new distance is smaller, we _can't_ have removed `v`
        //    yet. That's the invariant of Dijkstra's algorithm.
    }

    // Initialize
    with (maze.nodes) {
        forEach {
            distances[it] = Double.MAX_VALUE
        }
        queue.addAll(this)
    }

    // Dijkstra!
    relax(source, 0.0)
    while (queue.isNotEmpty()) {
        val current = queue.poll()

        if (current == target) {
            break
        }

        maze.neighbours(current).forEach {
            relax(it, distances[current]!! + maze.cost(current, it), current)
        }
    }

    // Extract path by backtracking from the target node
    return sequence {
        var current: Node? = target
        while ( current != null) {
            yield(current) // new inference!
            current = predecessors[current.row][current.column]
        }
    }.toList()
}
