package graphVizualization.controller

import Vertex
import graphVizualization.model.Edge
import graphVizualization.model.Force
import graphVizualization.model.ForceAtlas2
import graphVizualization.view.EdgeView
import graphVizualization.view.GraphView
import graphVizualization.view.VertexView
import javafx.beans.property.SimpleBooleanProperty
import javafx.concurrent.ScheduledService
import javafx.concurrent.Task
import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.scene.input.*
import javafx.util.Duration
import tornadofx.Controller
import tornadofx.add
import kotlin.math.absoluteValue
import kotlin.random.Random

class GraphController(
    private val graphView: GraphView
) : Controller() {

    var forceAtlas2 = ForceAtlas2(graphView.graph)
    var forceAtlas2Running = false
    private set

    private var forceAtlas2Service = ForceAtlas2Service()

    fun cancelForceAtlas2() {
        forceAtlas2Running = false
        forceAtlas2Service.cancel()
    }

    fun startForceAtlas2() {
        cancelForceAtlas2()
        forceAtlas2Running = true
        forceAtlas2.reset(graphView.graph)
        forceAtlas2Service = ForceAtlas2Service()
        forceAtlas2Service.start()
    }

    fun reset() {
        for((v, vView) in graphView.vertices) {
            v.layoutData.delta = Point2D(Random.nextInt().absoluteValue.toDouble() % 100, Random.nextInt().absoluteValue.toDouble() % 100)
            vView.centerX = v.layoutData.delta.x
            vView.centerY = v.layoutData.delta.y
        }
    }

    fun setLinLogAttraction() {
        forceAtlas2.attraction = Force.Factory.LinLogAttraction()
    }

    fun setDistanceAttraction() {
        forceAtlas2.attraction = Force.Factory.DistanceAttraction()
    }

    private var oldMousePos = Point2D(0.0, 0.0)

    fun onScroll(e: ScrollEvent, graphView: GraphView) {
        if (!e.isControlDown) return

        val delta = e.deltaY / 1000
        if (graphView.root.scaleX + delta >= 0) graphView.root.scaleX += delta
        if (graphView.root.scaleY + delta >= 0) graphView.root.scaleY += delta
    }

    fun onMousePressed(e: MouseEvent) {
        if (!e.isControlDown) return

        oldMousePos = Point2D(e.x, e.y)
    }

    fun onMouseDragged(e: MouseEvent, graphView: GraphView) {
        if (!e.isControlDown) return

        val mousePos = Point2D(e.x, e.y)
        graphView.root.translateX += mousePos.x - oldMousePos.x
        graphView.root.translateY += mousePos.y - oldMousePos.y

        oldMousePos = mousePos
    }

    fun createVertex(vertexValue: String) {
        val newVertex = Vertex(vertexValue)
        if (graphView.graph.addVertex(newVertex) != null) {
            graphView.vertices[newVertex] = VertexView(newVertex).also {
                graphView.root.add(it)
                graphView.root.add(it.label)
                graphView.setHandlersOnVertex(it)
            }
        }
    }

    fun createEdge(vertexView1: VertexView, vertexView2: VertexView) {
        if (vertexView1 == vertexView2) return
        val newEdge = Edge(vertexView1.vertex, vertexView2.vertex, 1.0)
        if (graphView.graph.addEdge(newEdge) != null) {
            graphView.edges[newEdge] = EdgeView(newEdge, vertexView1, vertexView2).also {
                graphView.root.add(it)
                graphView.setHandlersOnEdge(it)
                for ((_, vView) in graphView.vertices) vView.toFront()
            }
        }
    }

    fun removeVertex(vertexView: VertexView) {
        graphView.graph.removeVertex(vertexView.vertex)
        graphView.root.children.remove(vertexView)
        graphView.vertices.remove(vertexView.vertex)
    }

    fun removeEdge(edgeView: EdgeView) {
        graphView.graph.removeEdge(edgeView.edge)
        graphView.root.children.remove(edgeView)
        graphView.edges.remove(edgeView.edge)
    }

    private inner class ForceAtlas2Service : ScheduledService<Unit>() {

        init {
            period = Duration(10.0)
            onSucceeded = EventHandler {
                for ((v, vView) in graphView.vertices) {
                    vView.centerX = v.layoutData.delta.x
                    vView.centerY = v.layoutData.delta.y
                }
            }
        }

        override fun createTask(): Task<Unit> = IterationTask()

        private inner class IterationTask : Task<Unit>() {
            override fun call() {
                forceAtlas2.doIteration()
            }
        }

    }
}