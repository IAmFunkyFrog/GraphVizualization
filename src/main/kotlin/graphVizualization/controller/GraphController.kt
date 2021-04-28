package graphVizualization.controller

import graphVizualization.model.Graph
import graphVizualization.view.GraphView
import javafx.geometry.Point2D
import javafx.scene.input.*
import tornadofx.Controller

class GraphController<V>(
    private val graph: GraphView<V> = GraphView(Graph.EmptyGraph())
): Controller() {

    private var forceAtlas2: ForceAtlas2 = ForceAtlas2(graph)

    fun resetForceAtlas2() {
        forceAtlas2 = ForceAtlas2(graph)
    }

    fun doForceAtlas2() {
        for(i in 0..10) {
            forceAtlas2.doIteration()
            println("iteration number $i")
        }
    }

    private var oldMousePos = Point2D(0.0, 0.0)

    fun onScroll(e: ScrollEvent, graphView: GraphView<*>) {
        val delta = e.deltaY / 1000
        if(e.isControlDown) {
            if(graphView.scaleX + delta >= 0) graphView.scaleX += delta
            if(graphView.scaleY + delta >= 0) graphView.scaleY += delta
        }
    }

    fun onMousePressed(e: MouseEvent, graphView: GraphView<*>) {
        oldMousePos = graphView.localToParent(e.x, e.y)
    }

    fun onMouseDragged(e: MouseEvent, graphView: GraphView<*>) {
        val mousePos = graphView.localToParent(e.x, e.y)
        graphView.translateX += mousePos.x - oldMousePos.x
        graphView.translateY += mousePos.y - oldMousePos.y

        oldMousePos = mousePos
    }
}