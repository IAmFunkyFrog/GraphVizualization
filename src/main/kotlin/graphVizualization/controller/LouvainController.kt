package graphVizualization.controller

import graphVizualization.model.Louvain
import graphVizualization.view.GraphView
import javafx.concurrent.Task
import tornadofx.Controller
import tornadofx.success
import java.lang.Thread.sleep

class LouvainController(private val graphView: GraphView) : Controller() {
    private var task: Task<Unit>? = null

    var pauseBetweenIterations = false

    private fun doLouvainIteration(louvain: Louvain) {
        louvain.doIteration()
        for ((vertex, vertexView) in graphView.vertices) {
            vertexView.fill = vertex.layoutData.fill
        }
    }

    fun runLouvain() {
        task?.cancel()
        task = runAsync {
            val louvain = Louvain(graphView.graph)
            do {
                doLouvainIteration(louvain)
                if (pauseBetweenIterations) sleep(500)
            } while (louvain.modularityIsImproved)
        } success {
            task = null
        }
    }
}