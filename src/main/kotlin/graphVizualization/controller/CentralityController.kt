package graphVizualization.controller

import graphVizualization.model.HarmonicCentrality
import graphVizualization.view.GraphView
import javafx.beans.property.SimpleBooleanProperty
import javafx.concurrent.Task
import javafx.scene.control.ProgressBar
import tornadofx.*

class CentralityController(private val graphView: GraphView) : Controller() {
    private var task: Task<Unit>? = null
    var inProgress = SimpleBooleanProperty(false)

    val toggled: Boolean get() = task != null

    fun toggle() {
        if (toggled) {
            inProgress.value = false
            task!!.cancel()
            task = null
        } else {
            inProgress.value = true
            val harmonicCentrality = HarmonicCentrality(graphView.graph)
            task = runAsync {
                harmonicCentrality.run()
            } success {
                inProgress.value = false
                harmonicCentrality.updateCentrality()
                graphView.vertices.forEach { (vertex, vertexView) ->
                    vertexView.radius = vertex.layoutData.radius
                }
            }
        }
    }
}