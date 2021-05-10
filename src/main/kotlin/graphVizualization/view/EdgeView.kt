package graphVizualization.view

import graphVizualization.model.Edge
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.shape.Line
import tornadofx.*

class EdgeView(
    val edge: Edge,
    val vertexView1: VertexView,
    val vertexView2: VertexView
) : Line() {

    init {
        startXProperty().bind(vertexView1.centerXProperty())
        startYProperty().bind(vertexView1.centerYProperty())
        endXProperty().bind(vertexView2.centerXProperty())
        endYProperty().bind(vertexView2.centerYProperty())
        strokeWidth = 3.0
    }

    val weightEditor = WeightEditor()

    inner class WeightEditor() : Fragment() {
        override val root: Parent = vbox {
            NumberField(edge.weight) {
                edge.weight = it
            }.also {
                label("Weight") {
                    labelFor = it
                }
                add(it)
            }
            button("Delete") {
                action {

                }
            }
        }
    }
}