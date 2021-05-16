import graphVizualization.model.Edge
import graphVizualization.model.Graph
import graphVizualization.view.GraphView
import graphVizualization.view.Neo4jLoadFragment
import graphVizualization.view.Neo4jSaveFragment
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.testfx.framework.junit5.ApplicationTest
import java.time.Duration.ofMillis

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class Neo4jTest: ApplicationTest() {

    private val responseTime = 10000 // time in millis to interaction with DB

    private val name = "ControlGraph"
    private val graph = Graph.EmptyGraph().apply {
        addVertex(Vertex("Stepan"))
        addVertex(Vertex("Ira"))
        addVertex(Vertex("Maria"))

        val vertices = vertices()

        addEdge(Edge(vertices[0], vertices[1], 1.0))
        addEdge(Edge(vertices[0], vertices[2], 1.5))
    }

    private val graphView: GraphView = GraphView()
    private lateinit var saveFragment: Neo4jSaveFragment
    private lateinit var loadFragment: Neo4jLoadFragment

    override fun start(stage: Stage?) {
        graphView.resetGraph(graph, name)
        saveFragment = Neo4jSaveFragment(graphView.graph, graphView.name)
        loadFragment = Neo4jLoadFragment(graphView)
        stage?.scene = Scene(BorderPane().apply {
            left = saveFragment.root
            right = loadFragment.root
        })
        stage?.show()
    }

    @BeforeEach
    fun clearGraphView() {
        graphView.resetGraph(Graph.EmptyGraph(), "Undefined")
    }

    @Test
    @Order(1)
    fun `Neo4j save test`() {
        graphView.resetGraph(graph, name)
        clickOn("#save_btn")
        assertTimeoutPreemptively(ofMillis(responseTime.toLong())) {
            val label = saveFragment.root.lookup("#status_lbl") as Label
            while (label.text != "Graph saved") Thread.sleep(1)
            assertEquals("Graph saved", label.text)
        }
    }

    @Test
    @Order(2)
    fun `Neo4j load test`() {
        assertTimeoutPreemptively(ofMillis(responseTime.toLong())) {
            val list = lookup(".list-view").query<ListView<String>>()
            while (!list.items.any { it == name }) Thread.sleep(1)
            list.selectionModel.select(name)
        }
        assertTimeoutPreemptively(ofMillis(responseTime.toLong())) {
            clickOn("#select_btn")
            val label = loadFragment.root.lookup("#status_lbl") as Label
            while (label.text != "Loaded") Thread.sleep(1)
            assertEquals("Loaded", label.text)
        }
        val expectedVertices = graph.vertices()
        val actualVertices = graphView.graph.vertices()
        for (vertex in actualVertices) {
            assert(expectedVertices.any { it.compareTo(vertex) == 0 })
        }
        val expectedEdges = graph.edges()
        val actualEdges = graphView.graph.edges()
        for (edge in actualEdges) {
            assert(expectedEdges.any { it.compareTo(edge) == 0 })
        }
        assertEquals(name, graphView.name.value)
    }
}