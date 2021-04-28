package graphVizualization.view

import graphVizualization.controller.GraphController
import graphVizualization.model.Graph
import javafx.scene.layout.Pane
import tornadofx.add
import java.lang.IllegalArgumentException

class GraphView<V>(
    private val graph: Graph<V>
) : Pane() {
    //TODO проверить с lazy
    val vertices = graph.vertices().associateWith {
            VertexView(it)
        }
    val edges = graph.edges().associateWith {
            val vertexView1 = vertices[it.vertex1] ?: throw IllegalArgumentException()
            val vertexView2 = vertices[it.vertex2] ?: throw IllegalArgumentException()
            EdgeView(it, vertexView1, vertexView2)
        }
    val controller: GraphController<V> = GraphController(this)

    init {
        vertices.values.forEach { v ->
            add(v)
        }
        edges.values.forEach { e ->
            add(e)
        }
        this.setOnScroll {
            controller.onScroll(it, this)
        }
        this.setOnMousePressed {
            controller.onMousePressed(it, this)
        }
        this.setOnMouseDragged {
            controller.onMouseDragged(it, this)
        }
    }
}