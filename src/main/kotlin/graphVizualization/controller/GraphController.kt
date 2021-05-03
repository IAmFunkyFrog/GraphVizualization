package graphVizualization.controller

import graphVizualization.model.Graph
import graphVizualization.view.GraphView
import graphVizualization.view.VertexView
import javafx.concurrent.ScheduledService
import javafx.concurrent.Task
import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.scene.input.*
import javafx.util.Duration
import tornadofx.Controller

class ForceAtlas2Service(
    var forceAtlas2: ForceAtlas2
): ScheduledService<Unit>() {
    private lateinit var lastDisplacement: Map<VertexView, Point2D>

    init {
        period = Duration(10.0)
        onSucceeded = EventHandler {
            if(this::lastDisplacement.isInitialized) {
                for((vView, displacement) in lastDisplacement) {
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

class GraphController(
    private val graph: GraphView = GraphView(Graph.EmptyGraph())
): Controller() {

    var forceAtlas2 = ForceAtlas2(graph)
    var forceAtlas2Service: ForceAtlas2Service = ForceAtlas2Service(forceAtlas2)

    fun resetForceAtlas2() {
        forceAtlas2 = ForceAtlas2(graph)
    }

    fun cancelForceAtlas2() {
        forceAtlas2Service.cancel()
    }

    fun startForceAtlas2() {
        forceAtlas2Service = ForceAtlas2Service(forceAtlas2)
        forceAtlas2Service.start()
    }

    private var oldMousePos = Point2D(0.0, 0.0)

    fun onScroll(e: ScrollEvent, graphView: GraphView) {
        if(!e.isControlDown) return

        val delta = e.deltaY / 1000
        if(graphView.scaleX + delta >= 0) graphView.scaleX += delta
        if(graphView.scaleY + delta >= 0) graphView.scaleY += delta
    }

    fun onMousePressed(e: MouseEvent, graphView: GraphView) {
        if(!e.isControlDown) return

        oldMousePos = graphView.localToParent(e.x, e.y)
    }

    fun onMouseDragged(e: MouseEvent, graphView: GraphView) {
        if(!e.isControlDown) return

        val mousePos = graphView.localToParent(e.x, e.y)
        graphView.translateX += mousePos.x - oldMousePos.x
        graphView.translateY += mousePos.y - oldMousePos.y

        oldMousePos = mousePos
    }
}