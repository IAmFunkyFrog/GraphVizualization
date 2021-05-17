import graphVizualization.controller.Neo4jSaveLoadController
import graphVizualization.model.DBConnection
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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertTimeoutPreemptively
import org.testfx.framework.junit5.ApplicationTest
import java.time.Duration.ofMillis



class Neo4jTest {

    val responseTime = 5000 // time in millis to interaction with DB

    private val name = "ControlGraph"
    private val graph = Graph.EmptyGraph().apply {
        addVertex(Vertex("Stepan"))
        addVertex(Vertex("Ira"))
        addVertex(Vertex("Maria"))

        val vertices = vertices()

        addEdge(Edge(vertices[0], vertices[1], 1.0))
        addEdge(Edge(vertices[0], vertices[2], 1.5))
    }

    @Nested
    inner class Save: ApplicationTest() {
        private val graphView: GraphView = GraphView()
        private lateinit var saveFragment: Neo4jSaveFragment

        override fun start(stage: Stage?) {
            graphView.resetGraph(graph, name)
            saveFragment = Neo4jSaveFragment(graphView.graph, graphView.name)
            stage?.scene = Scene(BorderPane(saveFragment.root))
            stage?.show()
        }

        @Test
        fun `Neo4j save test`() {
            clickOn("#save_btn")
            assertTimeoutPreemptively(ofMillis(responseTime.toLong())) {
                val label = lookup("#status_lbl").query<Label>()
                while(label.text != "Graph saved") Thread.sleep(1)
                assertEquals("Graph saved", label.text)
            }
        }
    }

    @Nested
    inner class Load: ApplicationTest() {
        private val graphView: GraphView = GraphView()
        private lateinit var loadFragment: Neo4jLoadFragment

        override fun start(stage: Stage?) {
            Neo4jSaveLoadController().saveGraph(graph, name)
            loadFragment = Neo4jLoadFragment(graphView)
            stage?.scene = Scene(BorderPane(loadFragment.root))
            stage?.show()
        }

        @Test
        fun `Neo4j load test`() {
            assertTimeoutPreemptively(ofMillis(responseTime.toLong())) {
                val list = lookup(".list-view").query<ListView<String>>()
                while(!list.items.any { it == name }) Thread.sleep(1)
                list.selectionModel.select(name)
            }
            assertTimeoutPreemptively(ofMillis(responseTime.toLong())) {
                clickOn("#select_btn")
                val label = lookup("#status_lbl").query<Label>()
                while(label.text != "Loaded") Thread.sleep(1)
                assertEquals("Loaded", label.text)
            }
            val expectedVertices = graph.vertices()
            val actualVertices = graphView.graph.vertices()
            for(vertex in actualVertices) {
                assert(expectedVertices.any { it.compareTo(vertex) == 0 })
            }
            val expectedEdges = graph.edges()
            val actualEdges = graphView.graph.edges()
            for(edge in actualEdges) {
                assert(expectedEdges.any { it.compareTo(edge) == 0 })
            }
        }
    }
}