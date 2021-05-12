package graphVizualization.controller

import Vertex
import graphVizualization.model.Edge
import graphVizualization.model.Force
import graphVizualization.model.ForceAtlas2
import graphVizualization.view.EdgeView
import graphVizualization.view.GraphView
import graphVizualization.view.VertexView
import javafx.concurrent.ScheduledService
import javafx.concurrent.Task
import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.scene.input.*
import javafx.util.Duration
import tornadofx.Controller
import tornadofx.add

class GraphController(
    private val graphView: GraphView
) : Controller() {

    var forceAtlas2 = ForceAtlas2(graphView)

    private var forceAtlas2Service = ForceAtlas2Service()

    fun cancelForceAtlas2() {
        forceAtlas2Service.cancel()
    }

    fun startForceAtlas2() {
        cancelForceAtlas2()
        forceAtlas2.reset()
        forceAtlas2Service = ForceAtlas2Service()
        forceAtlas2Service.start()
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

    fun onMousePressed(e: MouseEvent, graphView: GraphView) {
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

    var pressedVertex: VertexView? = null

    fun onPressVertex(e: MouseEvent, vertexView: VertexView) {
        if (!e.isSecondaryButtonDown) {
            pressedVertex = null
            return
        }

        if (pressedVertex == null) pressedVertex = vertexView
        else pressedVertex?.let { createEdge(it, vertexView) }
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
        //TODO добавить возможность создавать ребра с определенным весом или хотя бы изменять вес ребра
        val newEdge = Edge(vertexView1.vertex, vertexView2.vertex, 1.0)
        if (graphView.graph.addEdge(newEdge) != null) {
            graphView.edges[newEdge] = EdgeView(newEdge, vertexView1, vertexView2).also {
                graphView.root.add(it)
                graphView.setHandlersOnEdge(it)
                //TODO разобраться с костылем
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
        private lateinit var lastDisplacement: Map<VertexView, Point2D>

        init {
            period = Duration(10.0)
            onSucceeded = EventHandler {
                if (this::lastDisplacement.isInitialized) {
                    for ((vView, displacement) in lastDisplacement) {
                        vView.centerX += displacement.x
                        vView.centerY += displacement.y
                    }
                }
            }
        }

        override fun createTask(): Task<Unit> = IterationTask()

        private inner class IterationTask : Task<Unit>() {
            override fun call() {
                lastDisplacement = forceAtlas2.doIteration()
            }
        }

    }
}