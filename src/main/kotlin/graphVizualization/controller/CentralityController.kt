package graphVizualization.controller

import graphVizualization.model.HarmonicCentrality
import graphVizualization.view.GraphView
import javafx.concurrent.Task
import tornadofx.*

class CentralityController(private val graphView: GraphView) : Controller() {
    private var task: Task<Unit>? = null

    val toggled: Boolean get() = task != null

    fun toggle() {
        if (toggled) {
            task!!.cancel()
            task = null
        } else {
            val harmonicCentrality = HarmonicCentrality(graphView.graph)
            task = runAsync {
                harmonicCentrality.run()
            } success {
                harmonicCentrality.updateCentrality()
                graphView.vertices.forEach { (vertex, vertexView) ->
                    vertexView.radius = vertex.centrality!! * 150 // TODO normalize centrality
                }
            }
        }
    }
}