package graphVizualization.view

import graphVizualization.controller.GraphController
import graphVizualization.model.Graph
import javafx.scene.layout.Pane
import tornadofx.add
import java.lang.IllegalArgumentException

class GraphView(
    val graph: Graph
) : Pane() {
    //TODO проверить с lazy
    var vertices = graph.vertices().associateWith {
            VertexView(it)
        } as MutableMap
    var edges = graph.edges().associateWith {
            val vertexView1 = vertices[it.vertex1] ?: throw IllegalArgumentException()
            val vertexView2 = vertices[it.vertex2] ?: throw IllegalArgumentException()
            EdgeView(it, vertexView1, vertexView2)
        } as MutableMap
    val controller: GraphController = GraphController(this)

    init {
        edges.values.forEach { e ->
            add(e)
        }
        vertices.values.forEach { v ->
            add(v)
            add(v.label)
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