package advent.week1

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test cases from
 *   [here](https://gist.github.com/MarcinMoskala/2f586da50a93f7954beff616e9207fa8),
 * via the
 *   [problem statement](https://blog.kotlin-academy.com/the-advent-of-kotlin-2018-week-1-229e442a143).
 */
class DijkstraTest {

    fun List<Node>.isPath() : Boolean {
        return mapIndexed { i, node ->
            if (i < size - 1) {
                // Rely on `Node.isAdjacentTo`; correct by inspection.
                node.isAdjacentTo(this[i + 1])
            } else {
                true
            }
        }.reduce(Boolean::and)
    }

    @Test
    fun `Marks start and end as part of way`() {
        val mapString = """
            ....................
            .........XS.........
            ....................
        """.trimIndent()

        val marked = """
            ....................
            .........**.........
            ....................
        """.trimIndent()

        val map = readMaze(mapString)
        val path = Dijkstra.shortestPath(map)

        assertTrue(path.isPath())
        assertEquals(map.start, path.first())
        assertEquals(map.end, path.last())
        assertEquals(marked.count { it == '*' }, path.size)
    }

    @Test
    fun `Straight way is straight`() {
        val mapString = """
            ....................
            .....X..........S...
            ....................
        """.trimIndent()

        val marked = """
            ....................
            .....************...
            ....................
        """.trimIndent()

        val map = readMaze(mapString)
        val path = Dijkstra.shortestPath(map)

        assertTrue(path.isPath())
        assertEquals(map.start, path.first())
        assertEquals(map.end, path.last())
        assertEquals(marked.count { it == '*' }, path.size)
    }

    @Test
    fun `Use cross moves`() {
        val mapString = """
            ...........
            .......S...
            ...........
            ...........
            ...........
            ...........
            ..X........
        """.trimIndent()

        val marked = """
            ...........
            .......*...
            ......*....
            .....*.....
            ....*......
            ...*.......
            ..*........
        """.trimIndent()

        val map = readMaze(mapString)
        val path = Dijkstra.shortestPath(map)

        assertTrue(path.isPath())
        assertEquals(map.start, path.first())
        assertEquals(map.end, path.last())
        assertEquals(marked.count { it == '*' }, path.size)
    }

    @Test
    fun `Mark way around wall`() {
        val mapString = """
            ....................
            ......X...B.........
            ..........B.........
            ........BBB....S....
            ....................
        """.trimIndent()

        val marked = """
            ........***.........
            ......**..B*........
            ..........B.*.......
            ........BBB..***....
            ....................
        """.trimIndent()

        val map = readMaze(mapString)
        val path = Dijkstra.shortestPath(map)

        assertTrue(path.isPath())
        assertEquals(map.start, path.first())
        assertEquals(map.end, path.last())
        assertEquals(marked.count { it == '*' }, path.size)
    }

    @Test
    fun `Mark way around wall 2`() {
        val mapString = """
            ..........B.........
            ......X...B.........
            ..........B.........
            ........BBB....S....
            ....................
        """.trimIndent()

        val marked = """
            ..........B.........
            ......*...B.........
            ......*...B.........
            .......*BBB*****....
            ........***.........
        """.trimIndent()

        val map = readMaze(mapString)
        val path = Dijkstra.shortestPath(map)

        assertTrue(path.isPath())
        assertEquals(map.start, path.first())
        assertEquals(map.end, path.last())
        assertEquals(marked.count { it == '*' }, path.size)
    }

    @Test
    fun `Mark way on labyrinth`() {
        val mapString = """
            BB..B...B...BBBBB...
            ....B.X.BBB.B...B.B.
            ..BBB.B.B.B.B.B.B.B.
            ....B.BBB.B.B.BS..B.
            BBB.B...B.B.BBBBBBB.
            ..B...B.............
        """.trimIndent()

        val marked = """
            BB..B...B...BBBBB.*.
            ....B.*.BBB.B...B*B*
            ..BBB*B.B.B.B.B.B*B*
            ....B*BBB.B.B.B**.B*
            BBB.B.**B.B.BBBBBBB*
            ..B...B.***********.
        """.trimIndent()

        val map = readMaze(mapString)
        val path = Dijkstra.shortestPath(map)

        assertTrue(path.isPath())
        assertEquals(map.start, path.first())
        assertEquals(map.end, path.last())
        assertEquals(marked.count { it == '*' }, path.size)
    }

    @Test
    fun `Navigate on forest`() {
        val mapString = """
            ..B....B..B.....BB.BBB......B..B.......B....BBB.....BB.B....BB.BB..BB.......BBB...BBBBB.....B...B..B
            ....BB.....B.B.B....BBB..........BBB.B....BB..B.B..B...BBBBBBBB..BB.B..........B..B.......B.BB..B..B
            B.......S.B.B.....B.........B.B..B...B...BB.BB.B...B.B.B...B..BB.BB.....BB..B........BB.B..B......BB
            .......B..BBBB.B.....B....B...B.B..B...B...BB.B.BBB..BBB....B......B....B..B.B.BB......BB....BB....B
            ......B.BB.B.B......B..BBBB.B..BBBBB..B.B.BB.BBBBBB.BB.B..B.BBB..B......B......B....B........B.B..B.
            B.....B......BB..BBB..B.B....BB.BBBBB.....BB..B..B..BB..BB..BB.BB......BB....B.BB.B...B..B......B...
            B.B.B.................BB.B.B....BB.....BB.BB...BB.BB..B.B.......B......BB...BB.BB...BB...BB..B...B..
            BB...BB.......BBBB........BBB..BBB.B.B...B..B....B...BBB..........B..B..B..BB..BB...BB..B...BBBBB..B
            ....B..B....B...B.B.B...B.BB........B....BBB..........B...B.BB.B..BB..B.......BB...B.B..B...B..B....
            .........B.....BB.B.....B...BB.B.B.BBBB....B.....B.......BBBBB....B.B............BB.....B........B..
            .B..B.B.B...............B..B.B.....B.BB.BB.B...B.BBB....BB.......BB...B.B.BBBB......B...B.....B.....
            .B..B.....B.B..BB....BB.......B.B.BBB.B.....B..BB.B...B.....B..B...B..B.B..B.BB......B..B..B.....B.B
            .......B.BBB.BB.......B.BBB....B...BB...B..........B.......B...B.....BBBB.B....B..B..BB.B.B.B...B.B.
            B.B..BB.........BB.......BB.....B.BB..BB..BBB..B...BB....B.BBB..B....B.B....B.BBBB..B..BB.B.........
            .B..BB......B...B..BB..BBB.B.B.B.BB.B.B..BBB.BBBB.B..B......BB.....BB.....B....BB....B..BB..........
            B.B..B...B.B.B..B.B..B......B.B.B.BBB.B.B.B....B........BB..B....BB....BBB.BB.B....B...B....BBB.....
            .B..BB...B.....B.B.B.BBB.B.........B...B..B.B...BBBBB....B.BBB..BB...........B.....BBBB....B.....BB.
            .B......BB...BB.......B..B.B....B.BBB...BB.B......B.BBB..B....BB....BB....B.........B.B.B...BB....B.
            B..B...B...B......B.....B......B..B..B....B.B.B.........B.BBB.B..B..BB..B..BB..B.....B..BB.BB.B....B
            B.BB...B..BB..B.BB.B........B.BB......B...B.B..BB.BB.B.B......B...B..B.B...BBB...BBB.........X..B..B
            .B.B.B.....B.BB..BB...BB.....B.BB.....B...BB...BBB..B..BBB..B.B.....B......B.....BB.....B..B.B..B...
            BB...B.B..BB..B.B......B.....B....BBB...BBB.B...B.....B..BBBB.B..B..BBBBBB....BB.BB.B....BB.BB.BBB..
            ..B....B..BB........B..BB..BB..B...B.B..BB.B....BBB..B.....B......B...........BBBB.......B.BB....B..
            BB....BBB...B.B...B.....B.B..B.B....B.B.B...B.B..BBBBBB.B....B...BB..BBB...B.BB....B.........BB..B..
        """.trimIndent()

        val marked = """
            ..B....B..B.....BB.BBB......B..B.......B....BBB.....BB.B....BB.BB..BB.......BBB...BBBBB.....B...B..B
            ....BB.....B.B.B....BBB..........BBB.B....BB..B.B..B...BBBBBBBB..BB.B..........B..B.......B.BB..B..B
            B.......*.B.B.....B.........B.B..B...B...BB.BB.B...B.B.B...B..BB.BB.....BB..B........BB.B..B......BB
            .......B.*BBBB.B.....B....B...B.B..B...B...BB.B.BBB..BBB....B......B....B..B.B.BB......BB....BB....B
            ......B.BB*B.B......B..BBBB.B..BBBBB..B.B.BB.BBBBBB.BB.B..B.BBB..B......B......B....B........B.B..B.
            B.....B....**BB..BBB..B.B....BB.BBBBB.....BB..B..B..BB..BB..BB.BB......BB....B.BB.B...B..B......B...
            B.B.B........*********BB.B.B....BB.....BB.BB...BB.BB..B.B.......B......BB...BB.BB...BB...BB..B...B..
            BB...BB.......BBBB....***.BBB..BBB.B*B...B..B....B...BBB..........B..B..B..BB..BB...BB..B...BBBBB..B
            ....B..B....B...B.B.B...B*BB********B****BBB..........B...B.BB.B..BB..B.......BB...B.B..B...B..B....
            .........B.....BB.B.....B.**BB.B.B.BBBB..*.B.....B.......BBBBB....B.B............BB.....B........B..
            .B..B.B.B...............B..B.B.....B.BB.BB*B...B.BBB....BB.......BB...B.B.BBBB......B...B.....B.....
            .B..B.....B.B..BB....BB.......B.B.BBB.B....*B..BB.B***B....*B..B...B..B.B..B.BB......B..B..B.....B.B
            .......B.BBB.BB.......B.BBB....B...BB...B...*******B..*****B***B.....BBBB.B....B..B..BB.B.B.B...B.B.
            B.B..BB.........BB.......BB.....B.BB..BB..BBB..B...BB....B.BBB.*B....B.B....B.BBBB..B..BB.B.........
            .B..BB......B...B..BB..BBB.B.B.B.BB.B.B..BBB.BBBB.B..B......BB..***BB.....B....BB....B..BB..........
            B.B..B...B.B.B..B.B..B......B.B.B.BBB.B.B.B....B........BB..B....BB****BBB.BB.B....B...B....BBB.....
            .B..BB...B.....B.B.B.BBB.B.........B...B..B.B...BBBBB....B.BBB..BB.....******B.....BBBB....B.....BB.
            .B......BB...BB.......B..B.B....B.BBB...BB.B......B.BBB..B....BB....BB....B..*******B.B.B...BB....B.
            B..B...B...B......B.....B......B..B..B....B.B.B.........B.BBB.B..B..BB..B..BB..B....*B..BB.BB.B....B
            B.BB...B..BB..B.BB.B........B.BB......B...B.B..BB.BB.B.B......B...B..B.B...BBB...BBB.*********..B..B
            .B.B.B.....B.BB..BB...BB.....B.BB.....B...BB...BBB..B..BBB..B.B.....B......B.....BB.....B..B.B..B...
            BB...B.B..BB..B.B......B.....B....BBB...BBB.B...B.....B..BBBB.B..B..BBBBBB....BB.BB.B....BB.BB.BBB..
            ..B....B..BB........B..BB..BB..B...B.B..BB.B....BBB..B.....B......B...........BBBB.......B.BB....B..
            BB....BBB...B.B...B.....B.B..B.B....B.B.B...B.B..BBBBBB.B....B...BB..BBB...B.BB....B.........BB..B..
        """.trimIndent()

        val map = readMaze(mapString)
        val path = Dijkstra.shortestPath(map)

        assertTrue(path.isPath())
        assertEquals(map.start, path.first())
        assertEquals(map.end, path.last())
        assertEquals(marked.count { it == '*' }, path.size)
    }
}