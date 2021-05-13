package graphVizualization.view

import graphVizualization.controller.GraphController
import graphVizualization.model.Graph
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.scene.layout.Pane
import tornadofx.View
import tornadofx.pane
import java.lang.IllegalArgumentException

class GraphView(
    var name: StringProperty = SimpleStringProperty("Undefined")
) : View() {

    var graph: Graph = Graph.ControlGraph(8000, 2)
    var vertices = graph.vertices().associateWith {
        VertexView(it)
    } as MutableMap
    var edges = graph.edges().associateWith {
        val vertexView1 = vertices[it.vertex1] ?: throw IllegalArgumentException()
        val vertexView2 = vertices[it.vertex2] ?: throw IllegalArgumentException()
        EdgeView(it, vertexView1, vertexView2)
    } as MutableMap

    val controller: GraphController = GraphController(this)

    override val root: Pane = pane {
        edges.values.forEach { e ->
            add(e)
            setHandlersOnEdge(e)
        }
        vertices.values.forEach { v ->
            add(v)
            setHandlersOnVertex(v)
        }
        vertices.values.forEach { v ->
            add(v.label)
        }
    }

    fun setHandlersOnVertex(vertexView: VertexView) {
        vertexView.setOnMousePressed {
            if(it.isAltDown) openInternalWindow(vertexView.VertexEditor {
                controller.removeVertex(vertexView)
                edges.values
                    .filter { e -> e.vertexView1 == vertexView || e.vertexView2 == vertexView }
                    .forEach { e -> controller.removeEdge(e) }
            })
            else controller.onPressVertex(it, vertexView)
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
            if(it.isAltDown) openInternalWindow(edgeView.EdgeEditor {
                controller.removeEdge(edgeView)
            })
        }
    }

    fun resetGraph(graph: Graph, name: String) {
        this.graph = graph
        this.name.value = name
        vertices = graph.vertices().associateWith {
            VertexView(it)
        } as MutableMap
        edges = graph.edges().associateWith {
            val vertexView1 = vertices[it.vertex1] ?: throw IllegalArgumentException()
            val vertexView2 = vertices[it.vertex2] ?: throw IllegalArgumentException()
            EdgeView(it, vertexView1, vertexView2)
        } as MutableMap
        //TODO подумать как пофиксить кастыль
        root.children.clear()
        edges.values.forEach { e ->
            root.add(e)
            setHandlersOnEdge(e)
        }
        vertices.values.forEach { v ->
            add(v)
            add(v.label)
            setHandlersOnVertex(v)
        }
    }
}