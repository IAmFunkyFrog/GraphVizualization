package graphVizualization.view

import graphVizualization.controller.GraphController
import graphVizualization.model.Graph
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.scene.Parent
import tornadofx.*
import java.lang.IllegalArgumentException

class GraphView(
    var name: StringProperty = SimpleStringProperty("Undefined")
) : View() {

    val graph: Graph = Graph.RandomGraph()
    var vertices = graph.vertices().associateWith {
            VertexView(it)
        } as MutableMap
    var edges = graph.edges().associateWith {
            val vertexView1 = vertices[it.vertex1] ?: throw IllegalArgumentException()
            val vertexView2 = vertices[it.vertex2] ?: throw IllegalArgumentException()
            EdgeView(it, vertexView1, vertexView2)
        } as MutableMap
    val controller: GraphController = GraphController(this)

    override val root: Parent = pane {
        edges.values.forEach { e ->
            add(e)
            setHandlersOnEdge(e)
        }
        vertices.values.forEach { v ->
            add(v)
            add(v.label)
            setHandlersOnVertex(v)
        }
    }

    init {
        root.setOnScroll {
            controller.onScroll(it, this)
        }
        root.setOnMousePressed {
            controller.onMousePressed(it, this)
        }
        root.setOnMouseDragged {
            controller.onMouseDragged(it, this)
        }
    }

    fun setHandlersOnVertex(vertexView: VertexView) {
        vertexView.setOnMousePressed {
            controller.onPressVertex(it, vertexView)
        }
        vertexView.setOnMouseDragged {
            vertexView.controller.onDrag(it, vertexView)
        }
        vertexView.setOnMouseEntered {
            vertexView.controller.onMouseEntered(vertexView)
        }
        vertexView.setOnMouseExited {
            vertexView.controller.onMouseExited(vertexView)
        }
    }

    fun setHandlersOnEdge(edgeView: EdgeView) {
        edgeView.setOnMousePressed {
            if(it.isAltDown) openInternalWindow(edgeView.weightEditor)
        }
    }
}